package com.jesusnazareno.asistencia.modules.dashboard;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.repository.JustificacionRepository;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardFiltro;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumen;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumenGrado;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumenSeccion;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardTendenciaDia;
import com.jesusnazareno.asistencia.modules.dashboard.service.DashboardServiceImpl;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Pruebas Unitarias - DashboardService")
class DashboardServiceTest {

    @Mock
    private PersonaActorRepository personaActorRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private JustificacionRepository justificacionRepository;

    @Mock
    private AnioEscolarRepository anioEscolarRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private SeccionRepository seccionRepository;

    @Mock
    private com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService;

    @Mock
    private com.jesusnazareno.asistencia.modules.alerta.repository.AlertaAsistenciaRepository alertaAsistenciaRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

    @BeforeEach
    void setUp() {
        when(calendarioEscolarService.esDiaLectivo(any(), any())).thenReturn(true);
    }

    @Test
    @DisplayName("obtenerResumen() invoca consultas COUNT reales y mapea correctamente los indicadores")
    void obtenerResumen_devuelveDatosRealesDeRepositories() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);

        when(personaActorRepository.countByTipoActorCodigoAndActivoTrueAndPersonaActivoTrue("ESTUDIANTE"))
                .thenReturn(120L);
        when(matriculaRepository.countByEstadoAndAnioEscolarActivo(EstadoMatricula.MATRICULADO))
                .thenReturn(115L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.PRESENTE)))
                .thenReturn(98L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.TARDANZA)))
                .thenReturn(12L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.AUSENTE)))
                .thenReturn(5L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.JUSTIFICADO)))
                .thenReturn(2L);
        when(justificacionRepository.countByEstadoAndActivoTrue(EstadoJustificacion.PENDIENTE))
                .thenReturn(3L);
        when(alertaAsistenciaRepository.countByEstadoAndActivoTrue(com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta.PENDIENTE))
                .thenReturn(4L);

        DashboardResumen resumen = dashboardService.obtenerResumen();

        assertThat(resumen).isNotNull();
        assertThat(resumen.getTotalEstudiantes()).isEqualTo(120L);
        assertThat(resumen.getTotalMatriculas()).isEqualTo(115L);
        assertThat(resumen.getPresentesHoy()).isEqualTo(98L);
        assertThat(resumen.getTardanzasHoy()).isEqualTo(12L);
        assertThat(resumen.getAusentesHoy()).isEqualTo(5L);
        assertThat(resumen.getJustificadosHoy()).isEqualTo(2L);
        assertThat(resumen.getJustificacionesPendientes()).isEqualTo(3L);
        assertThat(resumen.getTotalAlertasPendientes()).isEqualTo(4L);
        assertThat(resumen.getFecha()).isEqualTo(hoyLima);
        assertThat(resumen.getFechaFormateada()).isNotBlank();

        verify(personaActorRepository).countByTipoActorCodigoAndActivoTrueAndPersonaActivoTrue("ESTUDIANTE");
        verify(matriculaRepository).countByEstadoAndAnioEscolarActivo(EstadoMatricula.MATRICULADO);
        verify(asistenciaRepository).countByFechaAndEstado(hoyLima, EstadoAsistencia.PRESENTE);
        verify(asistenciaRepository).countByFechaAndEstado(hoyLima, EstadoAsistencia.TARDANZA);
        verify(asistenciaRepository).countByFechaAndEstado(hoyLima, EstadoAsistencia.AUSENTE);
        verify(justificacionRepository).countByEstadoAndActivoTrue(EstadoJustificacion.PENDIENTE);
        verify(alertaAsistenciaRepository).countByEstadoAndActivoTrue(com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta.PENDIENTE);
    }

    @Test
    @DisplayName("4. presentesHoy correcto")
    void indicadores_presentesHoyEsCorrecto() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.PRESENTE)))
                .thenReturn(45L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getPresentesHoy()).isEqualTo(45L);
    }

    @Test
    @DisplayName("5. tardanzasHoy correcto")
    void indicadores_tardanzasHoyEsCorrecto() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.TARDANZA)))
                .thenReturn(10L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getTardanzasHoy()).isEqualTo(10L);
    }

    @Test
    @DisplayName("6. ausentesHoy correcto")
    void indicadores_ausentesHoyEsCorrecto() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.AUSENTE)))
                .thenReturn(7L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getAusentesHoy()).isEqualTo(7L);
    }

    @Test
    @DisplayName("7. justificadosHoy correcto")
    void indicadores_justificadosHoyEsCorrecto() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.JUSTIFICADO)))
                .thenReturn(4L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getJustificadosHoy()).isEqualTo(4L);
    }

    @Test
    @DisplayName("8. justificacionesPendientes correcto")
    void indicadores_justificacionesPendientesEsCorrecto() {
        when(justificacionRepository.countByEstadoAndActivoTrue(EstadoJustificacion.PENDIENTE))
                .thenReturn(8L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getJustificacionesPendientes()).isEqualTo(8L);
    }

    @Test
    @DisplayName("9. totalRegistrosHoy correcto (suma exacta de los 4 estados)")
    void indicadores_totalRegistrosHoyEsCorrecto() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.PRESENTE))).thenReturn(50L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.TARDANZA))).thenReturn(10L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.AUSENTE))).thenReturn(5L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.JUSTIFICADO))).thenReturn(3L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getTotalRegistrosHoy()).isEqualTo(68L);
        assertThat(resumen.getAsistenciaEfectiva()).isEqualTo(60L);
    }

    @Test
    @DisplayName("10. porcentajeAsistencia correcto: (PRESENTE + TARDANZA) * 100 / totalRegistrosHoy")
    void indicadores_porcentajeAsistenciaEsCorrecto() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.PRESENTE))).thenReturn(80L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.TARDANZA))).thenReturn(10L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.AUSENTE))).thenReturn(10L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.JUSTIFICADO))).thenReturn(0L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        // 90 / 100 * 100 = 90.0%
        assertThat(resumen.getPorcentajeAsistencia()).isEqualTo(90.0);
        assertThat(resumen.getPorcentajeAsistenciaFormatted()).isEqualTo("90.0%");
    }

    @Test
    @DisplayName("11. porcentaje no divide entre cero cuando totalRegistrosHoy = 0")
    void indicadores_porcentajeNoDivideEntreCero() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.PRESENTE))).thenReturn(0L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.TARDANZA))).thenReturn(0L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.AUSENTE))).thenReturn(0L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.JUSTIFICADO))).thenReturn(0L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getTotalRegistrosHoy()).isEqualTo(0L);
        assertThat(resumen.getPorcentajeAsistencia()).isEqualTo(0.0);
        assertThat(resumen.getPorcentajeAsistenciaFormatted()).isEqualTo("0.0%");
    }

    @Test
    @DisplayName("12. JUSTIFICADO no se suma automáticamente a PRESENTE")
    void indicadores_justificadoNoSeSumaAPresente() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.PRESENTE))).thenReturn(70L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.TARDANZA))).thenReturn(0L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.AUSENTE))).thenReturn(20L);
        when(asistenciaRepository.countByFechaAndEstado(eq(hoyLima), eq(EstadoAsistencia.JUSTIFICADO))).thenReturn(10L);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        // total: 100, presentes: 70, justificados: 10. Efectiva: 70 (no 80)
        assertThat(resumen.getAsistenciaEfectiva()).isEqualTo(70L);
        assertThat(resumen.getJustificadosHoy()).isEqualTo(10L);
        assertThat(resumen.getPorcentajeAsistencia()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("13. resumen por grado correcto")
    void obtenerResumenPorGrado_devuelveListaCorrecta() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{1L, "1° Secundaria", 1, EstadoAsistencia.PRESENTE, 25L});
        filas.add(new Object[]{1L, "1° Secundaria", 1, EstadoAsistencia.TARDANZA, 3L});
        filas.add(new Object[]{1L, "1° Secundaria", 1, EstadoAsistencia.AUSENTE, 2L});
        filas.add(new Object[]{2L, "2° Secundaria", 2, EstadoAsistencia.PRESENTE, 28L});

        when(asistenciaRepository.contarAsistenciaPorGrado(eq(hoyLima), any(), any(), any()))
                .thenReturn(filas);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getResumenGrados()).hasSize(2);

        DashboardResumenGrado grado1 = resumen.getResumenGrados().get(0);
        assertThat(grado1.getGradoNombre()).isEqualTo("1° Secundaria");
        assertThat(grado1.getPresentes()).isEqualTo(25L);
        assertThat(grado1.getTardanzas()).isEqualTo(3L);
        assertThat(grado1.getAusentes()).isEqualTo(2L);
        assertThat(grado1.getTotal()).isEqualTo(30L);
        assertThat(grado1.getPorcentajeFormatted()).isEqualTo("93.3%");
    }

    @Test
    @DisplayName("14. resumen por sección correcto")
    void obtenerResumenPorSeccion_devuelveListaCorrecta() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{1L, "1° Secundaria", 10L, "A", EstadoAsistencia.PRESENTE, 18L});
        filas.add(new Object[]{1L, "1° Secundaria", 10L, "A", EstadoAsistencia.TARDANZA, 2L});
        filas.add(new Object[]{1L, "1° Secundaria", 11L, "B", EstadoAsistencia.PRESENTE, 20L});

        when(asistenciaRepository.contarAsistenciaPorSeccion(eq(hoyLima), any(), any(), any()))
                .thenReturn(filas);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getResumenSecciones()).hasSize(2);

        DashboardResumenSeccion seccionA = resumen.getResumenSecciones().get(0);
        assertThat(seccionA.getSeccionNombre()).isEqualTo("A");
        assertThat(seccionA.getPresentes()).isEqualTo(18L);
        assertThat(seccionA.getTardanzas()).isEqualTo(2L);
        assertThat(seccionA.getTotal()).isEqualTo(20L);
        assertThat(seccionA.getPorcentajeFormatted()).isEqualTo("100.0%");
    }

    @Test
    @DisplayName("15. tendencia de 7 días correcta")
    void obtenerTendenciaUltimos7Dias_devuelveSieteDiasCompletos() {
        LocalDate hoyLima = LocalDate.now(ZONA_LIMA);
        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{hoyLima, EstadoAsistencia.PRESENTE, 50L});
        filas.add(new Object[]{hoyLima.minusDays(1), EstadoAsistencia.PRESENTE, 48L});

        when(asistenciaRepository.contarAsistenciaTendencia(eq(hoyLima.minusDays(6)), eq(hoyLima), any(), any(), any()))
                .thenReturn(filas);

        DashboardResumen resumen = dashboardService.obtenerResumen();
        assertThat(resumen.getTendenciaDias()).hasSize(7);

        DashboardTendenciaDia diaHoy = resumen.getTendenciaDias().get(6);
        assertThat(diaHoy.getFecha()).isEqualTo(hoyLima);
        assertThat(diaHoy.getPresentes()).isEqualTo(50L);

        DashboardTendenciaDia diaAyer = resumen.getTendenciaDias().get(5);
        assertThat(diaAyer.getFecha()).isEqualTo(hoyLima.minusDays(1));
        assertThat(diaAyer.getPresentes()).isEqualTo(48L);
    }

    @Test
    @DisplayName("16. filtros funcionan y condicionan los indicadores")
    void obtenerResumenConFiltros_aplicaFiltrosCorrectamente() {
        DashboardFiltro filtro = new DashboardFiltro(1L, 2L, 3L);
        when(anioEscolarRepository.existsById(1L)).thenReturn(true);
        when(gradoRepository.existsById(2L)).thenReturn(true);

        Grado grado = new Grado();
        grado.setId(2L);
        Seccion seccion = new Seccion();
        seccion.setId(3L);
        seccion.setGrado(grado);
        when(seccionRepository.findById(3L)).thenReturn(Optional.of(seccion));

        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 24L});
        conteos.add(new Object[]{EstadoAsistencia.TARDANZA, 2L});
        conteos.add(new Object[]{EstadoAsistencia.AUSENTE, 1L});
        conteos.add(new Object[]{EstadoAsistencia.JUSTIFICADO, 1L});

        when(asistenciaRepository.contarPorEstadoParaReporte(any(), eq(3L), eq(2L), eq(1L), any(), any(), any()))
                .thenReturn(conteos);
        when(matriculaRepository.countMatriculasActivasConFiltros(1L, 2L, 3L)).thenReturn(28L);

        DashboardResumen resumen = dashboardService.obtenerResumen(filtro);

        assertThat(resumen.getPresentesHoy()).isEqualTo(24L);
        assertThat(resumen.getTardanzasHoy()).isEqualTo(2L);
        assertThat(resumen.getAusentesHoy()).isEqualTo(1L);
        assertThat(resumen.getJustificadosHoy()).isEqualTo(1L);
        assertThat(resumen.getTotalRegistrosHoy()).isEqualTo(28L);
        assertThat(resumen.getEsperadosHoy()).isEqualTo(28L);
        assertThat(resumen.getPendientesHoy()).isEqualTo(0L);
    }

    @Test
    @DisplayName("17. sección/grado inválidos son rechazados")
    void validarFiltros_seccionInvalidaParaGrado_lanzaBusinessException() {
        DashboardFiltro filtro = new DashboardFiltro(1L, 1L, 2L);
        when(anioEscolarRepository.existsById(1L)).thenReturn(true);
        when(gradoRepository.existsById(1L)).thenReturn(true);

        Grado gradoOtro = new Grado();
        gradoOtro.setId(2L); // Grado 2, pero el filtro especificó Grado 1
        Seccion seccion = new Seccion();
        seccion.setId(2L);
        seccion.setGrado(gradoOtro);
        when(seccionRepository.findById(2L)).thenReturn(Optional.of(seccion));

        assertThatThrownBy(() -> dashboardService.validarFiltros(filtro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no pertenece al grado especificado");
    }

    @Test
    @DisplayName("18. Dashboard sin datos funciona sin errores")
    void obtenerResumen_sinDatosEnBaseDeDatos_funcionaSinErrores() {
        when(asistenciaRepository.countByFechaAndEstado(any(), any())).thenReturn(0L);
        when(personaActorRepository.countByTipoActorCodigoAndActivoTrueAndPersonaActivoTrue(any())).thenReturn(0L);
        when(matriculaRepository.countByEstadoAndAnioEscolarActivo(any())).thenReturn(0L);
        when(justificacionRepository.countByEstadoAndActivoTrue(any())).thenReturn(0L);
        when(asistenciaRepository.contarAsistenciaPorGrado(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(asistenciaRepository.contarAsistenciaPorSeccion(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(asistenciaRepository.contarAsistenciaTendencia(any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());

        DashboardResumen resumen = dashboardService.obtenerResumen();

        assertThat(resumen).isNotNull();
        assertThat(resumen.getTotalEstudiantes()).isEqualTo(0L);
        assertThat(resumen.getTotalMatriculas()).isEqualTo(0L);
        assertThat(resumen.getTotalRegistrosHoy()).isEqualTo(0L);
        assertThat(resumen.getPorcentajeAsistencia()).isEqualTo(0.0);
        assertThat(resumen.getPorcentajeAsistenciaFormatted()).isEqualTo("0.0%");
        assertThat(resumen.getResumenGrados()).isEmpty();
        assertThat(resumen.getResumenSecciones()).isEmpty();
        assertThat(resumen.getTendenciaDias()).hasSize(7);
        assertThat(resumen.getTendenciaDias().get(0).getTotal()).isEqualTo(0L);
    }

    @Test
    @DisplayName("19. Dashboard distingue día no lectivo")
    void obtenerResumen_diaNoLectivo_marcaNoLectivoYSinAusenciaMasiva() {
        AnioEscolar anio = AnioEscolar.builder().id(1L).anio(2026).activo(true).build();
        when(anioEscolarRepository.findByActivoTrueOrderByAnioDesc()).thenReturn(List.of(anio));
        when(anioEscolarRepository.existsById(1L)).thenReturn(true);
        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(calendarioEscolarService.esDiaLectivo(eq(1L), any())).thenReturn(false);

        DashboardResumen resumen = dashboardService.obtenerResumen();

        assertThat(resumen).isNotNull();
        assertThat(resumen.isEsDiaLectivo()).isFalse();
        assertThat(resumen.getMensajeDiaLectivo()).isEqualTo("Hoy no es un día lectivo.");
        assertThat(resumen.getEsperadosHoy()).isEqualTo(0L);
        assertThat(resumen.getPendientesHoy()).isEqualTo(0L);
    }
}
