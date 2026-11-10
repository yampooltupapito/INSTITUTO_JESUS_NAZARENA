package com.jesusnazareno.asistencia.modules.persona.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.mapper.PersonaMapper;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    private PersonaMapper personaMapper;
    private PersonaServiceImpl personaService;

    private PersonaRequest requestValido;
    private Persona personaGuardada;

    @BeforeEach
    void setUp() {
        personaMapper = new PersonaMapper();
        personaService = new PersonaServiceImpl(personaRepository, personaMapper);

        requestValido = PersonaRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Ana")
                .apellidoPaterno("Torres")
                .apellidoMaterno("Vargas")
                .fechaNacimiento(LocalDate.of(2002, 3, 10))
                .sexo(Sexo.FEMENINO)
                .telefono("987654321")
                .correo("ana.torres@example.com")
                .build();

        personaGuardada = Persona.builder()
                .id(10L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Ana")
                .apellidoPaterno("Torres")
                .apellidoMaterno("Vargas")
                .fechaNacimiento(LocalDate.of(2002, 3, 10))
                .sexo(Sexo.FEMENINO)
                .telefono("987654321")
                .correo("ana.torres@example.com")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("crearPersona debe registrar y retornar la persona correctamente")
    void testCrearPersonaExitoso() {
        when(personaRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.empty());
        when(personaRepository.save(any(Persona.class))).thenReturn(personaGuardada);

        PersonaResponse response = personaService.crearPersona(requestValido);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("12345678", response.getNumeroDocumento());
        verify(personaRepository).save(any(Persona.class));
    }

    @Test
    @DisplayName("crearPersona con número de documento duplicado debe lanzar BusinessException")
    void testCrearPersonaDocumentoDuplicado() {
        when(personaRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(personaGuardada));

        assertThrows(BusinessException.class, () -> personaService.crearPersona(requestValido));
    }

    @Test
    @DisplayName("crearPersona con DNI de formato inválido (7 dígitos) debe lanzar ValidationException")
    void testCrearPersonaDniInvalido() {
        PersonaRequest requestBadDni = PersonaRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("1234567") // Inválido (7 dígitos)
                .nombres("Ana")
                .apellidoPaterno("Torres")
                .apellidoMaterno("Vargas")
                .fechaNacimiento(LocalDate.of(2002, 3, 10))
                .sexo(Sexo.FEMENINO)
                .build();

        assertThrows(ValidationException.class, () -> personaService.crearPersona(requestBadDni));
    }

    @Test
    @DisplayName("buscarPorId inexistente debe lanzar ResourceNotFoundException")
    void testBuscarPorIdInexistente() {
        when(personaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("desactivarPersona debe cambiar el estado activo a false")
    void testDesactivarPersona() {
        when(personaRepository.findById(10L)).thenReturn(Optional.of(personaGuardada));

        personaService.desactivarPersona(10L);

        assertFalse(personaGuardada.getActivo());
        verify(personaRepository).save(personaGuardada);
    }
}
