package com.jesusnazareno.asistencia.modules.persona.mapper;

import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper manual simple para conversión entre entidad Persona y sus DTOs.
 */
@Component
public class PersonaMapper {

    public Persona toEntity(PersonaRequest request) {
        if (request == null) {
            return null;
        }
        return Persona.builder()
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .nombres(request.getNombres())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .fechaNacimiento(request.getFechaNacimiento())
                .sexo(request.getSexo())
                .telefono(request.getTelefono())
                .correo(request.getCorreo())
                .direccion(request.getDireccion())
                .activo(true)
                .build();
    }

    public PersonaResponse toResponse(Persona entity) {
        if (entity == null) {
            return null;
        }
        return PersonaResponse.builder()
                .id(entity.getId())
                .tipoDocumento(entity.getTipoDocumento())
                .numeroDocumento(entity.getNumeroDocumento())
                .nombres(entity.getNombres())
                .apellidoPaterno(entity.getApellidoPaterno())
                .apellidoMaterno(entity.getApellidoMaterno())
                .fechaNacimiento(entity.getFechaNacimiento())
                .sexo(entity.getSexo())
                .telefono(entity.getTelefono())
                .correo(entity.getCorreo())
                .direccion(entity.getDireccion())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(PersonaRequest request, Persona entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setTipoDocumento(request.getTipoDocumento());
        entity.setNumeroDocumento(request.getNumeroDocumento());
        entity.setNombres(request.getNombres());
        entity.setApellidoPaterno(request.getApellidoPaterno());
        entity.setApellidoMaterno(request.getApellidoMaterno());
        entity.setFechaNacimiento(request.getFechaNacimiento());
        entity.setSexo(request.getSexo());
        entity.setTelefono(request.getTelefono());
        entity.setCorreo(request.getCorreo());
        entity.setDireccion(request.getDireccion());
    }
}
