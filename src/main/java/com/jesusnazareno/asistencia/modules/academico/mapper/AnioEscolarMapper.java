package com.jesusnazareno.asistencia.modules.academico.mapper;

import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import org.springframework.stereotype.Component;

@Component
public class AnioEscolarMapper {

    public AnioEscolar toEntity(AnioEscolarRequest request) {
        if (request == null) {
            return null;
        }
        return AnioEscolar.builder()
                .anio(request.getAnio())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .nombre(request.getNombre())
                .activo(true)
                .build();
    }

    public AnioEscolarResponse toResponse(AnioEscolar entity) {
        if (entity == null) {
            return null;
        }
        return AnioEscolarResponse.builder()
                .id(entity.getId())
                .anio(entity.getAnio())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .nombre(entity.getNombre())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(AnioEscolarRequest request, AnioEscolar entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setAnio(request.getAnio());
        entity.setFechaInicio(request.getFechaInicio());
        entity.setFechaFin(request.getFechaFin());
        entity.setNombre(request.getNombre());
    }
}
