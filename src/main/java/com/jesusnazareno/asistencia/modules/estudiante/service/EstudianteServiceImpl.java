package com.jesusnazareno.asistencia.modules.estudiante.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import com.jesusnazareno.asistencia.modules.persona.domain.TipoActor;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.mapper.PersonaMapper;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.TipoActorRepository;
import com.jesusnazareno.asistencia.modules.persona.service.PersonaService;
import com.jesusnazareno.asistencia.modules.persona.service.PersonaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para la gestión y consulta de estudiantes.
 */
@Service
public class EstudianteServiceImpl implements EstudianteService {

    public static final String CODIGO_ACTOR_ESTUDIANTE = "ESTUDIANTE";

    private final PersonaRepository personaRepository;
    private final PersonaActorRepository personaActorRepository;
    private final TipoActorRepository tipoActorRepository;
    private final PersonaMapper personaMapper;
    private final PersonaService personaService;

    @Autowired
    public EstudianteServiceImpl(PersonaRepository personaRepository,
                                 PersonaActorRepository personaActorRepository,
                                 TipoActorRepository tipoActorRepository,
                                 PersonaMapper personaMapper,
                                 PersonaService personaService) {
        this.personaRepository = personaRepository;
        this.personaActorRepository = personaActorRepository;
        this.tipoActorRepository = tipoActorRepository;
        this.personaMapper = personaMapper;
        this.personaService = personaService != null ? personaService : new PersonaServiceImpl(personaRepository, personaMapper);
    }

    public EstudianteServiceImpl(PersonaRepository personaRepository,
                                 PersonaActorRepository personaActorRepository,
                                 TipoActorRepository tipoActorRepository,
                                 PersonaMapper personaMapper) {
        this(personaRepository, personaActorRepository, tipoActorRepository, personaMapper, null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esEstudiante(Long personaId) {
        if (personaId == null) {
            return false;
        }
        return personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(personaId, CODIGO_ACTOR_ESTUDIANTE);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaResponse obtenerEstudiantePorId(Long personaId) {
        Persona persona = obtenerEntidadEstudiantePorId(personaId);
        return personaMapper.toResponse(persona);
    }

    @Override
    @Transactional(readOnly = true)
    public Persona obtenerEntidadEstudiantePorId(Long personaId) {
        if (personaId == null) {
            throw new ResourceNotFoundException("Identificador de estudiante no puede ser nulo.");
        }

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + personaId));

        boolean tieneActorActivo = esEstudiante(personaId);
        boolean tieneActorRegistrado = personaActorRepository.findByPersonaIdAndTipoActorCodigo(personaId, CODIGO_ACTOR_ESTUDIANTE).isPresent();

        if (!tieneActorActivo && !tieneActorRegistrado) {
            throw new BusinessException("La persona no tiene el perfil de estudiante asignado.");
        }

        return persona;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaResponse> listarEstudiantesActivos(Pageable pageable) {
        Page<Persona> estudiantes = personaActorRepository.findPersonasActivasByTipoActorCodigo(CODIGO_ACTOR_ESTUDIANTE, pageable);
        return estudiantes.map(personaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaResponse> listarTodosEstudiantesActivos() {
        List<Persona> estudiantes = personaActorRepository.findPersonasActivasByTipoActorCodigoOrderByApellidos(CODIGO_ACTOR_ESTUDIANTE);
        return estudiantes.stream().map(personaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaResponse> buscarEstudiantes(String q, String estado, Pageable pageable) {
        String query = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        Boolean activo = null;
        if ("ACTIVOS".equalsIgnoreCase(estado)) {
            activo = Boolean.TRUE;
        } else if ("INACTIVOS".equalsIgnoreCase(estado)) {
            activo = Boolean.FALSE;
        }

        Page<Persona> estudiantes = personaActorRepository.buscarEstudiantes(query, activo, pageable);
        return estudiantes.map(personaMapper::toResponse);
    }

    @Override
    @Transactional
    public PersonaResponse registrarEstudiante(PersonaRequest request) {
        if (request == null) {
            throw new BusinessException("Los datos del estudiante son obligatorios.");
        }

        PersonaResponse personaCreada = personaService.crearPersona(request);
        asignarActorEstudiante(personaCreada.getId());
        return personaService.buscarPorId(personaCreada.getId());
    }

    @Override
    @Transactional
    public PersonaResponse actualizarEstudiante(Long id, PersonaRequest request) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de estudiante no puede ser nulo.");
        }
        obtenerEntidadEstudiantePorId(id);
        return personaService.actualizarPersona(id, request);
    }

    @Override
    @Transactional
    public void desactivarEstudiante(Long id) {
        Persona persona = obtenerEntidadEstudiantePorId(id);
        persona.setActivo(false);
        personaRepository.save(persona);

        personaActorRepository.findByPersonaIdAndTipoActorCodigo(id, CODIGO_ACTOR_ESTUDIANTE)
                .ifPresent(actor -> {
                    actor.setActivo(false);
                    actor.setFechaFin(LocalDate.now());
                    personaActorRepository.save(actor);
                });
    }

    @Override
    @Transactional
    public void activarEstudiante(Long id) {
        Persona persona = obtenerEntidadEstudiantePorId(id);
        persona.setActivo(true);
        personaRepository.save(persona);

        personaActorRepository.findByPersonaIdAndTipoActorCodigo(id, CODIGO_ACTOR_ESTUDIANTE)
                .ifPresent(actor -> {
                    actor.setActivo(true);
                    actor.setFechaFin(null);
                    personaActorRepository.save(actor);
                });
    }

    @Override
    @Transactional
    public PersonaActor asignarActorEstudiante(Long personaId) {
        if (personaId == null) {
            throw new ResourceNotFoundException("Identificador de persona no puede ser nulo.");
        }

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + personaId));

        TipoActor tipoActor = tipoActorRepository.findByCodigo(CODIGO_ACTOR_ESTUDIANTE)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de actor ESTUDIANTE no encontrado."));

        Optional<PersonaActor> existingOpt = personaActorRepository.findByPersonaIdAndTipoActorId(persona.getId(), tipoActor.getId());

        if (existingOpt.isPresent()) {
            PersonaActor existente = existingOpt.get();
            if (Boolean.TRUE.equals(existente.getActivo())) {
                throw new BusinessException("La persona ya está registrada como estudiante.");
            }
            existente.setActivo(true);
            existente.setFechaInicio(LocalDate.now());
            existente.setFechaFin(null);
            return personaActorRepository.save(existente);
        }

        PersonaActor nuevo = PersonaActor.builder()
                .persona(persona)
                .tipoActor(tipoActor)
                .fechaInicio(LocalDate.now())
                .activo(true)
                .build();

        return personaActorRepository.save(nuevo);
    }
}
