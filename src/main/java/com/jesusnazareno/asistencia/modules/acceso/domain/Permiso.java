package com.jesusnazareno.asistencia.modules.acceso.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa la tabla 'permiso' en la base de datos.
 */
@Entity
@Table(name = "permiso")
public class Permiso extends BaseEntity {

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Permiso() {
    }

    public Permiso(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                   String codigo, String nombre, String descripcion, String modulo, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.modulo = modulo;
        this.activo = activo;
    }

    public static PermisoBuilder builder() {
        return new PermisoBuilder();
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

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permiso permiso = (Permiso) o;
        return getId() != null && Objects.equals(getId(), permiso.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Permiso{" +
                "id=" + getId() +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", modulo='" + modulo + '\'' +
                ", activo=" + activo +
                '}';
    }

    public static class PermisoBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String modulo;
        private Boolean activo;

        public PermisoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PermisoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PermisoBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PermisoBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public PermisoBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public PermisoBuilder codigo(String codigo) {
            this.codigo = codigo;
            return this;
        }

        public PermisoBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public PermisoBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public PermisoBuilder modulo(String modulo) {
            this.modulo = modulo;
            return this;
        }

        public PermisoBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Permiso build() {
            return new Permiso(id, createdAt, updatedAt, createdBy, updatedBy, codigo, nombre, descripcion, modulo, activo);
        }
    }
}
