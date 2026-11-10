package com.jesusnazareno.asistencia.modules.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambioPasswordRequest {

    @NotBlank(message = "{usuario.password-actual.not-blank}")
    private String passwordActual;

    @NotBlank(message = "{usuario.password-nueva.not-blank}")
    @Size(min = 6, max = 100, message = "{usuario.password-nueva.size}")
    private String passwordNueva;

    public CambioPasswordRequest() {
    }

    public CambioPasswordRequest(String passwordActual, String passwordNueva) {
        this.passwordActual = passwordActual;
        this.passwordNueva = passwordNueva;
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNueva() {
        return passwordNueva;
    }

    public void setPasswordNueva(String passwordNueva) {
        this.passwordNueva = passwordNueva;
    }
}
