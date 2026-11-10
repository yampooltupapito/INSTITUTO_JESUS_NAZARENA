package com.jesusnazareno.asistencia.exception;

import com.jesusnazareno.asistencia.common.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("handleResourceNotFoundException debe retornar 404 NOT FOUND")
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Alumno no encontrado.");
        ResponseEntity<ApiError> response = exceptionHandler.handleResourceNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Alumno no encontrado.", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("handleBusinessException debe retornar 400 BAD REQUEST")
    void testBusinessException() {
        BusinessException ex = new BusinessException("No se puede realizar la operación solicitada.");
        ResponseEntity<ApiError> response = exceptionHandler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("No se puede realizar la operación solicitada.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleValidationException debe retornar 400 BAD REQUEST con validationErrors")
    void testValidationException() {
        Map<String, String> errors = Map.of("dni", "El DNI debe contener exactamente 8 dígitos.");
        ValidationException ex = new ValidationException("Los datos ingresados no son válidos.", errors);
        ResponseEntity<ApiError> response = exceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Los datos ingresados no son válidos.", response.getBody().getMessage());
        assertEquals("El DNI debe contener exactamente 8 dígitos.", response.getBody().getValidationErrors().get("dni"));
    }

    @Test
    @DisplayName("handleUnauthorizedException debe retornar 401 UNAUTHORIZED")
    void testUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("No está autorizado para realizar esta operación.");
        ResponseEntity<ApiError> response = exceptionHandler.handleUnauthorizedException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    @DisplayName("handleForbiddenException debe retornar 403 FORBIDDEN")
    void testForbiddenException() {
        ForbiddenException ex = new ForbiddenException("No tiene permisos para realizar esta operación.");
        ResponseEntity<ApiError> response = exceptionHandler.handleForbiddenException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    @DisplayName("handleAllUncaughtException debe retornar 500 INTERNAL SERVER ERROR sin exponer stacktrace")
    void testGeneralException() {
        Exception ex = new RuntimeException("Error interno confidencial de BD");
        ResponseEntity<ApiError> response = exceptionHandler.handleAllUncaughtException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Ha ocurrido un error inesperado en el servidor.", response.getBody().getMessage());
    }
}
