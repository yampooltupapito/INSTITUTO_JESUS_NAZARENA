package com.jesusnazareno.asistencia.modules.persona.mapper;

import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonaMapperTest {

    private PersonaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PersonaMapper();
    }

    @Test
    @DisplayName("toEntity debe mapear correctamente un PersonaRequest a Persona")
    void testToEntity() {
        PersonaRequest request = PersonaRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("García")
                .fechaNacimiento(LocalDate.of(2005, 5, 15))
                .sexo(Sexo.MASCULINO)
                .telefono("987654321")
                .correo("juan.perez@example.com")
                .direccion("Av. Lima 123")
                .build();

        Persona persona = mapper.toEntity(request);

        assertNotNull(persona);
        assertEquals(TipoDocumento.DNI, persona.getTipoDocumento());
        assertEquals("12345678", persona.getNumeroDocumento());
        assertEquals("Juan", persona.getNombres());
        assertEquals("Pérez", persona.getApellidoPaterno());
        assertEquals("García", persona.getApellidoMaterno());
        assertEquals(LocalDate.of(2005, 5, 15), persona.getFechaNacimiento());
        assertEquals(Sexo.MASCULINO, persona.getSexo());
        assertEquals("987654321", persona.getTelefono());
        assertEquals("juan.perez@example.com", persona.getCorreo());
        assertEquals("Av. Lima 123", persona.getDireccion());
        assertTrue(persona.getActivo());
    }

    @Test
    @DisplayName("toResponse debe mapear correctamente un Persona a PersonaResponse")
    void testToResponse() {
        Persona persona = Persona.builder()
                .id(1L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("87654321")
                .nombres("Maria")
                .apellidoPaterno("López")
                .apellidoMaterno("Rojas")
                .fechaNacimiento(LocalDate.of(2008, 10, 20))
                .sexo(Sexo.FEMENINO)
                .telefono("912345678")
                .correo("maria.lopez@example.com")
                .direccion("Calle Real 456")
                .activo(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PersonaResponse response = mapper.toResponse(persona);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(TipoDocumento.DNI, response.getTipoDocumento());
        assertEquals("87654321", response.getNumeroDocumento());
        assertEquals("Maria", response.getNombres());
        assertEquals("López", response.getApellidoPaterno());
        assertEquals("Rojas", response.getApellidoMaterno());
        assertTrue(response.getActivo());
    }

    @Test
    @DisplayName("toEntity con nulo debe retornar nulo")
    void testToEntityNull() {
        assertNull(mapper.toEntity(null));
    }
}
