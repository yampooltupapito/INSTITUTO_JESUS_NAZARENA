package com.jesusnazareno.asistencia.modules.academico.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla 'grado' en la base de datos.
 */
@Entity
@Table(name = "grado")
public class Grado extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false, length = 20)
    private NivelEducativo nivel;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Grado() {
    }

    public Grado(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                 NivelEducativo nivel, Integer numero, String nombre, String descripcion, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.nivel = nivel;
        this.numero = numero;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public static GradoBuilder builder() {
        return new GradoBuilder();
    }

    public NivelEducativo getNivel() {
        return nivel;
    }

    public void setNivel(NivelEducativo nivel) {
        this.nivel = nivel;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public static class GradoBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private NivelEducativo nivel;
        private Integer numero;
        private String nombre;
        private String descripcion;
        private Boolean activo = true;

        GradoBuilder() {
        }

        public GradoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GradoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GradoBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public GradoBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public GradoBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public GradoBuilder nivel(NivelEducativo nivel) {
            this.nivel = nivel;
            return this;
        }

        public GradoBuilder numero(Integer numero) {
            this.numero = numero;
            return this;
        }

        public GradoBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public GradoBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public GradoBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Grado build() {
            return new Grado(id, createdAt, updatedAt, createdBy, updatedBy, nivel, numero, nombre, descripcion, activo);
        }
    }
}
