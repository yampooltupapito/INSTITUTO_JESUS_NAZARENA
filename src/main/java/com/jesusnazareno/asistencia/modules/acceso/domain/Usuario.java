package com.jesusnazareno.asistencia.modules.acceso.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa la tabla 'usuario' en la base de datos.
 */
@Entity
@Table(name = "usuario")
public class Usuario extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    public Usuario() {
    }

    public Usuario(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                   Persona persona, String username, String password, Rol rol, Boolean activo, LocalDateTime ultimoAcceso) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.persona = persona;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
        this.ultimoAcceso = ultimoAcceso;
    }

    public static UsuarioBuilder builder() {
        return new UsuarioBuilder();
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return getId() != null && Objects.equals(getId(), usuario.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", activo=" + activo +
                ", ultimoAcceso=" + ultimoAcceso +
                '}';
    }

    public static class UsuarioBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Persona persona;
        private String username;
        private String password;
        private Rol rol;
        private Boolean activo;
        private LocalDateTime ultimoAcceso;

        public UsuarioBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UsuarioBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UsuarioBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UsuarioBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public UsuarioBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public UsuarioBuilder persona(Persona persona) {
            this.persona = persona;
            return this;
        }

        public UsuarioBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UsuarioBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UsuarioBuilder rol(Rol rol) {
            this.rol = rol;
            return this;
        }

        public UsuarioBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public UsuarioBuilder ultimoAcceso(LocalDateTime ultimoAcceso) {
            this.ultimoAcceso = ultimoAcceso;
            return this;
        }

        public Usuario build() {
            return new Usuario(id, createdAt, updatedAt, createdBy, updatedBy,
                    persona, username, password, rol, activo, ultimoAcceso);
        }
    }
}
