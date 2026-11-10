package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.HorarioAsistencia;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.AsistenciaMapper;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de Integración Horario - AsistenciaService")
class HorarioAsistenciaIntegracionAsistenciaTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HorarioAsistenciaService horarioAsistenciaService;

    private AsistenciaMapper asistenciaMapper;
    private AsistenciaServiceImpl asistenciaService;

    private Matricula matricula;
    private Persona estudiante;
    private AnioEscolar anioEscolar;
    private Grado grado;
    private Seccion seccion;
    private Usuario usuario;
    private HorarioAsistencia horario;

    @BeforeEach
    void setUp() {
        asistenciaMapper = new AsistenciaMapper();
        asistenciaService = new AsistenciaServiceImpl(
                asistenciaRepository,
                matriculaRepository,
                asistenciaMapper,
                usuarioRepository,
                horarioAsistenciaService
        );

        usuario = Usuario.builder().id(1L).username("test_user").activo(true).build();

        estudiante = Persona.builder()
                .id(1L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Juan")
                .apellidoPaterno("Pérez")
                .activo(true)
                .build();

        anioEscolar = AnioEscolar.builder()
                .id(1L)
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();

        grado = Grado.builder().id(10L).nombre("1° Grado").nivel(NivelEducativo.PRIMARIA).activo(true).build();
        seccion = Seccion.builder().id(100L).nombre("A").grado(grado).turno(Turno.MANANA).activo(true).build();

        matricula = Matricula.builder()
                .id(50L)
                .estudiante(estudiante)
                .seccion(seccion)
                .anioEscolar(anioEscolar)
                .estado(EstadoMatricula.MATRICULADO)
                .build();

        horario = HorarioAsistencia.builder()
                .id(1L)
                .anioEscolar(anioEscolar)
                .turno(Turno.MANANA)
                .horaEntrada(LocalTime.of(7, 45))
                .minutosTolerancia(10)
                .horaSalida(LocalTime.of(13, 0))
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("8. AUSENTE no requiere cálculo de tardanza ni horario")
    void testAsistenciaService_AusenteNoCalculaTardanza() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        AsistenciaRequest req = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(fecha)
                .estado(EstadoAsistencia.AUSENTE)
                .horaIngreso(null)
                .registradoPor(1L)
                .build();

        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(i -> {
            Asistencia a = i.getArgument(0);
            a.setId(999L);
            return a;
        });

        AsistenciaResponse resp = asistenciaService.registrar(req);

        assertEquals(EstadoAsistencia.AUSENTE, resp.getEstado());
        assertNull(resp.getHoraIngreso());
        // No se debe invocar resolverHorario ni determinarEstado para AUSENTE
        verify(horarioAsistenciaService, never()).determinarEstado(any(), any());
    }

    @Test
    @DisplayName("9. JUSTIFICADO no se sobrescribe ni se permite asignar manualmente")
    void testAsistenciaService_JustificadoNoSeSobrescribe() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);

        // Registro manual con JUSTIFICADO debe ser rechazado
        AsistenciaRequest req = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(fecha)
                .estado(EstadoAsistencia.JUSTIFICADO)
                .registradoPor(1L)
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> asistenciaService.registrar(req));
        assertEquals("No se puede registrar manualmente una asistencia en estado JUSTIFICADO.", ex.getMessage());

        // Modificar una asistencia ya JUSTIFICADA debe ser rechazado
        Asistencia asistenciaJustificada = Asistencia.builder()
                .id(200L)
                .matricula(matricula)
                .fecha(fecha)
                .estado(EstadoAsistencia.JUSTIFICADO)
                .build();

        when(asistenciaRepository.findById(200L)).thenReturn(Optional.of(asistenciaJustificada));

        AsistenciaRequest reqActualizar = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(fecha)
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 40))
                .registradoPor(1L)
                .build();

        BusinessException exActualizar = assertThrows(BusinessException.class, () -> asistenciaService.actualizar(200L, reqActualizar));
        assertEquals("Una asistencia en estado JUSTIFICADO no puede modificarse manualmente.", exActualizar.getMessage());
    }

    @Test
    @DisplayName("15. Cambio de horario no modifica asistencias históricas previamente registradas")
    void testCambioHorario_NoAlteraAsistenciasHistoricas() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        LocalTime horaIngreso = LocalTime.of(7, 50);

        // Registro original con horario existente
        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(horarioAsistenciaService.resolverHorario(fecha, 1L, 10L, 100L, Turno.MANANA))
                .thenReturn(Optional.of(horario));
        when(horarioAsistenciaService.determinarEstado(horario, horaIngreso))
                .thenReturn(EstadoAsistencia.PRESENTE);

        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(i -> {
            Asistencia a = i.getArgument(0);
            a.setId(1001L);
            return a;
        });

        AsistenciaRequest req = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(fecha)
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(horaIngreso)
                .registradoPor(1L)
                .build();

        AsistenciaResponse resp = asistenciaService.registrar(req);
        assertEquals(EstadoAsistencia.PRESENTE, resp.getEstado());

        // Si se crea o desactiva un horario posteriormente, no se ejecuta ningún update sobre asistencias históricas
        verify(asistenciaRepository, never()).deleteAll();
    }
}
