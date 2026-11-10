package com.jesusnazareno.asistencia.modules.academico.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla 'anio_escolar' en la base de datos.
 */
@Entity
@Table(name = "anio_escolar")
public class AnioEscolar extends BaseEntity {

    @Column(name = "anio", nullable = false, unique = true)
    private Integer anio;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public AnioEscolar() {
    }

    public AnioEscolar(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                       Integer anio, LocalDate fechaInicio, LocalDate fechaFin, String nombre, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.nombre = nombre;
        this.activo = activo;
    }

    public static AnioEscolarBuilder builder() {
        return new AnioEscolarBuilder();
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

    public static class AnioEscolarBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Integer anio;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String nombre;
        private Boolean activo = true;

        AnioEscolarBuilder() {
        }

        public AnioEscolarBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AnioEscolarBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AnioEscolarBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AnioEscolarBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AnioEscolarBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public AnioEscolarBuilder anio(Integer anio) {
            this.anio = anio;
            return this;
        }

        public AnioEscolarBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public AnioEscolarBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public AnioEscolarBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public AnioEscolarBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public AnioEscolar build() {
            return new AnioEscolar(id, createdAt, updatedAt, createdBy, updatedBy, anio, fechaInicio, fechaFin, nombre, activo);
        }
    }
}
