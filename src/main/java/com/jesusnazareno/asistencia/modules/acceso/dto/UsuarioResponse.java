package com.jesusnazareno.asistencia.modules.acceso.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioResponse {

    private Long id;
    private Long personaId;
    private String personaNombreCompleto;
    private String personaNumeroDocumento;
    private String username;
    private Long rolId;
    private String rolCodigo;
    private String rolNombre;
    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<String> permisos = new ArrayList<>();

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, Long personaId, String personaNombreCompleto, String personaNumeroDocumento,
                           String username, Long rolId, String rolCodigo, String rolNombre,
                           Boolean activo, LocalDateTime ultimoAcceso,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.personaId = personaId;
        this.personaNombreCompleto = personaNombreCompleto;
        this.personaNumeroDocumento = personaNumeroDocumento;
        this.username = username;
        this.rolId = rolId;
        this.rolCodigo = rolCodigo;
        this.rolNombre = rolNombre;
        this.activo = activo;
        this.ultimoAcceso = ultimoAcceso;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long personaId;
        private String personaNombreCompleto;
        private String personaNumeroDocumento;
        private String username;
        private Long rolId;
        private String rolCodigo;
        private String rolNombre;
        private Boolean activo;
        private LocalDateTime ultimoAcceso;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private List<String> permisos = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder personaId(Long personaId) {
            this.personaId = personaId;
            return this;
        }

        public Builder personaNombreCompleto(String personaNombreCompleto) {
            this.personaNombreCompleto = personaNombreCompleto;
            return this;
        }

        public Builder personaNumeroDocumento(String personaNumeroDocumento) {
            this.personaNumeroDocumento = personaNumeroDocumento;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder rolId(Long rolId) {
            this.rolId = rolId;
            return this;
        }

        public Builder rolCodigo(String rolCodigo) {
            this.rolCodigo = rolCodigo;
            return this;
        }

        public Builder rolNombre(String rolNombre) {
            this.rolNombre = rolNombre;
            return this;
        }

        public Builder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Builder ultimoAcceso(LocalDateTime ultimoAcceso) {
            this.ultimoAcceso = ultimoAcceso;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder permisos(List<String> permisos) {
            this.permisos = permisos != null ? permisos : new ArrayList<>();
            return this;
        }

        public UsuarioResponse build() {
            UsuarioResponse response = new UsuarioResponse(id, personaId, personaNombreCompleto, personaNumeroDocumento,
                    username, rolId, rolCodigo, rolNombre, activo, ultimoAcceso, createdAt, updatedAt);
            response.setCreatedBy(createdBy);
            response.setUpdatedBy(updatedBy);
            response.setPermisos(permisos);
            return response;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getPersonaNombreCompleto() {
        return personaNombreCompleto;
    }

    public void setPersonaNombreCompleto(String personaNombreCompleto) {
        this.personaNombreCompleto = personaNombreCompleto;
    }

    public String getPersonaNumeroDocumento() {
        return personaNumeroDocumento;
    }

    public void setPersonaNumeroDocumento(String personaNumeroDocumento) {
        this.personaNumeroDocumento = personaNumeroDocumento;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public String getRolCodigo() {
        return rolCodigo;
    }

    public void setRolCodigo(String rolCodigo) {
        this.rolCodigo = rolCodigo;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos != null ? permisos : new ArrayList<>();
    }
}
