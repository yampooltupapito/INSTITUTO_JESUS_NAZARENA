package com.jesusnazareno.asistencia.modules.academico.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla 'seccion' en la base de datos.
 */
@Entity
@Table(name = "seccion")
public class Seccion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false, length = 20)
    private Turno turno;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Seccion() {
    }

    public Seccion(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                   Grado grado, String nombre, Turno turno, Integer capacidad, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.grado = grado;
        this.nombre = nombre;
        this.turno = turno;
        this.capacidad = capacidad;
        this.activo = activo;
    }

    public static SeccionBuilder builder() {
        return new SeccionBuilder();
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public static class SeccionBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Grado grado;
        private String nombre;
        private Turno turno;
        private Integer capacidad;
        private Boolean activo = true;

        SeccionBuilder() {
        }

        public SeccionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SeccionBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SeccionBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SeccionBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public SeccionBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public SeccionBuilder grado(Grado grado) {
            this.grado = grado;
            return this;
        }

        public SeccionBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public SeccionBuilder turno(Turno turno) {
            this.turno = turno;
            return this;
        }

        public SeccionBuilder capacidad(Integer capacidad) {
            this.capacidad = capacidad;
            return this;
        }

        public SeccionBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Seccion build() {
            return new Seccion(id, createdAt, updatedAt, createdBy, updatedBy, grado, nombre, turno, capacidad, activo);
        }
    }
}
