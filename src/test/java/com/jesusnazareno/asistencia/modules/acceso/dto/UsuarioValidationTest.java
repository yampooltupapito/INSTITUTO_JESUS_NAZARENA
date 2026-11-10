package com.jesusnazareno.asistencia.modules.acceso.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("UsuarioCreateRequest válido no genera violaciones")
    void usuarioCreateRequest_valido_sinViolaciones() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(1L, "admin", "Password123", 1L);

        Set<ConstraintViolation<UsuarioCreateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("UsuarioCreateRequest con campos nulos o vacíos genera violaciones")
    void usuarioCreateRequest_camposInvalidos_generaViolaciones() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(null, " ", "123", null);

        Set<ConstraintViolation<UsuarioCreateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("personaId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("rolId")));
    }

    @Test
    @DisplayName("CambioPasswordRequest válido no genera violaciones")
    void cambioPasswordRequest_valido_sinViolaciones() {
        CambioPasswordRequest request = new CambioPasswordRequest("Actual123", "NuevaClave456");

        Set<ConstraintViolation<CambioPasswordRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("CambioPasswordRequest con clave nueva corta genera violaciones")
    void cambioPasswordRequest_claveNuevaCorta_generaViolaciones() {
        CambioPasswordRequest request = new CambioPasswordRequest("Actual123", "123");

        Set<ConstraintViolation<CambioPasswordRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("passwordNueva")));
    }
}
