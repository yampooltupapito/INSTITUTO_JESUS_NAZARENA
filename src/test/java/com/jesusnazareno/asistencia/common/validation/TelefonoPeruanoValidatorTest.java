package com.jesusnazareno.asistencia.common.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelefonoPeruanoValidatorTest {

    private TelefonoPeruanoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TelefonoPeruanoValidator();
    }

    @Test
    @DisplayName("Teléfono celular peruano válido (9 dígitos iniciando con 9)")
    void testTelefonoValido() {
        assertTrue(validator.isValid("987654321", null));
        assertTrue(validator.isValid("912345678", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"98765432", "9876543210", "123456789", "887654321", "98765432A", "9876 54321", "   ", " 987654321 ", "987654321 "})
    @DisplayName("Teléfono celular peruano inválido (longitud errónea, no empieza con 9 o contiene letras/espacios)")
    void testTelefonoInvalido(String telefonoInvalido) {
        assertFalse(validator.isValid(telefonoInvalido, null));
    }

    @Test
    @DisplayName("Teléfono nulo o vacío debe considerarse válido por el validador (delegando a @NotNull/@NotBlank)")
    void testTelefonoNuloOVacio() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("", null));
    }
}

