package com.jesusnazareno.asistencia.modules.asistencia.mapper;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionResponse;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JustificacionMapperTest {

    private JustificacionMapper mapper;
    private Asistencia asistencia;
    private Usuario usuarioRevisor;

    @BeforeEach
    void setUp() {
        mapper = new JustificacionMapper();

        usuarioRevisor = Usuario.builder()
                .id(2L)
                .username("director")
                .activo(true)
                .build();

        Persona estudiante = Persona.builder()
                .id(1L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .fechaNacimiento(LocalDate.of(2012, 3, 10))
                .sexo(Sexo.MASCULINO)
                .activo(true)
                .build();

        Matricula matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .build();

        asistencia = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.AUSENTE)
                .build();
    }

    @Test
    @DisplayName("toEntity mapea correctamente con estado inicial PENDIENTE y activo true")
    void toEntity_mapeaCorrectamente() {
        JustificacionRequest request = JustificacionRequest.builder()
                .asistenciaId(100L)
                .motivo("Cita Médica")
                .descripcion("Atención por odontología")
                .build();

        Justificacion entity = mapper.toEntity(request, asistencia);

        assertNotNull(entity);
        assertEquals(asistencia, entity.getAsistencia());
        assertEquals("Cita Médica", entity.getMotivo());
        assertEquals("Atención por odontología", entity.getDescripcion());
        assertEquals(EstadoJustificacion.PENDIENTE, entity.getEstado());
        assertTrue(entity.getActivo());
        assertEquals(LocalDate.now(), entity.getFechaSolicitud());
    }

    @Test
    @DisplayName("toResponse mapea datos de la justificación y asistencia asociada")
    void toResponse_mapeaCorrectamente() {
        Justificacion entity = Justificacion.builder()
                .id(50L)
                .asistencia(asistencia)
                .motivo("Cita Médica")
                .descripcion("Atención médica general")
                .fechaSolicitud(LocalDate.of(2026, 3, 16))
                .estado(EstadoJustificacion.APROBADA)
                .observacionRevision("Certificado médico validado")
                .revisadoPor(usuarioRevisor)
                .fechaRevision(LocalDateTime.of(2026, 3, 16, 10, 30))
                .activo(true)
                .build();

        JustificacionResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals(100L, response.getAsistenciaId());
        assertEquals(1L, response.getEstudianteId());
        assertEquals("Pérez Gómez, Juan", response.getEstudianteNombreCompleto());
        assertEquals(LocalDate.of(2026, 3, 15), response.getAsistenciaFecha());
        assertEquals(EstadoAsistencia.AUSENTE, response.getAsistenciaEstado());
        assertEquals("Cita Médica", response.getMotivo());
        assertEquals(EstadoJustificacion.APROBADA, response.getEstado());
        assertEquals("Certificado médico validado", response.getObservacionRevision());
        assertEquals(2L, response.getRevisadoPor());
    }

    @Test
    @DisplayName("nullSafety maneja valores nulos sin lanzar excepción")
    void nullSafety() {
        assertNull(mapper.toEntity(null, null));
        assertNull(mapper.toResponse(null));
    }
}
