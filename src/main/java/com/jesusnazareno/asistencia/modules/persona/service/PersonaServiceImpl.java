package com.jesusnazareno.asistencia.modules.persona.service;

import com.jesusnazareno.asistencia.common.validation.DniPeruanoValidator;
import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.mapper.PersonaMapper;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Implementación de la capa de servicio para Persona.
 */
@Service
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    private final DniPeruanoValidator dniValidator;

    public PersonaServiceImpl(PersonaRepository personaRepository, PersonaMapper personaMapper) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
        this.dniValidator = new DniPeruanoValidator();
    }

    @Override
    @Transactional
    public PersonaResponse crearPersona(PersonaRequest request) {
        validarReglasNegocio(request, null);

        Persona persona = personaMapper.toEntity(request);
        Persona guardada = personaRepository.save(persona);
        return personaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaResponse buscarPorId(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));
        return personaMapper.toResponse(persona);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaResponse buscarPorNumeroDocumento(String numeroDocumento) {
        Persona persona = personaRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con documento: " + numeroDocumento));
        return personaMapper.toResponse(persona);
    }

    @Override
    @Transactional
    public PersonaResponse actualizarPersona(Long id, PersonaRequest request) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));

        validarReglasNegocio(request, id);

        personaMapper.updateEntity(request, persona);
        Persona actualizada = personaRepository.save(persona);
        return personaMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public void desactivarPersona(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));
        persona.setActivo(false);
        personaRepository.save(persona);
    }

    private void validarReglasNegocio(PersonaRequest request, Long personaIdActual) {
        // Validación de DNI peruano condicional si tipoDocumento es DNI
        if (request.getTipoDocumento() == TipoDocumento.DNI) {
            if (!dniValidator.isValid(request.getNumeroDocumento(), null)) {
                throw new ValidationException(
                        "Los datos ingresados no son válidos.",
                        Map.of("numeroDocumento", "El DNI debe contener exactamente 8 dígitos numéricos.")
                );
            }
        }

        // Validación de duplicidad de número de documento
        personaRepository.findByNumeroDocumento(request.getNumeroDocumento())
                .ifPresent(existente -> {
                    if (personaIdActual == null || !existente.getId().equals(personaIdActual)) {
                        throw new BusinessException("Ya existe una persona con este número de documento.");
                    }
                });
    }
}
