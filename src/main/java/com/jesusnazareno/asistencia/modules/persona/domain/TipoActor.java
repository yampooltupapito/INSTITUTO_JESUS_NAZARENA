package com.jesusnazareno.asistencia.modules.persona.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla 'tipo_actor' en la base de datos.
 */
@Entity
@Table(name = "tipo_actor")
public class TipoActor extends BaseEntity {

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public TipoActor() {
    }

    public TipoActor(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                     String codigo, String nombre, String descripcion, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public static TipoActorBuilder builder() {
        return new TipoActorBuilder();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public static class TipoActorBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String codigo;
        private String nombre;
        private String descripcion;
        private Boolean activo = true;

        TipoActorBuilder() {
        }

        public TipoActorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TipoActorBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TipoActorBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TipoActorBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public TipoActorBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public TipoActorBuilder codigo(String codigo) {
            this.codigo = codigo;
            return this;
        }

        public TipoActorBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public TipoActorBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public TipoActorBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public TipoActor build() {
            return new TipoActor(id, createdAt, updatedAt, createdBy, updatedBy, codigo, nombre, descripcion, activo);
        }
    }
}
