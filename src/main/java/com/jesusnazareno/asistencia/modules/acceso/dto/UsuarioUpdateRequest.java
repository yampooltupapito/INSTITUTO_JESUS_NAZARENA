package com.jesusnazareno.asistencia.modules.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de datos generales de un usuario.
 * No permite modificar la contraseña (se realiza mediante una operación separada).
 */
public class UsuarioUpdateRequest {

    @NotBlank(message = "{validation.required}")
    @Size(min = 3, max = 50, message = "{validation.invalid}")
    private String username;

    @NotNull(message = "{validation.required}")
    private Long rolId;

    private Boolean activo;

    public UsuarioUpdateRequest() {
    }

    public UsuarioUpdateRequest(Long rolId, Boolean activo) {
        this.rolId = rolId;
        this.activo = activo;
    }

    public UsuarioUpdateRequest(String username, Long rolId, Boolean activo) {
        this.username = username;
        this.rolId = rolId;
        this.activo = activo;
    }

    public static UsuarioUpdateRequestBuilder builder() {
        return new UsuarioUpdateRequestBuilder();
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public static class UsuarioUpdateRequestBuilder {
        private String username;
        private Long rolId;
        private Boolean activo;

        public UsuarioUpdateRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UsuarioUpdateRequestBuilder rolId(Long rolId) {
            this.rolId = rolId;
            return this;
        }

        public UsuarioUpdateRequestBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public UsuarioUpdateRequest build() {
            return new UsuarioUpdateRequest(username, rolId, activo);
        }
    }
}
