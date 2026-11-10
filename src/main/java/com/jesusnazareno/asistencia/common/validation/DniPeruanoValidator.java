package com.jesusnazareno.asistencia.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador para la anotación @DniPeruano.
 * Reglas:
 * - Si el valor es nulo o vacío, se considera válido (la presencia requerida se delega a @NotNull/@NotBlank).
 * - Debe ser una cadena de exactamente 8 dígitos numéricos del 0 al 9.
 * - No se aceptan espacios, letras ni guiones.
 */
public class DniPeruanoValidator implements ConstraintValidator<DniPeruano, String> {

    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return DNI_PATTERN.matcher(value).matches();
    }
}

