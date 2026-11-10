package com.jesusnazareno.asistencia.modules.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo usuario en el sistema.
 */
public class UsuarioCreateRequest {

    @NotNull(message = "{validation.required}")
    private Long personaId;

    @NotBlank(message = "{validation.required}")
    @Size(min = 3, max = 50, message = "{validation.invalid}")
    private String username;

    @NotBlank(message = "{validation.required}")
    @Size(min = 8, max = 100, message = "{validation.invalid}")
    private String password;

    @NotNull(message = "{validation.required}")
    private Long rolId;

    public UsuarioCreateRequest() {
    }

    public UsuarioCreateRequest(Long personaId, String username, String password, Long rolId) {
        this.personaId = personaId;
        this.username = username;
        this.password = password;
        this.rolId = rolId;
    }

    public static UsuarioCreateRequestBuilder builder() {
        return new UsuarioCreateRequestBuilder();
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
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

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public static class UsuarioCreateRequestBuilder {
        private Long personaId;
        private String username;
        private String password;
        private Long rolId;

        public UsuarioCreateRequestBuilder personaId(Long personaId) {
            this.personaId = personaId;
            return this;
        }

        public UsuarioCreateRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UsuarioCreateRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UsuarioCreateRequestBuilder rolId(Long rolId) {
            this.rolId = rolId;
            return this;
        }

        public UsuarioCreateRequest build() {
            return new UsuarioCreateRequest(personaId, username, password, rolId);
        }
    }
}
