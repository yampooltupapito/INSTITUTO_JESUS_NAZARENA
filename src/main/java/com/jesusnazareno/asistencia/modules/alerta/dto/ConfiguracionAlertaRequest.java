package com.jesusnazareno.asistencia.modules.alerta.dto;

import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la actualización de umbrales en la configuración de alertas.
 */
public class ConfiguracionAlertaRequest {

    @NotNull(message = "El tipo de alerta es obligatorio.")
    private TipoAlertaAsistencia tipo;

    @NotNull(message = "El umbral es obligatorio.")
    @Min(value = 1, message = "El umbral debe ser mayor que cero.")
    private Integer umbral;

    public ConfiguracionAlertaRequest() {
    }

    public ConfiguracionAlertaRequest(TipoAlertaAsistencia tipo, Integer umbral) {
        this.tipo = tipo;
        this.umbral = umbral;
    }

    public TipoAlertaAsistencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlertaAsistencia tipo) {
        this.tipo = tipo;
    }

    public Integer getUmbral() {
        return umbral;
    }

    public void setUmbral(Integer umbral) {
        this.umbral = umbral;
    }
}
