package com.jesusnazareno.asistencia.modules.matricula.mapper;

import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaResponse;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MatriculaMapperTest {

    private MatriculaMapper mapper;
    private Persona estudiante;
    private AnioEscolar anioEscolar;
    private Grado grado;
    private Seccion seccion;

    @BeforeEach
    void setUp() {
        mapper = new MatriculaMapper();

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

        anioEscolar = AnioEscolar.builder()
                .id(2L)
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .nombre("Año Escolar 2026")
                .activo(true)
                .build();

        grado = Grado.builder()
                .id(3L)
                .nivel(NivelEducativo.SECUNDARIA)
                .numero(1)
                .nombre("Primer Grado de Secundaria")
                .activo(true)
                .build();

        seccion = Seccion.builder()
                .id(4L)
                .grado(grado)
                .nombre("A")
                .turno(Turno.MANANA)
                .capacidad(30)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("toEntity mapea correctamente los campos")
    void toEntity_mapeaCorrectamente() {
        MatriculaRequest request = MatriculaRequest.builder()
                .personaEstudianteId(1L)
                .anioEscolarId(2L)
                .gradoId(3L)
                .seccionId(4L)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .observaciones("Matrícula regular")
                .build();

        Matricula matricula = mapper.toEntity(request, estudiante, anioEscolar, grado, seccion);

        assertNotNull(matricula);
        assertEquals(estudiante, matricula.getEstudiante());
        assertEquals(anioEscolar, matricula.getAnioEscolar());
        assertEquals(grado, matricula.getGrado());
        assertEquals(seccion, matricula.getSeccion());
        assertEquals("MAT-2026-001", matricula.getCodigoMatricula());
        assertEquals(LocalDate.of(2026, 2, 15), matricula.getFechaMatricula());
        assertEquals(EstadoMatricula.MATRICULADO, matricula.getEstado());
        assertEquals("Matrícula regular", matricula.getObservaciones());
    }

    @Test
    @DisplayName("toResponse mapea correctamente los campos y genera nombre completo")
    void toResponse_mapeaCorrectamente() {
        Matricula matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-002")
                .fechaMatricula(LocalDate.of(2026, 2, 20))
                .estado(EstadoMatricula.MATRICULADO)
                .observaciones("Sin observaciones")
                .build();

        MatriculaResponse response = mapper.toResponse(matricula);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getPersonaEstudianteId());
        assertEquals("Pérez Gómez, Juan", response.getEstudianteNombreCompleto());
        assertEquals("12345678", response.getEstudianteNumeroDocumento());
        assertEquals(2L, response.getAnioEscolarId());
        assertEquals(2026, response.getAnioEscolarAnio());
        assertEquals(3L, response.getGradoId());
        assertEquals("Primer Grado de Secundaria", response.getGradoNombre());
        assertEquals(4L, response.getSeccionId());
        assertEquals("A", response.getSeccionNombre());
        assertEquals("MAT-2026-002", response.getCodigoMatricula());
        assertEquals(EstadoMatricula.MATRICULADO, response.getEstado());
    }

    @Test
    @DisplayName("toEntity y toResponse manejan nulos con seguridad")
    void nullSafety() {
        assertNull(mapper.toEntity(null, null, null, null, null));
        assertNull(mapper.toResponse(null));
    }
}
