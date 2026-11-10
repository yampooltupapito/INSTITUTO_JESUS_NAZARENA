package com.jesusnazareno.asistencia.modules.alerta.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.alerta.domain.AlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.domain.ConfiguracionAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.dto.AlertaAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.ResolucionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.RevisionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.mapper.AlertaAsistenciaMapper;
import com.jesusnazareno.asistencia.modules.alerta.mapper.ConfiguracionAlertaMapper;
import com.jesusnazareno.asistencia.modules.alerta.repository.AlertaAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.alerta.repository.ConfiguracionAlertaAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Pruebas Unitarias - AlertaAsistenciaService")
class AlertaAsistenciaServiceTest {

    @Mock
    private AlertaAsistenciaRepository alertaRepository;

    @Mock
    private ConfiguracionAlertaAsistenciaRepository configuracionRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private CalendarioEscolarService calendarioEscolarService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private final AlertaAsistenciaMapper alertaMapper = new AlertaAsistenciaMapper();

    private final ConfiguracionAlertaMapper configuracionMapper = new ConfiguracionAlertaMapper();

    private AlertaAsistenciaServiceImpl alertaService;

    private Matricula matricula;
    private AnioEscolar anioEscolar;
    private Usuario usuarioRevisor;

    @BeforeEach
    void setUp() {
        when(calendarioEscolarService.esDiaLectivo(any(), any())).thenReturn(true);

        alertaService = new AlertaAsistenciaServiceImpl(
                alertaRepository,
                configuracionRepository,
                matriculaRepository,
                asistenciaRepository,
                calendarioEscolarService,
                usuarioRepository,
                alertaMapper,
                configuracionMapper
        );

        anioEscolar = AnioEscolar.builder()
                .id(1L)
                .nombre("2026")
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();

        matricula = Matricula.builder()
                .id(10L)
                .anioEscolar(anioEscolar)
                .codigoMatricula("MAT-2026-0010")
                .build();

        usuarioRevisor = Usuario.builder()
                .id(5L)
                .username("revisor.user")
                .build();
    }

    private Asistencia crearAsistencia(LocalDate fecha, EstadoAsistencia estado) {
        return Asistencia.builder()
                .matricula(matricula)
                .fecha(fecha)
                .estado(estado)
                .build();
    }

    @Test
    @DisplayName("1. Ausencias consecutivas consideran solo días lectivos")
    void evaluarMatricula_ausenciasConsecutivas_consideraSoloDiasLectivos() {
        // Viernes 2026-03-06: AUSENTE, Lunes 2026-03-09: AUSENTE
        LocalDate viernes = LocalDate.of(2026, 3, 6);
        LocalDate lunes = LocalDate.of(2026, 3, 9);

        List<Asistencia> asistencias = List.of(
                crearAsistencia(viernes, EstadoAsistencia.AUSENTE),
                crearAsistencia(lunes, EstadoAsistencia.AUSENTE)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(
                10L, anioEscolar.getFechaInicio(), anioEscolar.getFechaFin())).thenReturn(asistencias);

        // Umbral de 2 ausencias consecutivas
        ConfiguracionAlertaAsistencia cfgConsecutivas = ConfiguracionAlertaAsistencia.builder()
                .tipo(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS)
                .umbral(2)
                .activo(true)
                .build();
        when(configuracionRepository.findByTipoAndActivoTrue(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS))
                .thenReturn(Optional.of(cfgConsecutivas));

        // Fin de semana: no lectivos
        when(calendarioEscolarService.esDiaLectivo(1L, LocalDate.of(2026, 3, 7))).thenReturn(false);
        when(calendarioEscolarService.esDiaLectivo(1L, LocalDate.of(2026, 3, 8))).thenReturn(false);

        alertaService.evaluarMatricula(10L);

        ArgumentCaptor<AlertaAsistencia> captor = ArgumentCaptor.forClass(AlertaAsistencia.class);
        verify(alertaRepository).save(captor.capture());

        AlertaAsistencia guardada = captor.getValue();
        assertEquals(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS, guardada.getTipo());
        assertEquals(2, guardada.getCantidad());
        assertEquals(EstadoAlerta.PENDIENTE, guardada.getEstado());
        assertTrue(guardada.getActivo());
    }

