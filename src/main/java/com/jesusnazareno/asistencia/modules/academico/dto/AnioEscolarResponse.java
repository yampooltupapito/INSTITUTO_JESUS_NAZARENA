package com.jesusnazareno.asistencia.modules.academico.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AnioEscolarResponse {

    private Long id;
    private Integer anio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String nombre;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long cantidadMatriculas;

    public AnioEscolarResponse() {
    }

    public AnioEscolarResponse(Long id, Integer anio, LocalDate fechaInicio, LocalDate fechaFin, String nombre,
                               Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, anio, fechaInicio, fechaFin, nombre, activo, createdAt, updatedAt, null, null, 0L);
    }

    public AnioEscolarResponse(Long id, Integer anio, LocalDate fechaInicio, LocalDate fechaFin, String nombre,
                               Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt,
                               String createdBy, String updatedBy, Long cantidadMatriculas) {
        this.id = id;
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.nombre = nombre;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.cantidadMatriculas = cantidadMatriculas;
    }

    public static AnioEscolarResponseBuilder builder() {
        return new AnioEscolarResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Long getCantidadMatriculas() {
        return cantidadMatriculas;
    }

    public void setCantidadMatriculas(Long cantidadMatriculas) {
        this.cantidadMatriculas = cantidadMatriculas;
    }

    public static class AnioEscolarResponseBuilder {
        private Long id;
        private Integer anio;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String nombre;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long cantidadMatriculas;

        AnioEscolarResponseBuilder() {
        }

        public AnioEscolarResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AnioEscolarResponseBuilder anio(Integer anio) {
            this.anio = anio;
            return this;
        }

        public AnioEscolarResponseBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public AnioEscolarResponseBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public AnioEscolarResponseBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public AnioEscolarResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public AnioEscolarResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AnioEscolarResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AnioEscolarResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AnioEscolarResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public AnioEscolarResponseBuilder cantidadMatriculas(Long cantidadMatriculas) {
            this.cantidadMatriculas = cantidadMatriculas;
            return this;
        }

        public AnioEscolarResponse build() {
            return new AnioEscolarResponse(id, anio, fechaInicio, fechaFin, nombre, activo, createdAt, updatedAt, createdBy, updatedBy, cantidadMatriculas);
        }
    }
}
