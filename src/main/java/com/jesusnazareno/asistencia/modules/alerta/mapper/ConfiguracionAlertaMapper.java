package com.jesusnazareno.asistencia.modules.alerta.mapper;

import com.jesusnazareno.asistencia.modules.alerta.domain.ConfiguracionAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir ConfiguracionAlertaAsistencia a ConfiguracionAlertaResponse.
 */
@Component
public class ConfiguracionAlertaMapper {

    public ConfiguracionAlertaResponse toResponse(ConfiguracionAlertaAsistencia entity) {
        if (entity == null) {
            return null;
        }
        return new ConfiguracionAlertaResponse(
                entity.getId(),
                entity.getTipo(),
                entity.getTipo() != null ? entity.getTipo().getDescripcion() : null,
                entity.getUmbral(),
                entity.getActivo()
        );
    }
}
