package com.jesusnazareno.asistencia.modules.asistencia.mapper;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AsistenciaMapperTest {

    private AsistenciaMapper mapper;
    private Matricula matricula;
    private Persona estudiante;
    private Grado grado;
    private Seccion seccion;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        mapper = new AsistenciaMapper();

        usuario = Usuario.builder()
                .id(1L)
                .username("auxiliar")
                .activo(true)
                .build();

        estudiante = Persona.builder()
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

        grado = Grado.builder()
                .id(2L)
                .nivel(NivelEducativo.SECUNDARIA)
                .numero(1)
                .nombre("Primer Grado de Secundaria")
                .activo(true)
                .build();

        seccion = Seccion.builder()
                .id(3L)
                .grado(grado)
                .nombre("A")
                .turno(Turno.MANANA)
                .capacidad(30)
                .activo(true)
                .build();

        matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .build();
    }

    @Test
    @DisplayName("toEntity mapea correctamente los campos")
    void toEntity_mapeaCorrectamente() {
        AsistenciaRequest request = AsistenciaRequest.builder()
                .matriculaId(10L)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 55))
                .horaSalida(LocalTime.of(13, 30))
                .observacion("Llegada puntual")
                .registradoPor(1L)
                .build();

        Asistencia entity = mapper.toEntity(request, matricula, usuario);

        assertNotNull(entity);
        assertEquals(matricula, entity.getMatricula());
        assertEquals(LocalDate.of(2026, 3, 15), entity.getFecha());
        assertEquals(EstadoAsistencia.PRESENTE, entity.getEstado());
        assertEquals(LocalTime.of(7, 55), entity.getHoraIngreso());
        assertEquals(LocalTime.of(13, 30), entity.getHoraSalida());
        assertEquals("Llegada puntual", entity.getObservacion());
        assertEquals(usuario, entity.getRegistradoPor());
    }

    @Test
    @DisplayName("toResponse genera DTO con nombres estructurados")
    void toResponse_mapeaCorrectamente() {
        Asistencia entity = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.TARDANZA)
                .horaIngreso(LocalTime.of(8, 15))
                .observacion("Tráfico")
                .registradoPor(usuario)
                .build();

        AsistenciaResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(10L, response.getMatriculaId());
        assertEquals(1L, response.getEstudianteId());
        assertEquals("Pérez Gómez, Juan", response.getEstudianteNombreCompleto());
        assertEquals("12345678", response.getEstudianteNumeroDocumento());
        assertEquals("Primer Grado de Secundaria", response.getGradoNombre());
        assertEquals("A", response.getSeccionNombre());
        assertEquals(EstadoAsistencia.TARDANZA, response.getEstado());
        assertEquals(LocalTime.of(8, 15), response.getHoraIngreso());
        assertEquals(1L, response.getRegistradoPor());
    }

    @Test
    @DisplayName("updateEntity actualiza únicamente campos controlados")
    void updateEntity_actualizaCamposControlados() {
        Asistencia entity = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 50))
                .registradoPor(usuario)
                .build();

        AsistenciaRequest updateRequest = AsistenciaRequest.builder()
                .estado(EstadoAsistencia.JUSTIFICADO)
                .horaIngreso(null)
                .observacion("Permiso médico")
                .build();

        mapper.updateEntity(updateRequest, entity);

        assertEquals(EstadoAsistencia.JUSTIFICADO, entity.getEstado());
        assertNull(entity.getHoraIngreso());
        assertEquals("Permiso médico", entity.getObservacion());
        // Matrícula y fecha permanecen inalteradas
        assertEquals(matricula, entity.getMatricula());
        assertEquals(LocalDate.of(2026, 3, 15), entity.getFecha());
    }

    @Test
    @DisplayName("Manejo seguro de valores nulos")
    void nullSafety() {
        assertNull(mapper.toEntity(null, null, null));
        assertNull(mapper.toResponse(null));
    }
}
