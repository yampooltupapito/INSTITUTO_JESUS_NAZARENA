package com.jesusnazareno.asistencia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.Map;

/**
 * Excepción para errores de validación de datos en procesos del sistema.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = Collections.emptyMap();
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors != null ? errors : Collections.emptyMap();
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