    @Test
    @DisplayName("2. Fines de semana no rompen la secuencia escolar")
    void evaluarMatricula_finesDeSemanaNoRompenSecuenciaEscolar() {
        // Viernes, Lunes, Martes = 3 ausencias consecutivas
        LocalDate viernes = LocalDate.of(2026, 3, 6);
        LocalDate lunes = LocalDate.of(2026, 3, 9);
        LocalDate martes = LocalDate.of(2026, 3, 10);

        List<Asistencia> asistencias = List.of(
                crearAsistencia(viernes, EstadoAsistencia.AUSENTE),
                crearAsistencia(lunes, EstadoAsistencia.AUSENTE),
                crearAsistencia(martes, EstadoAsistencia.AUSENTE)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistencias);

        // Sábado y Domingo no lectivos
        when(calendarioEscolarService.esDiaLectivo(1L, LocalDate.of(2026, 3, 7))).thenReturn(false);
        when(calendarioEscolarService.esDiaLectivo(1L, LocalDate.of(2026, 3, 8))).thenReturn(false);

        alertaService.evaluarMatricula(10L);

        ArgumentCaptor<AlertaAsistencia> captor = ArgumentCaptor.forClass(AlertaAsistencia.class);
        verify(alertaRepository).save(captor.capture());

        AlertaAsistencia guardada = captor.getValue();
        assertEquals(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS, guardada.getTipo());
        assertEquals(3, guardada.getCantidad());
    }

    @Test
    @DisplayName("3. JUSTIFICADO no cuenta como AUSENTE y rompe racha consecutiva")
    void evaluarMatricula_justificadoNoCuentaComoAusente() {
        // Día 1: AUSENTE, Día 2: JUSTIFICADO, Día 3: AUSENTE
        LocalDate d1 = LocalDate.of(2026, 3, 2);
        LocalDate d2 = LocalDate.of(2026, 3, 3);
        LocalDate d3 = LocalDate.of(2026, 3, 4);

        List<Asistencia> asistencias = List.of(
                crearAsistencia(d1, EstadoAsistencia.AUSENTE),
                crearAsistencia(d2, EstadoAsistencia.JUSTIFICADO),
                crearAsistencia(d3, EstadoAsistencia.AUSENTE)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistencias);

        // Umbrales por defecto: consecutivas = 3, acumuladas = 5
        alertaService.evaluarMatricula(10L);

        // Ningún umbral alcanzado (consecutivas máx = 1, ausencias acumuladas = 2)
        verify(alertaRepository, never()).save(any());
    }

    @Test
    @DisplayName("4. TARDANZA cuenta solo para tardanzas acumuladas")
    void evaluarMatricula_tardanzaCuentaSoloParaTardanzas() {
        LocalDate base = LocalDate.of(2026, 3, 2);
        List<Asistencia> asistencias = List.of(
                crearAsistencia(base, EstadoAsistencia.TARDANZA),
                crearAsistencia(base.plusDays(1), EstadoAsistencia.TARDANZA),
                crearAsistencia(base.plusDays(2), EstadoAsistencia.TARDANZA),
                crearAsistencia(base.plusDays(3), EstadoAsistencia.PRESENTE),
                crearAsistencia(base.plusDays(4), EstadoAsistencia.AUSENTE)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistencias);

        // Umbral tardanzas = 3
        ConfiguracionAlertaAsistencia cfgTardanzas = ConfiguracionAlertaAsistencia.builder()
                .tipo(TipoAlertaAsistencia.TARDANZAS_ACUMULADAS)
                .umbral(3)
                .activo(true)
                .build();
        when(configuracionRepository.findByTipoAndActivoTrue(TipoAlertaAsistencia.TARDANZAS_ACUMULADAS))
                .thenReturn(Optional.of(cfgTardanzas));

        alertaService.evaluarMatricula(10L);

        ArgumentCaptor<AlertaAsistencia> captor = ArgumentCaptor.forClass(AlertaAsistencia.class);
        verify(alertaRepository).save(captor.capture());

        AlertaAsistencia guardada = captor.getValue();
        assertEquals(TipoAlertaAsistencia.TARDANZAS_ACUMULADAS, guardada.getTipo());
        assertEquals(3, guardada.getCantidad());
    }

    @Test
    @DisplayName("5. Umbrales vienen de configuración persistida")
    void evaluarMatricula_umbralesProvienenDeConfiguracion() {
        LocalDate base = LocalDate.of(2026, 3, 2);
        List<Asistencia> asistencias = List.of(
                crearAsistencia(base, EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(2), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(4), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(6), EstadoAsistencia.AUSENTE)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistencias);

        // Umbral configurado en BD = 4 para acumuladas
        ConfiguracionAlertaAsistencia cfgAcumuladas = ConfiguracionAlertaAsistencia.builder()
                .tipo(TipoAlertaAsistencia.AUSENCIAS_ACUMULADAS)
                .umbral(4)
                .activo(true)
                .build();
        when(configuracionRepository.findByTipoAndActivoTrue(TipoAlertaAsistencia.AUSENCIAS_ACUMULADAS))
                .thenReturn(Optional.of(cfgAcumuladas));

        alertaService.evaluarMatricula(10L);

        ArgumentCaptor<AlertaAsistencia> captor = ArgumentCaptor.forClass(AlertaAsistencia.class);
        verify(alertaRepository).save(captor.capture());

        assertEquals(TipoAlertaAsistencia.AUSENCIAS_ACUMULADAS, captor.getValue().getTipo());
        assertEquals(4, captor.getValue().getCantidad());
    }

