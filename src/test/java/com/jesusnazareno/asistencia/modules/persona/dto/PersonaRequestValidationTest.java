package com.jesusnazareno.asistencia.modules.persona.dto;

import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonaRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("PersonaRequest con datos válidos no debe generar violaciones de validación")
    void testPersonaRequestValida() {
        PersonaRequest request = PersonaRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Carlos")
                .apellidoPaterno("Sánchez")
                .apellidoMaterno("Mendoza")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .sexo(Sexo.MASCULINO)
                .telefono("987654321")
                .correo("carlos.sanchez@example.com")
                .direccion("Av. Sol 789")
                .build();

        Set<ConstraintViolation<PersonaRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("PersonaRequest sin campos obligatorios debe generar violaciones")
    void testPersonaRequestInvalidaCamposObligatorios() {
        PersonaRequest request = PersonaRequest.builder().build();

        Set<ConstraintViolation<PersonaRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 5);
    }

    @Test
    @DisplayName("PersonaRequest con correo o teléfono inválido debe generar violaciones")
    void testPersonaRequestTelefonoYCorreoInvalidos() {
        PersonaRequest request = PersonaRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Carlos")
                .apellidoPaterno("Sánchez")
                .apellidoMaterno("Mendoza")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .sexo(Sexo.MASCULINO)
                .telefono("12345") // Inválido
                .correo("correo_invalido") // Inválido
                .build();

        Set<ConstraintViolation<PersonaRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }
}
