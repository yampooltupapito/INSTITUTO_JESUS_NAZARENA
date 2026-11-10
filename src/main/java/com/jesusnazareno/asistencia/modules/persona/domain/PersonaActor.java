package com.jesusnazareno.asistencia.modules.persona.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla 'persona_actor' en la base de datos.
 */
@Entity
@Table(name = "persona_actor")
public class PersonaActor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_actor_id", nullable = false)
    private TipoActor tipoActor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public PersonaActor() {
    }

    public PersonaActor(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                        Persona persona, TipoActor tipoActor, LocalDate fechaInicio, LocalDate fechaFin, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.persona = persona;
        this.tipoActor = tipoActor;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
    }

    public static PersonaActorBuilder builder() {
        return new PersonaActorBuilder();
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public TipoActor getTipoActor() {
        return tipoActor;
    }

    public void setTipoActor(TipoActor tipoActor) {
        this.tipoActor = tipoActor;
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

    public static class PersonaActorBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Persona persona;
        private TipoActor tipoActor;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Boolean activo = true;

        PersonaActorBuilder() {
        }

        public PersonaActorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PersonaActorBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PersonaActorBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PersonaActorBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public PersonaActorBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public PersonaActorBuilder persona(Persona persona) {
            this.persona = persona;
            return this;
        }

        public PersonaActorBuilder tipoActor(TipoActor tipoActor) {
            this.tipoActor = tipoActor;
            return this;
        }

        public PersonaActorBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public PersonaActorBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public PersonaActorBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public PersonaActor build() {
            return new PersonaActor(id, createdAt, updatedAt, createdBy, updatedBy, persona, tipoActor, fechaInicio, fechaFin, activo);
        }
    }
}
