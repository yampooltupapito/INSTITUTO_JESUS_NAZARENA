package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta y visualización de excepciones y fechas especiales del calendario escolar.
 */
public class CalendarioEscolarResponse {

    private Long id;
    private Long anioEscolarId;
    private Integer anioEscolarAnio;
    private String anioEscolarNombre;
    private LocalDate fecha;
    private String diaSemana;
    private TipoDiaCalendario tipoDia;
    private String tipoDiaDescripcion;
    private String descripcion;
    private Boolean esLectivo;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public CalendarioEscolarResponse() {
    }

    public CalendarioEscolarResponse(Long id, Long anioEscolarId, Integer anioEscolarAnio, String anioEscolarNombre,
                                    LocalDate fecha, String diaSemana, TipoDiaCalendario tipoDia, String tipoDiaDescripcion,
                                    String descripcion, Boolean esLectivo, Boolean activo,
                                    LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.anioEscolarId = anioEscolarId;
        this.anioEscolarAnio = anioEscolarAnio;
        this.anioEscolarNombre = anioEscolarNombre;
        this.fecha = fecha;
        this.diaSemana = diaSemana;
        this.tipoDia = tipoDia;
        this.tipoDiaDescripcion = tipoDiaDescripcion;
        this.descripcion = descripcion;
        this.esLectivo = esLectivo;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static CalendarioEscolarResponseBuilder builder() {
        return new CalendarioEscolarResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public TipoDiaCalendario getTipoDia() {
        return tipoDia;
    }

    public void setTipoDia(TipoDiaCalendario tipoDia) {
        this.tipoDia = tipoDia;
    }

    public String getTipoDiaDescripcion() {
        return tipoDiaDescripcion;
    }

    public void setTipoDiaDescripcion(String tipoDiaDescripcion) {
        this.tipoDiaDescripcion = tipoDiaDescripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsLectivo() {
        return esLectivo;
    }

    public void setEsLectivo(Boolean esLectivo) {
        this.esLectivo = esLectivo;
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

    public static class CalendarioEscolarResponseBuilder {
        private Long id;
        private Long anioEscolarId;
        private Integer anioEscolarAnio;
        private String anioEscolarNombre;
        private LocalDate fecha;
        private String diaSemana;
        private TipoDiaCalendario tipoDia;
        private String tipoDiaDescripcion;
        private String descripcion;
        private Boolean esLectivo;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public CalendarioEscolarResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CalendarioEscolarResponseBuilder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public CalendarioEscolarResponseBuilder anioEscolarAnio(Integer anioEscolarAnio) {
            this.anioEscolarAnio = anioEscolarAnio;
            return this;
        }

        public CalendarioEscolarResponseBuilder anioEscolarNombre(String anioEscolarNombre) {
            this.anioEscolarNombre = anioEscolarNombre;
            return this;
        }

        public CalendarioEscolarResponseBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public CalendarioEscolarResponseBuilder diaSemana(String diaSemana) {
            this.diaSemana = diaSemana;
            return this;
        }

        public CalendarioEscolarResponseBuilder tipoDia(TipoDiaCalendario tipoDia) {
            this.tipoDia = tipoDia;
            return this;
        }

        public CalendarioEscolarResponseBuilder tipoDiaDescripcion(String tipoDiaDescripcion) {
            this.tipoDiaDescripcion = tipoDiaDescripcion;
            return this;
        }

        public CalendarioEscolarResponseBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public CalendarioEscolarResponseBuilder esLectivo(Boolean esLectivo) {
            this.esLectivo = esLectivo;
            return this;
        }

        public CalendarioEscolarResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public CalendarioEscolarResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CalendarioEscolarResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CalendarioEscolarResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CalendarioEscolarResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public CalendarioEscolarResponse build() {
            return new CalendarioEscolarResponse(id, anioEscolarId, anioEscolarAnio, anioEscolarNombre,
                    fecha, diaSemana, tipoDia, tipoDiaDescripcion, descripcion, esLectivo, activo,
                    createdAt, updatedAt, createdBy, updatedBy);
        }
    }
}
