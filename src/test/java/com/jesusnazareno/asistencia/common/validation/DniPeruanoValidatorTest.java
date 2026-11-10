package com.jesusnazareno.asistencia.common.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DniPeruanoValidatorTest {

    private DniPeruanoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DniPeruanoValidator();
    }

    @Test
    @DisplayName("DNI con 8 dígitos numéricos debe ser válido")
    void testDniValido() {
        assertTrue(validator.isValid("12345678", null));
        assertTrue(validator.isValid("87654321", null));
        assertTrue(validator.isValid("01234567", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "123456789", "1234567A", "12 345678", "1234-5678", "ABCDEFGH", "   ", " 12345678 ", "12345678 "})
    @DisplayName("DNI con longitud incorrecta, letras, espacios o guiones debe ser inválido")
    void testDniInvalido(String dniInvalido) {
        assertFalse(validator.isValid(dniInvalido, null));
    }

    @Test
    @DisplayName("DNI nulo o vacío debe considerarse válido por el validador (delegando a @NotNull/@NotBlank)")
    void testDniNuloOVacio() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("", null));
    }
}

