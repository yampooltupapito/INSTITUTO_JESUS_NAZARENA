package com.jesusnazareno.asistencia.modules.alerta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la resolución definitiva de una alerta de asistencia.
 */
public class ResolucionAlertaRequest {

    @NotBlank(message = "La observación de resolución es obligatoria.")
    @Size(max = 500, message = "La observación no puede exceder los 500 caracteres.")
    private String observacion;

    public ResolucionAlertaRequest() {
    }

    public ResolucionAlertaRequest(String observacion) {
        this.observacion = observacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
