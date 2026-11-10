package com.jesusnazareno.asistencia.modules.acceso.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa la tabla 'rol' en la base de datos.
 */
@Entity
@Table(name = "rol")
public class Rol extends BaseEntity {

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Rol() {
    }

    public Rol(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
               String codigo, String nombre, String descripcion, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public static RolBuilder builder() {
        return new RolBuilder();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return getId() != null && Objects.equals(getId(), rol.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + getId() +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }

    public static class RolBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String codigo;
        private String nombre;
        private String descripcion;
        private Boolean activo;

        public RolBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RolBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RolBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public RolBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public RolBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public RolBuilder codigo(String codigo) {
            this.codigo = codigo;
            return this;
        }

        public RolBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public RolBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public RolBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Rol build() {
            return new Rol(id, createdAt, updatedAt, createdBy, updatedBy, codigo, nombre, descripcion, activo);
        }
    }
}
