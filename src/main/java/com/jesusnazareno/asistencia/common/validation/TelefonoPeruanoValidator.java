package com.jesusnazareno.asistencia.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador para la anotación @TelefonoPeruano.
 * Reglas:
 * - Si el valor es nulo o vacío, se considera válido (la presencia requerida se delega a @NotNull/@NotBlank).
 * - Debe tener exactamente 9 dígitos y comenzar con '9'.
 * - No se aceptan espacios ni caracteres no numéricos.
 */
public class TelefonoPeruanoValidator implements ConstraintValidator<TelefonoPeruano, String> {

    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^9[0-9]{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return TELEFONO_PATTERN.matcher(value).matches();
    }
}

