package com.jesusnazareno.asistencia.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para validar teléfonos móviles peruanos (9 dígitos, comenzando con el número 9).
 */
@Documented
@Constraint(validatedBy = TelefonoPeruanoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefonoPeruano {

    String message() default "{validation.phone}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
