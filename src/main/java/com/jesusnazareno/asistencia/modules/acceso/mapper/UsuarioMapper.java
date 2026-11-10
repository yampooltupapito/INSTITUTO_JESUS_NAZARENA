package com.jesusnazareno.asistencia.modules.acceso.mapper;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioResponse;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario entity) {
        if (entity == null) {
            return null;
        }

        Long personaId = null;
        String personaNombreCompleto = null;
        String personaNumeroDocumento = null;

        Persona persona = entity.getPersona();
        if (persona != null) {
            personaId = persona.getId();
            personaNumeroDocumento = persona.getNumeroDocumento();
            String pat = persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "";
            String mat = persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "";
            String nom = persona.getNombres() != null ? persona.getNombres() : "";
            personaNombreCompleto = (pat + " " + mat).trim() + ", " + nom;
        }

        Long rolId = entity.getRol() != null ? entity.getRol().getId() : null;
        String rolCodigo = entity.getRol() != null ? entity.getRol().getCodigo() : null;
        String rolNombre = entity.getRol() != null ? entity.getRol().getNombre() : null;

        return UsuarioResponse.builder()
                .id(entity.getId())
                .personaId(personaId)
                .personaNombreCompleto(personaNombreCompleto)
                .personaNumeroDocumento(personaNumeroDocumento)
                .username(entity.getUsername())
                .rolId(rolId)
                .rolCodigo(rolCodigo)
                .rolNombre(rolNombre)
                .activo(entity.getActivo())
                .ultimoAcceso(entity.getUltimoAcceso())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
