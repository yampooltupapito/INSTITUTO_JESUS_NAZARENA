package com.jesusnazareno.asistencia.modules.academico.mapper;

import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoResponse;
import org.springframework.stereotype.Component;

@Component
public class GradoMapper {

    public Grado toEntity(GradoRequest request) {
        if (request == null) {
            return null;
        }
        return Grado.builder()
                .nivel(request.getNivel())
                .numero(request.getNumero())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activo(true)
                .build();
    }

    public GradoResponse toResponse(Grado entity) {
        if (entity == null) {
            return null;
        }
        return GradoResponse.builder()
                .id(entity.getId())
                .nivel(entity.getNivel())
                .numero(entity.getNumero())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(GradoRequest request, Grado entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNivel(request.getNivel());
        entity.setNumero(request.getNumero());
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
    }
}
