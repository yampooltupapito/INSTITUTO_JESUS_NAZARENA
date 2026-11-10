package com.jesusnazareno.asistencia.modules.matricula.dto;

import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatriculaRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("MatriculaRequest con datos válidos no genera violaciones")
    void testMatriculaRequestValida() {
        MatriculaRequest request = MatriculaRequest.builder()
                .personaEstudianteId(1L)
                .anioEscolarId(1L)
                .gradoId(1L)
                .seccionId(1L)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 3, 1))
                .estado(EstadoMatricula.MATRICULADO)
                .observaciones("Estudiante regular")
                .build();

        Set<ConstraintViolation<MatriculaRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("MatriculaRequest con campos obligatorios nulos genera violaciones")
    void testMatriculaRequestInvalidaCamposObligatorios() {
        MatriculaRequest request = MatriculaRequest.builder().build();

        Set<ConstraintViolation<MatriculaRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        // Debe fallar en personaEstudianteId, anioEscolarId, gradoId, seccionId, codigoMatricula, fechaMatricula
        assertTrue(violations.size() >= 5);
    }
}
