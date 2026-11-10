package com.jesusnazareno.asistencia.modules.asistencia.dto;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta estructurada de una justificación.
 */
public class JustificacionResponse {

    private Long id;
    private Long asistenciaId;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteNumeroDocumento;
    private String gradoNombre;
    private String seccionNombre;
    private String anioEscolarNombre;
    private LocalDate asistenciaFecha;
    private EstadoAsistencia asistenciaEstado;
    private String motivo;
    private String descripcion;
    private LocalDate fechaSolicitud;
    private EstadoJustificacion estado;
    private String observacionRevision;
    private Long revisadoPor;
    private String revisadoPorNombre;
    private String revisadoPorUsername;
    private LocalDateTime fechaRevision;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public JustificacionResponse() {
    }

    public JustificacionResponse(Long id, Long asistenciaId, Long estudianteId, String estudianteNombreCompleto,
                                 LocalDate asistenciaFecha, EstadoAsistencia asistenciaEstado, String motivo,
                                 String descripcion, LocalDate fechaSolicitud, EstadoJustificacion estado,
                                 String observacionRevision, Long revisadoPor, LocalDateTime fechaRevision,
                                 Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.asistenciaId = asistenciaId;
        this.estudianteId = estudianteId;
        this.estudianteNombreCompleto = estudianteNombreCompleto;
        this.asistenciaFecha = asistenciaFecha;
        this.asistenciaEstado = asistenciaEstado;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
        this.observacionRevision = observacionRevision;
        this.revisadoPor = revisadoPor;
        this.fechaRevision = fechaRevision;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static JustificacionResponseBuilder builder() {
        return new JustificacionResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAsistenciaId() {
        return asistenciaId;
    }

    public void setAsistenciaId(Long asistenciaId) {
        this.asistenciaId = asistenciaId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public LocalDate getAsistenciaFecha() {
        return asistenciaFecha;
    }

    public void setAsistenciaFecha(LocalDate asistenciaFecha) {
        this.asistenciaFecha = asistenciaFecha;
    }

    public EstadoAsistencia getAsistenciaEstado() {
        return asistenciaEstado;
    }

    public void setAsistenciaEstado(EstadoAsistencia asistenciaEstado) {
        this.asistenciaEstado = asistenciaEstado;
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

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoJustificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoJustificacion estado) {
        this.estado = estado;
    }

    public String getObservacionRevision() {
        return observacionRevision;
    }

    public void setObservacionRevision(String observacionRevision) {
        this.observacionRevision = observacionRevision;
    }

    public Long getRevisadoPor() {
        return revisadoPor;
    }

    public void setRevisadoPor(Long revisadoPor) {
        this.revisadoPor = revisadoPor;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEstudianteNumeroDocumento() {
        return estudianteNumeroDocumento;
    }

    public void setEstudianteNumeroDocumento(String estudianteNumeroDocumento) {
        this.estudianteNumeroDocumento = estudianteNumeroDocumento;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public String getSeccionNombre() {
        return seccionNombre;
    }

    public void setSeccionNombre(String seccionNombre) {
        this.seccionNombre = seccionNombre;
    }

    public String getAnioEscolarNombre() {
        return anioEscolarNombre;
    }

    public void setAnioEscolarNombre(String anioEscolarNombre) {
        this.anioEscolarNombre = anioEscolarNombre;
    }

    public String getRevisadoPorNombre() {
        return revisadoPorNombre;
    }

    public void setRevisadoPorNombre(String revisadoPorNombre) {
        this.revisadoPorNombre = revisadoPorNombre;
    }

    public String getRevisadoPorUsername() {
        return revisadoPorUsername;
    }

    public void setRevisadoPorUsername(String revisadoPorUsername) {
        this.revisadoPorUsername = revisadoPorUsername;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public static class JustificacionResponseBuilder {
        private Long id;
        private Long asistenciaId;
        private Long estudianteId;
        private String estudianteNombreCompleto;
        private String estudianteNumeroDocumento;
        private String gradoNombre;
        private String seccionNombre;
        private String anioEscolarNombre;
        private LocalDate asistenciaFecha;
        private EstadoAsistencia asistenciaEstado;
        private String motivo;
        private String descripcion;
        private LocalDate fechaSolicitud;
        private EstadoJustificacion estado;
        private String observacionRevision;
        private Long revisadoPor;
        private String revisadoPorNombre;
        private String revisadoPorUsername;
        private LocalDateTime fechaRevision;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public JustificacionResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public JustificacionResponseBuilder asistenciaId(Long asistenciaId) {
            this.asistenciaId = asistenciaId;
            return this;
        }

        public JustificacionResponseBuilder estudianteId(Long estudianteId) {
            this.estudianteId = estudianteId;
            return this;
        }

        public JustificacionResponseBuilder estudianteNombreCompleto(String estudianteNombreCompleto) {
            this.estudianteNombreCompleto = estudianteNombreCompleto;
            return this;
        }

        public JustificacionResponseBuilder estudianteNumeroDocumento(String estudianteNumeroDocumento) {
            this.estudianteNumeroDocumento = estudianteNumeroDocumento;
            return this;
        }

        public JustificacionResponseBuilder gradoNombre(String gradoNombre) {
            this.gradoNombre = gradoNombre;
            return this;
        }

        public JustificacionResponseBuilder seccionNombre(String seccionNombre) {
            this.seccionNombre = seccionNombre;
            return this;
        }

        public JustificacionResponseBuilder anioEscolarNombre(String anioEscolarNombre) {
            this.anioEscolarNombre = anioEscolarNombre;
            return this;
        }

        public JustificacionResponseBuilder asistenciaFecha(LocalDate asistenciaFecha) {
            this.asistenciaFecha = asistenciaFecha;
            return this;
        }

        public JustificacionResponseBuilder asistenciaEstado(EstadoAsistencia asistenciaEstado) {
            this.asistenciaEstado = asistenciaEstado;
            return this;
        }

        public JustificacionResponseBuilder motivo(String motivo) {
            this.motivo = motivo;
            return this;
        }

        public JustificacionResponseBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public JustificacionResponseBuilder fechaSolicitud(LocalDate fechaSolicitud) {
            this.fechaSolicitud = fechaSolicitud;
            return this;
        }

        public JustificacionResponseBuilder estado(EstadoJustificacion estado) {
            this.estado = estado;
            return this;
        }

        public JustificacionResponseBuilder observacionRevision(String observacionRevision) {
            this.observacionRevision = observacionRevision;
            return this;
        }

        public JustificacionResponseBuilder revisadoPor(Long revisadoPor) {
            this.revisadoPor = revisadoPor;
            return this;
        }

        public JustificacionResponseBuilder revisadoPorNombre(String revisadoPorNombre) {
            this.revisadoPorNombre = revisadoPorNombre;
            return this;
        }

        public JustificacionResponseBuilder revisadoPorUsername(String revisadoPorUsername) {
            this.revisadoPorUsername = revisadoPorUsername;
            return this;
        }

        public JustificacionResponseBuilder fechaRevision(LocalDateTime fechaRevision) {
            this.fechaRevision = fechaRevision;
            return this;
        }

        public JustificacionResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public JustificacionResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public JustificacionResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public JustificacionResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public JustificacionResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public JustificacionResponse build() {
            JustificacionResponse res = new JustificacionResponse(id, asistenciaId, estudianteId, estudianteNombreCompleto,
                    asistenciaFecha, asistenciaEstado, motivo, descripcion, fechaSolicitud,
                    estado, observacionRevision, revisadoPor, fechaRevision, activo, createdAt, updatedAt);
            res.setEstudianteNumeroDocumento(estudianteNumeroDocumento);
            res.setGradoNombre(gradoNombre);
            res.setSeccionNombre(seccionNombre);
            res.setAnioEscolarNombre(anioEscolarNombre);
            res.setRevisadoPorNombre(revisadoPorNombre);
            res.setRevisadoPorUsername(revisadoPorUsername);
            res.setCreatedBy(createdBy);
            res.setUpdatedBy(updatedBy);
            return res;
        }
    }
}
