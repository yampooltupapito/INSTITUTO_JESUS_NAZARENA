package com.jesusnazareno.asistencia.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para validar que una cadena sea un DNI peruano válido (exactamente 8 dígitos numéricos).
 */
@Documented
@Constraint(validatedBy = DniPeruanoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DniPeruano {

    String message() default "{validation.dni}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
