package com.jesusnazareno.asistencia.modules.asistencia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de revisión (aprobación o rechazo) de una justificación.
 */
public class RevisionJustificacionRequest {

    @NotNull(message = "{validation.required}")
    private Long revisadoPor;

    @Size(max = 255, message = "{validation.invalid}")
    private String observacionRevision;

    public RevisionJustificacionRequest() {
    }

    public RevisionJustificacionRequest(Long revisadoPor, String observacionRevision) {
        this.revisadoPor = revisadoPor;
        this.observacionRevision = observacionRevision;
    }

    public static RevisionJustificacionRequestBuilder builder() {
        return new RevisionJustificacionRequestBuilder();
    }

    public Long getRevisadoPor() {
        return revisadoPor;
    }

    public void setRevisadoPor(Long revisadoPor) {
        this.revisadoPor = revisadoPor;
    }

    public String getObservacionRevision() {
        return observacionRevision;
    }

    public void setObservacionRevision(String observacionRevision) {
        this.observacionRevision = observacionRevision;
    }

    public static class RevisionJustificacionRequestBuilder {
        private Long revisadoPor;
        private String observacionRevision;

        public RevisionJustificacionRequestBuilder revisadoPor(Long revisadoPor) {
            this.revisadoPor = revisadoPor;
            return this;
        }

        public RevisionJustificacionRequestBuilder observacionRevision(String observacionRevision) {
            this.observacionRevision = observacionRevision;
            return this;
        }

        public RevisionJustificacionRequest build() {
            return new RevisionJustificacionRequest(revisadoPor, observacionRevision);
        }
    }
}
