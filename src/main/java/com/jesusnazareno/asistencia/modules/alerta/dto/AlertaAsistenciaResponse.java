package com.jesusnazareno.asistencia.modules.alerta.dto;

import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la visualización de alertas internas de asistencia.
 */
public class AlertaAsistenciaResponse {

    private Long id;
    private Long matriculaId;
    private String codigoMatricula;
    private Long estudianteId;
    private String estudianteDocumento;
    private String estudianteNombreCompleto;
    private Long anioEscolarId;
    private Integer anioEscolarAnio;
    private String anioEscolarNombre;
    private Long gradoId;
    private String gradoNombre;
    private Long seccionId;
    private String seccionNombre;
    private TipoAlertaAsistencia tipo;
    private String tipoDescripcion;
    private LocalDate fechaDeteccion;
    private LocalDate fechaInicioPeriodo;
    private LocalDate fechaFinPeriodo;
    private Integer cantidad;
    private EstadoAlerta estado;
    private String estadoDescripcion;
    private String descripcion;
    private Long revisadoPorId;
    private String revisadoPorNombre;
    private LocalDateTime fechaRevision;
    private String observacionRevision;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public AlertaAsistenciaResponse() {
    }

    public static AlertaAsistenciaResponseBuilder builder() {
        return new AlertaAsistenciaResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getCodigoMatricula() {
        return codigoMatricula;
    }

    public void setCodigoMatricula(String codigoMatricula) {
        this.codigoMatricula = codigoMatricula;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public Integer getAnioEscolarAnio() {
        return anioEscolarAnio;
    }

    public void setAnioEscolarAnio(Integer anioEscolarAnio) {
        this.anioEscolarAnio = anioEscolarAnio;
    }

    public String getAnioEscolarNombre() {
        return anioEscolarNombre;
    }

    public void setAnioEscolarNombre(String anioEscolarNombre) {
        this.anioEscolarNombre = anioEscolarNombre;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public String getSeccionNombre() {
        return seccionNombre;
    }

    public void setSeccionNombre(String seccionNombre) {
        this.seccionNombre = seccionNombre;
    }

    public TipoAlertaAsistencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlertaAsistencia tipo) {
        this.tipo = tipo;
    }

    public String getTipoDescripcion() {
        return tipoDescripcion;
    }

    public void setTipoDescripcion(String tipoDescripcion) {
        this.tipoDescripcion = tipoDescripcion;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDate fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public LocalDate getFechaInicioPeriodo() {
        return fechaInicioPeriodo;
    }

    public void setFechaInicioPeriodo(LocalDate fechaInicioPeriodo) {
        this.fechaInicioPeriodo = fechaInicioPeriodo;
    }

    public LocalDate getFechaFinPeriodo() {
        return fechaFinPeriodo;
    }

    public void setFechaFinPeriodo(LocalDate fechaFinPeriodo) {
        this.fechaFinPeriodo = fechaFinPeriodo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public EstadoAlerta getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlerta estado) {
        this.estado = estado;
    }

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getRevisadoPorId() {
        return revisadoPorId;
    }

    public void setRevisadoPorId(Long revisadoPorId) {
        this.revisadoPorId = revisadoPorId;
    }

    public String getRevisadoPorNombre() {
        return revisadoPorNombre;
    }

    public void setRevisadoPorNombre(String revisadoPorNombre) {
        this.revisadoPorNombre = revisadoPorNombre;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getObservacionRevision() {
        return observacionRevision;
    }

    public void setObservacionRevision(String observacionRevision) {
        this.observacionRevision = observacionRevision;
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

    public static class AlertaAsistenciaResponseBuilder {
        private Long id;
        private Long matriculaId;
        private String codigoMatricula;
        private Long estudianteId;
        private String estudianteDocumento;
        private String estudianteNombreCompleto;
        private Long anioEscolarId;
        private Integer anioEscolarAnio;
        private String anioEscolarNombre;
        private Long gradoId;
        private String gradoNombre;
        private Long seccionId;
        private String seccionNombre;
        private TipoAlertaAsistencia tipo;
        private String tipoDescripcion;
        private LocalDate fechaDeteccion;
        private LocalDate fechaInicioPeriodo;
        private LocalDate fechaFinPeriodo;
        private Integer cantidad;
        private EstadoAlerta estado;
        private String estadoDescripcion;
        private String descripcion;
        private Long revisadoPorId;
        private String revisadoPorNombre;
        private LocalDateTime fechaRevision;
        private String observacionRevision;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public AlertaAsistenciaResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AlertaAsistenciaResponseBuilder matriculaId(Long matriculaId) {
            this.matriculaId = matriculaId;
            return this;
        }

        public AlertaAsistenciaResponseBuilder codigoMatricula(String codigoMatricula) {
            this.codigoMatricula = codigoMatricula;
            return this;
        }

        public AlertaAsistenciaResponseBuilder estudianteId(Long estudianteId) {
            this.estudianteId = estudianteId;
            return this;
        }

        public AlertaAsistenciaResponseBuilder estudianteDocumento(String estudianteDocumento) {
            this.estudianteDocumento = estudianteDocumento;
            return this;
        }

        public AlertaAsistenciaResponseBuilder estudianteNombreCompleto(String estudianteNombreCompleto) {
            this.estudianteNombreCompleto = estudianteNombreCompleto;
            return this;
        }

        public AlertaAsistenciaResponseBuilder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public AlertaAsistenciaResponseBuilder anioEscolarAnio(Integer anioEscolarAnio) {
            this.anioEscolarAnio = anioEscolarAnio;
            return this;
        }

        public AlertaAsistenciaResponseBuilder anioEscolarNombre(String anioEscolarNombre) {
            this.anioEscolarNombre = anioEscolarNombre;
            return this;
        }

        public AlertaAsistenciaResponseBuilder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public AlertaAsistenciaResponseBuilder gradoNombre(String gradoNombre) {
            this.gradoNombre = gradoNombre;
            return this;
        }

        public AlertaAsistenciaResponseBuilder seccionId(Long seccionId) {
            this.seccionId = seccionId;
            return this;
        }

        public AlertaAsistenciaResponseBuilder seccionNombre(String seccionNombre) {
            this.seccionNombre = seccionNombre;
            return this;
        }

        public AlertaAsistenciaResponseBuilder tipo(TipoAlertaAsistencia tipo) {
            this.tipo = tipo;
            return this;
        }

        public AlertaAsistenciaResponseBuilder tipoDescripcion(String tipoDescripcion) {
            this.tipoDescripcion = tipoDescripcion;
            return this;
        }

        public AlertaAsistenciaResponseBuilder fechaDeteccion(LocalDate fechaDeteccion) {
            this.fechaDeteccion = fechaDeteccion;
            return this;
        }

        public AlertaAsistenciaResponseBuilder fechaInicioPeriodo(LocalDate fechaInicioPeriodo) {
            this.fechaInicioPeriodo = fechaInicioPeriodo;
            return this;
        }

        public AlertaAsistenciaResponseBuilder fechaFinPeriodo(LocalDate fechaFinPeriodo) {
            this.fechaFinPeriodo = fechaFinPeriodo;
            return this;
        }

        public AlertaAsistenciaResponseBuilder cantidad(Integer cantidad) {
            this.cantidad = cantidad;
            return this;
        }

        public AlertaAsistenciaResponseBuilder estado(EstadoAlerta estado) {
            this.estado = estado;
            return this;
        }

        public AlertaAsistenciaResponseBuilder estadoDescripcion(String estadoDescripcion) {
            this.estadoDescripcion = estadoDescripcion;
            return this;
        }

        public AlertaAsistenciaResponseBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public AlertaAsistenciaResponseBuilder revisadoPorId(Long revisadoPorId) {
            this.revisadoPorId = revisadoPorId;
            return this;
        }

        public AlertaAsistenciaResponseBuilder revisadoPorNombre(String revisadoPorNombre) {
            this.revisadoPorNombre = revisadoPorNombre;
            return this;
        }

        public AlertaAsistenciaResponseBuilder fechaRevision(LocalDateTime fechaRevision) {
            this.fechaRevision = fechaRevision;
            return this;
        }

        public AlertaAsistenciaResponseBuilder observacionRevision(String observacionRevision) {
            this.observacionRevision = observacionRevision;
            return this;
        }

        public AlertaAsistenciaResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public AlertaAsistenciaResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AlertaAsistenciaResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AlertaAsistenciaResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AlertaAsistenciaResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public AlertaAsistenciaResponse build() {
            AlertaAsistenciaResponse response = new AlertaAsistenciaResponse();
            response.setId(id);
            response.setMatriculaId(matriculaId);
            response.setCodigoMatricula(codigoMatricula);
            response.setEstudianteId(estudianteId);
            response.setEstudianteDocumento(estudianteDocumento);
            response.setEstudianteNombreCompleto(estudianteNombreCompleto);
            response.setAnioEscolarId(anioEscolarId);
            response.setAnioEscolarAnio(anioEscolarAnio);
            response.setAnioEscolarNombre(anioEscolarNombre);
            response.setGradoId(gradoId);
            response.setGradoNombre(gradoNombre);
            response.setSeccionId(seccionId);
            response.setSeccionNombre(seccionNombre);
            response.setTipo(tipo);
            response.setTipoDescripcion(tipoDescripcion);
            response.setFechaDeteccion(fechaDeteccion);
            response.setFechaInicioPeriodo(fechaInicioPeriodo);
            response.setFechaFinPeriodo(fechaFinPeriodo);
            response.setCantidad(cantidad);
            response.setEstado(estado);
            response.setEstadoDescripcion(estadoDescripcion);
            response.setDescripcion(descripcion);
            response.setRevisadoPorId(revisadoPorId);
            response.setRevisadoPorNombre(revisadoPorNombre);
            response.setFechaRevision(fechaRevision);
            response.setObservacionRevision(observacionRevision);
            response.setActivo(activo);
            response.setCreatedAt(createdAt);
            response.setUpdatedAt(updatedAt);
            response.setCreatedBy(createdBy);
            response.setUpdatedBy(updatedBy);
            return response;
        }
    }
}
