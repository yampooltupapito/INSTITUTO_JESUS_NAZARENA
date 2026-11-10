package com.jesusnazareno.asistencia.modules.acceso.mapper;

import com.jesusnazareno.asistencia.modules.acceso.dto.PermisoResponse;
import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import org.springframework.stereotype.Component;

@Component
public class PermisoMapper {

    public PermisoResponse toResponse(Permiso entity) {
        if (entity == null) {
            return null;
        }
        return new PermisoResponse(
                entity.getId(),
                entity.getCodigo(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getModulo(),
                entity.getActivo()
        );
    }
}
