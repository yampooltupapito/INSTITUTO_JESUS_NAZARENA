package com.jesusnazareno.asistencia.modules.apoderado.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la relación entre un estudiante y su apoderado.
 * Mapea la tabla 'estudiante_apoderado'.
 */
@Entity
@Table(name = "estudiante_apoderado")
public class EstudianteApoderado extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Persona estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apoderado_id", nullable = false)
    private Persona apoderado;

    @Enumerated(EnumType.STRING)
    @Column(name = "parentesco", nullable = false, length = 20)
    private Parentesco parentesco;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public EstudianteApoderado() {
    }

    public EstudianteApoderado(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                               Persona estudiante, Persona apoderado, Parentesco parentesco, Boolean esPrincipal,
                               LocalDate fechaInicio, LocalDate fechaFin, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.estudiante = estudiante;
        this.apoderado = apoderado;
        this.parentesco = parentesco;
        this.esPrincipal = esPrincipal != null ? esPrincipal : Boolean.FALSE;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo != null ? activo : Boolean.TRUE;
    }

    public static EstudianteApoderadoBuilder builder() {
        return new EstudianteApoderadoBuilder();
    }

    public Persona getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Persona estudiante) {
        this.estudiante = estudiante;
    }

    public Persona getApoderado() {
        return apoderado;
    }

    public void setApoderado(Persona apoderado) {
        this.apoderado = apoderado;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public static class EstudianteApoderadoBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Persona estudiante;
        private Persona apoderado;
        private Parentesco parentesco;
        private Boolean esPrincipal = Boolean.FALSE;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Boolean activo = Boolean.TRUE;

        EstudianteApoderadoBuilder() {
        }

        public EstudianteApoderadoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EstudianteApoderadoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EstudianteApoderadoBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public EstudianteApoderadoBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public EstudianteApoderadoBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public EstudianteApoderadoBuilder estudiante(Persona estudiante) {
            this.estudiante = estudiante;
            return this;
        }

        public EstudianteApoderadoBuilder apoderado(Persona apoderado) {
            this.apoderado = apoderado;
            return this;
        }

        public EstudianteApoderadoBuilder parentesco(Parentesco parentesco) {
            this.parentesco = parentesco;
            return this;
        }

        public EstudianteApoderadoBuilder esPrincipal(Boolean esPrincipal) {
            this.esPrincipal = esPrincipal;
            return this;
        }

        public EstudianteApoderadoBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public EstudianteApoderadoBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public EstudianteApoderadoBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public EstudianteApoderado build() {
            return new EstudianteApoderado(id, createdAt, updatedAt, createdBy, updatedBy,
                    estudiante, apoderado, parentesco, esPrincipal, fechaInicio, fechaFin, activo);
        }
    }
}
