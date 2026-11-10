package com.jesusnazareno.asistencia.modules.asistencia.dto;

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

class JustificacionRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("JustificacionRequest válida no genera violaciones")
    void testJustificacionRequestValida() {
        JustificacionRequest request = JustificacionRequest.builder()
                .asistenciaId(1L)
                .motivo("Cita Médica")
                .descripcion("Constancia adjunta")
                .build();

        Set<ConstraintViolation<JustificacionRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("JustificacionRequest con campos obligatorios vacíos genera violaciones")
    void testJustificacionRequestCamposObligatoriosNulos() {
        JustificacionRequest request = JustificacionRequest.builder().build();

        Set<ConstraintViolation<JustificacionRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        // Falla en: asistenciaId, motivo
        assertTrue(violations.size() >= 2);
    }

    @Test
    @DisplayName("RevisionJustificacionRequest válida no genera violaciones")
    void testRevisionJustificacionRequestValida() {
        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(1L)
                .observacionRevision("Aprobado")
                .build();

        Set<ConstraintViolation<RevisionJustificacionRequest>> violations = validator.validate(revision);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("RevisionJustificacionRequest sin revisadoPor genera violaciones")
    void testRevisionJustificacionRequestSinRevisadoPor() {
        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder().build();

        Set<ConstraintViolation<RevisionJustificacionRequest>> violations = validator.validate(revision);
        assertFalse(violations.isEmpty());
    }
}
