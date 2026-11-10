package com.jesusnazareno.asistencia.modules.academico.mapper;

import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import org.springframework.stereotype.Component;

@Component
public class SeccionMapper {

    public Seccion toEntity(SeccionRequest request, Grado grado) {
        if (request == null) {
            return null;
        }
        return Seccion.builder()
                .grado(grado)
                .nombre(request.getNombre())
                .turno(request.getTurno())
                .capacidad(request.getCapacidad())
                .activo(true)
                .build();
    }

    public SeccionResponse toResponse(Seccion entity) {
        if (entity == null) {
            return null;
        }
        return SeccionResponse.builder()
                .id(entity.getId())
                .gradoId(entity.getGrado() != null ? entity.getGrado().getId() : null)
                .gradoNombre(entity.getGrado() != null ? entity.getGrado().getNombre() : null)
                .nivel(entity.getGrado() != null ? entity.getGrado().getNivel() : null)
                .nombre(entity.getNombre())
                .turno(entity.getTurno())
                .capacidad(entity.getCapacidad())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(SeccionRequest request, Seccion entity, Grado grado) {
        if (request == null || entity == null) {
            return;
        }
        entity.setGrado(grado);
        entity.setNombre(request.getNombre());
        entity.setTurno(request.getTurno());
        entity.setCapacidad(request.getCapacidad());
    }
}
