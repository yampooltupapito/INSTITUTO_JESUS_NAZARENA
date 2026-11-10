package com.jesusnazareno.asistencia.modules.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Formulario para el cambio administrativo de contraseña de un usuario.
 */
public class UsuarioPasswordForm {

    @NotBlank(message = "{validation.required}")
    @Size(min = 8, max = 100, message = "{validation.invalid}")
    private String nuevaPassword;

    @NotBlank(message = "{validation.required}")
    @Size(min = 8, max = 100, message = "{validation.invalid}")
    private String confirmarPassword;

    public UsuarioPasswordForm() {
    }

    public UsuarioPasswordForm(String nuevaPassword, String confirmarPassword) {
        this.nuevaPassword = nuevaPassword;
        this.confirmarPassword = confirmarPassword;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}
