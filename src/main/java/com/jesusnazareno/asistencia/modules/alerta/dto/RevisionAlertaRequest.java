package com.jesusnazareno.asistencia.modules.alerta.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de cambio a estado EN_REVISION de una alerta de asistencia.
 */
public class RevisionAlertaRequest {

    @Size(max = 500, message = "La observación no puede exceder los 500 caracteres.")
    private String observacion;

    public RevisionAlertaRequest() {
    }

    public RevisionAlertaRequest(String observacion) {
        this.observacion = observacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