    @Test
    @DisplayName("6. Umbral <= 0 se rechaza con excepción")
    void actualizarConfiguracion_umbralMenorOIgualACero_lanzaExcepcion() {
        ConfiguracionAlertaRequest reqCero = new ConfiguracionAlertaRequest(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS, 0);
        ConfiguracionAlertaRequest reqNegativo = new ConfiguracionAlertaRequest(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS, -2);

        BusinessException exCero = assertThrows(BusinessException.class, () ->
                alertaService.actualizarConfiguracion(reqCero));
        assertEquals("El umbral debe ser mayor que cero.", exCero.getMessage());

        BusinessException exNeg = assertThrows(BusinessException.class, () ->
                alertaService.actualizarConfiguracion(reqNegativo));
        assertEquals("El umbral debe ser mayor que cero.", exNeg.getMessage());
    }

    @Test
    @DisplayName("7 y 8. Al alcanzar umbral genera alerta; antes del umbral no genera")
    void evaluarMatricula_alAlcanzarUmbralGeneraAlerta_antesNoGenera() {
        LocalDate base = LocalDate.of(2026, 3, 2);

        // 4 ausencias acumuladas (umbral por defecto = 5)
        List<Asistencia> asistencias4 = List.of(
                crearAsistencia(base, EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(2), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(4), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(6), EstadoAsistencia.AUSENTE)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistencias4);

        alertaService.evaluarMatricula(10L);
        verify(alertaRepository, never()).save(any());

        // Ahora con 5 ausencias
        List<Asistencia> asistencias5 = List.of(
                crearAsistencia(base, EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(2), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(4), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(6), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(8), EstadoAsistencia.AUSENTE)
        );
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistencias5);

