package com.jesusnazareno.asistencia.modules.acceso.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta para la tabla de unión 'rol_permiso'.
 */
@Embeddable
public class RolPermisoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "rol_id", nullable = false)
    private Long rolId;

    @Column(name = "permiso_id", nullable = false)
    private Long permisoId;

    public RolPermisoId() {
    }

    public RolPermisoId(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolPermisoId that = (RolPermisoId) o;
        return Objects.equals(rolId, that.rolId) && Objects.equals(permisoId, that.permisoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolId, permisoId);
    }

    @Override
    public String toString() {
        return "RolPermisoId{" +
                "rolId=" + rolId +
                ", permisoId=" + permisoId +
                '}';
    }
}
