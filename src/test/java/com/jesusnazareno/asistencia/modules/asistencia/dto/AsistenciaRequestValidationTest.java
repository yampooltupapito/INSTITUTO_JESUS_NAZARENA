package com.jesusnazareno.asistencia.modules.asistencia.dto;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsistenciaRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("AsistenciaRequest válida no produce violaciones")
    void testAsistenciaRequestValida() {
        AsistenciaRequest request = AsistenciaRequest.builder()
                .matriculaId(1L)
                .fecha(LocalDate.of(2026, 3, 10))
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 50))
                .horaSalida(LocalTime.of(13, 30))
                .observacion("Asistencia normal")
                .registradoPor(1L)
                .build();

        Set<ConstraintViolation<AsistenciaRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("AsistenciaRequest con campos obligatorios nulos produce violaciones")
    void testAsistenciaRequestCamposObligatoriosNulos() {
        AsistenciaRequest request = AsistenciaRequest.builder().build();

        Set<ConstraintViolation<AsistenciaRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        // Falla en: matriculaId, fecha, estado, registradoPor
        assertTrue(violations.size() >= 4);
    }
}
