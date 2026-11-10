package com.jesusnazareno.asistencia.modules.acceso.mapper;

import com.jesusnazareno.asistencia.modules.acceso.dto.PermisoResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.RolResponse;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RolMapper {

    public RolResponse toResponse(Rol entity) {
        return toResponse(entity, Collections.emptyList());
    }

    public RolResponse toResponse(Rol entity, List<PermisoResponse> permisos) {
        if (entity == null) {
            return null;
        }
        return new RolResponse(
                entity.getId(),
                entity.getCodigo(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getActivo(),
                permisos != null ? permisos : Collections.emptyList()
        );
    }
}