        alertaService.evaluarMatricula(10L);
        verify(alertaRepository).save(any(AlertaAsistencia.class));
    }

    @Test
    @DisplayName("9. No se duplica alerta activa equivalente (actualiza cantidad)")
    void evaluarMatricula_noDuplicaAlertaActivaEquivalente() {
        LocalDate base = LocalDate.of(2026, 3, 2);
        List<Asistencia> asistencias = List.of(
                crearAsistencia(base, EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(1), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(2), EstadoAsistencia.AUSENTE),
                crearAsistencia(base.plusDays(3), EstadoAsistencia.AUSENTE)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistencias);

        // Alerta activa existente
        AlertaAsistencia alertaExistente = AlertaAsistencia.builder()
                .id(100L)
                .matricula(matricula)
                .tipo(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS)
                .cantidad(3)
                .fechaInicioPeriodo(anioEscolar.getFechaInicio())
                .fechaFinPeriodo(base.plusDays(2))
                .estado(EstadoAlerta.PENDIENTE)
                .activo(true)
                .build();

        when(alertaRepository.findFirstByMatriculaIdAndTipoAndFechaInicioPeriodoAndEstadoInAndActivoTrue(
                eq(10L), eq(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS), any(), any()))
                .thenReturn(Optional.of(alertaExistente));

        alertaService.evaluarMatricula(10L);

        // No crea una nueva alerta con new AlertaAsistencia(), sino que guarda la existente con cantidad actualizada a 4
        verify(alertaRepository).save(alertaExistente);
        assertEquals(4, alertaExistente.getCantidad());
    }

    @Test
    @DisplayName("10. Asistencias de años distintos no se mezclan")
    void evaluarMatricula_asistenciasDeAniosDistintosNoSeMezclan() {
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(
                eq(10L), eq(LocalDate.of(2026, 3, 1)), eq(LocalDate.of(2026, 12, 20))))
                .thenReturn(Collections.emptyList());

        alertaService.evaluarMatricula(10L);

        // Se comprueba que la consulta se acotó estrictamente al rango del año escolar 2026
        verify(asistenciaRepository).findByMatriculaIdAndFechaBetweenOrderByFechaAsc(
                10L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 12, 20));
        verify(alertaRepository, never()).save(any());
    }

    @Test
    @DisplayName("11. Transición PENDIENTE -> EN_REVISION funciona")
    void marcarEnRevision_desdePendiente_funciona() {
        AlertaAsistencia alerta = AlertaAsistencia.builder()
                .id(1L)
                .estado(EstadoAlerta.PENDIENTE)
                .activo(true)
                .build();

        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alerta));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioRevisor));
        when(alertaRepository.save(any(AlertaAsistencia.class))).thenAnswer(i -> i.getArgument(0));

        RevisionAlertaRequest request = new RevisionAlertaRequest("Revisando caso con tutor");
        alertaService.marcarEnRevision(1L, request, 5L);

        assertEquals(EstadoAlerta.EN_REVISION, alerta.getEstado());
        assertEquals(usuarioRevisor, alerta.getRevisadoPor());
        assertNotNull(alerta.getFechaRevision());
        assertEquals("Revisando caso con tutor", alerta.getObservacionRevision());
    }

    @Test
    @DisplayName("12 y 13. EN_REVISION -> RESUELTA funciona; PENDIENTE -> RESUELTA funciona")
    void resolver_desdeEnRevisionOPendiente_funciona() {
        AlertaAsistencia alerta = AlertaAsistencia.builder()
                .id(2L)
                .estado(EstadoAlerta.EN_REVISION)
                .activo(true)
                .build();

        when(alertaRepository.findById(2L)).thenReturn(Optional.of(alerta));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioRevisor));
        when(alertaRepository.save(any(AlertaAsistencia.class))).thenAnswer(i -> i.getArgument(0));

        ResolucionAlertaRequest request = new ResolucionAlertaRequest("Padre de familia firmó compromiso de asistencia.");
        alertaService.resolver(2L, request, 5L);

        assertEquals(EstadoAlerta.RESUELTA, alerta.getEstado());
        assertEquals(usuarioRevisor, alerta.getRevisadoPor());
        assertEquals("Padre de familia firmó compromiso de asistencia.", alerta.getObservacionRevision());
    }

    @Test
    @DisplayName("14. Transición desde RESUELTA se rechaza (no se reabre)")
    void resolver_desdeResuelta_seRechaza() {
        AlertaAsistencia alerta = AlertaAsistencia.builder()
                .id(3L)
                .estado(EstadoAlerta.RESUELTA)
                .activo(true)
                .build();

        when(alertaRepository.findById(3L)).thenReturn(Optional.of(alerta));

        ResolucionAlertaRequest request = new ResolucionAlertaRequest("Intentando resolver otra vez");
        BusinessException ex = assertThrows(BusinessException.class, () ->
                alertaService.resolver(3L, request, 5L));

        assertEquals("La alerta ya se encuentra en estado RESUELTA.", ex.getMessage());
    }

    @Test
    @DisplayName("16 y 17. Reevaluación por justificación desactiva alerta pendiente sin eliminar histórico")
    void evaluarMatricula_justificacionAprobada_desactivaAlertaPendienteSinEliminarHistorico() {
        // Alerta existente activa
        AlertaAsistencia alertaExistente = AlertaAsistencia.builder()
                .id(20L)
                .matricula(matricula)
                .tipo(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS)
                .cantidad(3)
                .fechaInicioPeriodo(anioEscolar.getFechaInicio())
                .fechaFinPeriodo(LocalDate.of(2026, 3, 4))
                .estado(EstadoAlerta.PENDIENTE)
                .activo(true)
                .build();

        when(alertaRepository.findFirstByMatriculaIdAndTipoAndFechaInicioPeriodoAndEstadoInAndActivoTrue(
                eq(10L), eq(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS), any(), any()))
                .thenReturn(Optional.of(alertaExistente));

        // Tras justificación, sólo queda 1 ausencia consecutiva
        List<Asistencia> asistenciasTrasJustificar = List.of(
                crearAsistencia(LocalDate.of(2026, 3, 2), EstadoAsistencia.AUSENTE),
                crearAsistencia(LocalDate.of(2026, 3, 3), EstadoAsistencia.JUSTIFICADO),
                crearAsistencia(LocalDate.of(2026, 3, 4), EstadoAsistencia.JUSTIFICADO)
        );

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
                .thenReturn(asistenciasTrasJustificar);

        alertaService.evaluarMatricula(10L);

        // Se verifica que NO hubo delete físico
        verify(alertaRepository, never()).delete(any());
        verify(alertaRepository, never()).deleteById(any());

        // Se verifica que fue desactivada lógicamente conservando el histórico
        verify(alertaRepository).save(alertaExistente);
        assertFalse(alertaExistente.getActivo());
        assertTrue(alertaExistente.getObservacionRevision().contains("Desactivada automáticamente"));
    }
}
