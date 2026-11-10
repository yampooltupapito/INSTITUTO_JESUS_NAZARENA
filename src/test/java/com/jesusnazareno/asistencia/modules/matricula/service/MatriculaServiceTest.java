package com.jesusnazareno.asistencia.modules.matricula.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaResponse;
import com.jesusnazareno.asistencia.modules.matricula.mapper.MatriculaMapper;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.matricula.dto.SeccionCapacidadDTO;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private EstudianteService estudianteService;

    @Mock
    private AnioEscolarRepository anioEscolarRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private SeccionRepository seccionRepository;

    private MatriculaMapper matriculaMapper;
    private MatriculaServiceImpl matriculaService;

    private Persona estudiante;
    private AnioEscolar anioEscolar;
    private Grado grado;
    private Seccion seccion;
    private MatriculaRequest requestValido;

    @BeforeEach
    void setUp() {
        matriculaMapper = new MatriculaMapper();
        matriculaService = new MatriculaServiceImpl(
                matriculaRepository,
                matriculaMapper,
                estudianteService,
                anioEscolarRepository,
                gradoRepository,
                seccionRepository
        );

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

        requestValido = MatriculaRequest.builder()
                .personaEstudianteId(1L)
                .anioEscolarId(2L)
                .gradoId(3L)
                .seccionId(4L)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .observaciones("Matrícula regular")
                .build();
    }

    @Test
    @DisplayName("crearMatricula con estudiante válido registra matrícula exitosamente")
    void crearMatricula_estudianteValido_registraMatriculaCorrectamente() {
        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.of(anioEscolar));
        when(gradoRepository.findById(3L)).thenReturn(Optional.of(grado));
        when(seccionRepository.findById(4L)).thenReturn(Optional.of(seccion));
        when(matriculaRepository.existsByEstudianteIdAndAnioEscolarId(1L, 2L)).thenReturn(false);
        when(matriculaRepository.existsByCodigoMatricula("MAT-2026-001")).thenReturn(false);
        when(matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(4L, 2L, EstadoMatricula.MATRICULADO)).thenReturn(10L);

        Matricula matriculaGuardada = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .build();
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(matriculaGuardada);

        MatriculaResponse response = matriculaService.crearMatricula(requestValido);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("MAT-2026-001", response.getCodigoMatricula());
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    @DisplayName("crearMatricula con persona sin rol estudiante lanza BusinessException")
    void crearMatricula_personaSinActorEstudiante_lanzaBusinessException() {
        when(estudianteService.obtenerEntidadEstudiantePorId(1L))
                .thenThrow(new BusinessException("La persona no tiene el perfil de estudiante asignado."));

        assertThrows(BusinessException.class, () -> matriculaService.crearMatricula(requestValido));
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    @DisplayName("crearMatricula cuando año escolar no existe lanza ResourceNotFoundException")
    void crearMatricula_anioEscolarNoExiste_lanzaResourceNotFoundException() {
        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> matriculaService.crearMatricula(requestValido));
    }

    @Test
    @DisplayName("crearMatricula cuando año escolar está inactivo lanza BusinessException")
    void crearMatricula_anioEscolarInactivo_lanzaBusinessException() {
        anioEscolar.setActivo(false);
        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.of(anioEscolar));

        assertThrows(BusinessException.class, () -> matriculaService.crearMatricula(requestValido));
    }

    @Test
    @DisplayName("crearMatricula cuando sección no pertenece al grado lanza BusinessException")
    void crearMatricula_seccionNoPerteneceAlGrado_lanzaBusinessException() {
        Grado otroGrado = Grado.builder().id(99L).nivel(NivelEducativo.PRIMARIA).numero(6).activo(true).build();
        Seccion seccionDeOtroGrado = Seccion.builder().id(4L).grado(otroGrado).nombre("A").capacidad(30).activo(true).build();

        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.of(anioEscolar));
        when(gradoRepository.findById(3L)).thenReturn(Optional.of(grado));
        when(seccionRepository.findById(4L)).thenReturn(Optional.of(seccionDeOtroGrado));

        assertThrows(BusinessException.class, () -> matriculaService.crearMatricula(requestValido));
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    @DisplayName("crearMatricula con doble matrícula para estudiante en mismo año lanza BusinessException")
    void crearMatricula_matriculaDuplicadaEstudianteAnio_lanzaBusinessException() {
        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.of(anioEscolar));
        when(gradoRepository.findById(3L)).thenReturn(Optional.of(grado));
        when(seccionRepository.findById(4L)).thenReturn(Optional.of(seccion));
        when(matriculaRepository.existsByEstudianteIdAndAnioEscolarId(1L, 2L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> matriculaService.crearMatricula(requestValido));
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    @DisplayName("crearMatricula con código de matrícula duplicado lanza BusinessException")
    void crearMatricula_codigoMatriculaDuplicado_lanzaBusinessException() {
        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.of(anioEscolar));
        when(gradoRepository.findById(3L)).thenReturn(Optional.of(grado));
        when(seccionRepository.findById(4L)).thenReturn(Optional.of(seccion));
        when(matriculaRepository.existsByEstudianteIdAndAnioEscolarId(1L, 2L)).thenReturn(false);
        when(matriculaRepository.existsByCodigoMatricula("MAT-2026-001")).thenReturn(true);

        assertThrows(BusinessException.class, () -> matriculaService.crearMatricula(requestValido));
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    @DisplayName("crearMatricula cuando sección supera o iguala capacidad lanza BusinessException")
    void crearMatricula_seccionSinCapacidadDisponible_lanzaBusinessException() {
        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.of(anioEscolar));
        when(gradoRepository.findById(3L)).thenReturn(Optional.of(grado));
        when(seccionRepository.findById(4L)).thenReturn(Optional.of(seccion));
        when(matriculaRepository.existsByEstudianteIdAndAnioEscolarId(1L, 2L)).thenReturn(false);
        when(matriculaRepository.existsByCodigoMatricula("MAT-2026-001")).thenReturn(false);
        // Capacidad de la sección es 30, y ya hay 30 matriculados activos
        when(matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(4L, 2L, EstadoMatricula.MATRICULADO))
                .thenReturn(30L);

        assertThrows(BusinessException.class, () -> matriculaService.crearMatricula(requestValido));
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    @DisplayName("buscarPorId retorna matrícula cuando existe")
    void buscarPorId_cuandoExiste_retornaMatriculaResponse() {
        Matricula matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .build();
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));

        MatriculaResponse response = matriculaService.buscarPorId(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("MAT-2026-001", response.getCodigoMatricula());
    }

    @Test
    @DisplayName("buscarPorId lanza ResourceNotFoundException cuando no existe")
    void buscarPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(matriculaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> matriculaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("cambiarEstado a RETIRADO actualiza estado correctamente")
    void cambiarEstado_aRetirado_actualizaEstadoCorrectamente() {
        Matricula matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .build();
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        MatriculaResponse response = matriculaService.cambiarEstado(10L, EstadoMatricula.RETIRADO);

        assertNotNull(response);
        assertEquals(EstadoMatricula.RETIRADO, response.getEstado());
        verify(matriculaRepository).save(matricula);
    }

    @Test
    @DisplayName("buscarMatriculas retorna página de DTOs correctamente")
    void buscarMatriculas_retornaPaginaCorrecta() {
        Matricula matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .build();

        Page<Matricula> paginaEntidades = new PageImpl<>(List.of(matricula));
        Pageable pageable = PageRequest.of(0, 10);

        when(matriculaRepository.buscarMatriculas(eq("MAT"), eq(2L), eq(3L), eq(4L), eq(EstadoMatricula.MATRICULADO), eq(pageable)))
                .thenReturn(paginaEntidades);

        Page<MatriculaResponse> resultado = matriculaService.buscarMatriculas("MAT", 2L, 3L, 4L, EstadoMatricula.MATRICULADO, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("MAT-2026-001", resultado.getContent().get(0).getCodigoMatricula());
        verify(matriculaRepository).buscarMatriculas(eq("MAT"), eq(2L), eq(3L), eq(4L), eq(EstadoMatricula.MATRICULADO), eq(pageable));
    }

    @Test
    @DisplayName("listarSeccionesConCapacidad calcula vacantes correctamente")
    void listarSeccionesConCapacidad_calculaVacantesCorrectamente() {
        when(seccionRepository.findByGradoIdAndActivoTrue(3L)).thenReturn(List.of(seccion));
        when(matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(4L, 2L, EstadoMatricula.MATRICULADO)).thenReturn(22L);

        List<SeccionCapacidadDTO> dto = matriculaService.listarSeccionesConCapacidad(3L, 2L);

        assertNotNull(dto);
        assertEquals(1, dto.size());
        assertEquals(4L, dto.get(0).getId());
        assertEquals("A", dto.get(0).getNombre());
        assertEquals(30, dto.get(0).getCapacidad());
        assertEquals(22, dto.get(0).getMatriculados());
        assertEquals(8, dto.get(0).getVacantes());
    }

    @Test
    @DisplayName("actualizarMatricula con datos válidos actualiza y retorna response")
    void actualizarMatricula_conDatosValidos_actualizaCorrectamente() {
        Matricula matriculaExistente = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .build();

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matriculaExistente));
        when(estudianteService.obtenerEntidadEstudiantePorId(1L)).thenReturn(estudiante);
        when(anioEscolarRepository.findById(2L)).thenReturn(Optional.of(anioEscolar));
        when(gradoRepository.findById(3L)).thenReturn(Optional.of(grado));
        when(seccionRepository.findById(4L)).thenReturn(Optional.of(seccion));
        when(matriculaRepository.findByEstudianteIdAndAnioEscolarId(1L, 2L)).thenReturn(Optional.of(matriculaExistente));
        when(matriculaRepository.findByCodigoMatricula("MAT-2026-001")).thenReturn(Optional.of(matriculaExistente));
        when(matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(4L, 2L, EstadoMatricula.MATRICULADO)).thenReturn(10L);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        MatriculaResponse response = matriculaService.actualizarMatricula(10L, requestValido);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        verify(matriculaRepository).save(matriculaExistente);
    }
}
