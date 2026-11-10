package com.jesusnazareno.asistencia.modules.asistencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de registro de una justificación de asistencia.
 * El estado inicial no se recibe del cliente, siempre es PENDIENTE.
 */
public class JustificacionRequest {

    @NotNull(message = "{validation.required}")
    private Long asistenciaId;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "{validation.invalid}")
    private String motivo;

    @Size(max = 1000, message = "{validation.invalid}")
    private String descripcion;

    public JustificacionRequest() {
    }

    public JustificacionRequest(Long asistenciaId, String motivo, String descripcion) {
        this.asistenciaId = asistenciaId;
        this.motivo = motivo;
        this.descripcion = descripcion;
    }

    public static JustificacionRequestBuilder builder() {
        return new JustificacionRequestBuilder();
    }

    public Long getAsistenciaId() {
        return asistenciaId;
    }

    public void setAsistenciaId(Long asistenciaId) {
        this.asistenciaId = asistenciaId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public static class JustificacionRequestBuilder {
        private Long asistenciaId;
        private String motivo;
        private String descripcion;

        public JustificacionRequestBuilder asistenciaId(Long asistenciaId) {
            this.asistenciaId = asistenciaId;
            return this;
        }

        public JustificacionRequestBuilder motivo(String motivo) {
            this.motivo = motivo;
            return this;
        }

        public JustificacionRequestBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public JustificacionRequest build() {
            return new JustificacionRequest(asistenciaId, motivo, descripcion);
        }
    }
}
