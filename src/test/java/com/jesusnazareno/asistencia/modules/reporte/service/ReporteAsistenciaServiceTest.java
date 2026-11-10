package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResultado;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import com.jesusnazareno.asistencia.modules.reporte.dto.TipoReporteAsistencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ReporteAsistenciaService")
class ReporteAsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AnioEscolarRepository anioEscolarRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private SeccionRepository seccionRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private PersonaActorRepository personaActorRepository;

    private final ReporteAsistenciaPdfService reporteAsistenciaPdfService = new ReporteAsistenciaPdfService();
    private ReporteAsistenciaServiceImpl reporteService;

    private AnioEscolar anio2026;
    private Grado grado1;
    private Seccion seccionA;
    private Persona estudianteJuan;
    private Matricula matricula1;
    private Asistencia asistenciaPresente;
    private Asistencia asistenciaTardanza;
    private Asistencia asistenciaAusente;
    private Asistencia asistenciaJustificada;

    @BeforeEach
    void setUp() {
        reporteService = new ReporteAsistenciaServiceImpl(
                asistenciaRepository,
                matriculaRepository,
                anioEscolarRepository,
                gradoRepository,
                seccionRepository,
                personaRepository,
                personaActorRepository,
                reporteAsistenciaPdfService
        );

        anio2026 = new AnioEscolar();
        anio2026.setId(1L);
        anio2026.setAnio(2026);
        anio2026.setNombre("Año Escolar 2026");
        anio2026.setFechaInicio(LocalDate.of(2026, 3, 1));
        anio2026.setFechaFin(LocalDate.of(2026, 12, 20));
        anio2026.setActivo(true);

        grado1 = new Grado();
        grado1.setId(1L);
        grado1.setNombre("1° Secundaria");
        grado1.setNumero(1);
        grado1.setNivel(NivelEducativo.SECUNDARIA);
        grado1.setActivo(true);

        seccionA = new Seccion();
        seccionA.setId(1L);
        seccionA.setNombre("A");
        seccionA.setCapacidad(30);
        seccionA.setGrado(grado1);
        seccionA.setActivo(true);

        estudianteJuan = new Persona();
        estudianteJuan.setId(10L);
        estudianteJuan.setTipoDocumento(TipoDocumento.DNI);
        estudianteJuan.setNumeroDocumento("70809010");
        estudianteJuan.setApellidoPaterno("QUISPE");
        estudianteJuan.setApellidoMaterno("MAMANI");
        estudianteJuan.setNombres("JUAN");
        estudianteJuan.setActivo(true);

        matricula1 = new Matricula();
        matricula1.setId(100L);
        matricula1.setEstudiante(estudianteJuan);
        matricula1.setAnioEscolar(anio2026);
        matricula1.setGrado(grado1);
        matricula1.setSeccion(seccionA);
        matricula1.setCodigoMatricula("MAT-2026-001");
        matricula1.setEstado(EstadoMatricula.MATRICULADO);

        asistenciaPresente = new Asistencia();
        asistenciaPresente.setId(1001L);
        asistenciaPresente.setMatricula(matricula1);
        asistenciaPresente.setFecha(LocalDate.of(2026, 4, 1));
        asistenciaPresente.setEstado(EstadoAsistencia.PRESENTE);
        asistenciaPresente.setHoraIngreso(LocalTime.of(7, 45));
        asistenciaPresente.setObservacion("A tiempo");

        asistenciaTardanza = new Asistencia();
        asistenciaTardanza.setId(1002L);
        asistenciaTardanza.setMatricula(matricula1);
        asistenciaTardanza.setFecha(LocalDate.of(2026, 4, 2));
        asistenciaTardanza.setEstado(EstadoAsistencia.TARDANZA);
        asistenciaTardanza.setHoraIngreso(LocalTime.of(8, 15));
        asistenciaTardanza.setObservacion("Llegó 15 min tarde");

        asistenciaAusente = new Asistencia();
        asistenciaAusente.setId(1003L);
        asistenciaAusente.setMatricula(matricula1);
        asistenciaAusente.setFecha(LocalDate.of(2026, 4, 3));
        asistenciaAusente.setEstado(EstadoAsistencia.AUSENTE);
        asistenciaAusente.setObservacion("Falta sin aviso");

        asistenciaJustificada = new Asistencia();
        asistenciaJustificada.setId(1004L);
        asistenciaJustificada.setMatricula(matricula1);
        asistenciaJustificada.setFecha(LocalDate.of(2026, 4, 4));
        asistenciaJustificada.setEstado(EstadoAsistencia.JUSTIFICADO);
        asistenciaJustificada.setObservacion("Cita médica acreditada");
    }

    @Test
    @DisplayName("4. Rango de fechas válido funciona correctamente")
    void validarFiltros_rangoValido_noLanzaExcepcion() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setFechaInicio(LocalDate.of(2026, 4, 1));
        filtro.setFechaFin(LocalDate.of(2026, 4, 30));

        reporteService.validarFiltros(filtro);
    }

    @Test
    @DisplayName("5. Rango fechaInicio > fechaFin es rechazado")
    void validarFiltros_fechaInicioMayorAFechaFin_lanzaValidationException() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setFechaInicio(LocalDate.of(2026, 5, 10));
        filtro.setFechaFin(LocalDate.of(2026, 5, 1));

        assertThatThrownBy(() -> reporteService.validarFiltros(filtro))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("no puede ser posterior");
    }

    @Test
    @DisplayName("6. Filtro por año funciona y valida existencia")
    void validarFiltros_filtroAnioInexistente_lanzaResourceNotFoundException() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setAnioEscolarId(999L);
        when(anioEscolarRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> reporteService.validarFiltros(filtro))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Año escolar no encontrado");
    }

    @Test
    @DisplayName("7. Filtro por grado funciona y valida existencia")
    void validarFiltros_filtroGradoInexistente_lanzaResourceNotFoundException() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setGradoId(888L);
        when(gradoRepository.existsById(888L)).thenReturn(false);

        assertThatThrownBy(() -> reporteService.validarFiltros(filtro))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Grado no encontrado");
    }

    @Test
    @DisplayName("8. Filtro por sección funciona y valida que pertenezca al grado")
    void validarFiltros_seccionNoPerteneceAGrado_lanzaBusinessException() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setGradoId(1L);
        filtro.setSeccionId(2L);

        when(gradoRepository.existsById(1L)).thenReturn(true);

        Grado grado2 = new Grado();
        grado2.setId(2L);
        grado2.setNombre("2° Secundaria");

        Seccion seccionDeOtroGrado = new Seccion();
        seccionDeOtroGrado.setId(2L);
        seccionDeOtroGrado.setNombre("B");
        seccionDeOtroGrado.setGrado(grado2);

        when(seccionRepository.findById(2L)).thenReturn(Optional.of(seccionDeOtroGrado));

        assertThatThrownBy(() -> reporteService.validarFiltros(filtro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no pertenece al grado");
    }

    @Test
    @DisplayName("9. Filtro por estudiante funciona y valida que sea estudiante activo")
    void validarFiltros_personaNoEstudiante_lanzaBusinessException() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setEstudianteId(50L);

        when(personaRepository.existsById(50L)).thenReturn(true);
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(50L, "ESTUDIANTE"))
                .thenReturn(false);

        assertThatThrownBy(() -> reporteService.validarFiltros(filtro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no es un estudiante activo");
    }

    @Test
    @DisplayName("10. Filtro por estado funciona en la consulta")
    void generarReporte_filtroPorEstado_consultaBDCorrectamente() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.RANGO_FECHAS);
        filtro.setEstado(EstadoAsistencia.PRESENTE);
        filtro.setFechaInicio(LocalDate.of(2026, 4, 1));
        filtro.setFechaFin(LocalDate.of(2026, 4, 30));

        Pageable pageable = PageRequest.of(0, 10);
        when(asistenciaRepository.buscarAsistenciasPaginadasParaReporte(
                any(), any(), any(), any(), eq(EstadoAsistencia.PRESENTE), any(), any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(asistenciaPresente)));

        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 1L});
        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), eq(EstadoAsistencia.PRESENTE), any(), any()))
                .thenReturn(conteos);

        ReporteAsistenciaResultado resultado = reporteService.generarReporte(filtro, pageable);

        assertThat(resultado.getPaginaItems().getContent()).hasSize(1);
        assertThat(resultado.getPaginaItems().getContent().get(0).getEstado()).isEqualTo(EstadoAsistencia.PRESENTE);
    }

    @Test
    @DisplayName("11. PRESENTE se cuenta correctamente")
    void conteos_presenteSeCuentaCorrectamente() {
        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 15L});

        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        ReporteAsistenciaResumen resumen = reporteService.calcularResumen(filtro);

        assertThat(resumen.getPresentes()).isEqualTo(15L);
    }

    @Test
    @DisplayName("12. TARDANZA se cuenta correctamente")
    void conteos_tardanzaSeCuentaCorrectamente() {
        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.TARDANZA, 5L});

        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        ReporteAsistenciaResumen resumen = reporteService.calcularResumen(filtro);

        assertThat(resumen.getTardanzas()).isEqualTo(5L);
    }

    @Test
    @DisplayName("13. AUSENTE se cuenta correctamente")
    void conteos_ausenteSeCuentaCorrectamente() {
        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.AUSENTE, 3L});

        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        ReporteAsistenciaResumen resumen = reporteService.calcularResumen(filtro);

        assertThat(resumen.getAusentes()).isEqualTo(3L);
    }

    @Test
    @DisplayName("14. JUSTIFICADO se cuenta separadamente y no como presente")
    void conteos_justificadoSeCuentaSeparadamente() {
        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 20L});
        conteos.add(new Object[]{EstadoAsistencia.JUSTIFICADO, 4L});

        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        ReporteAsistenciaResumen resumen = reporteService.calcularResumen(filtro);

        assertThat(resumen.getPresentes()).isEqualTo(20L);
        assertThat(resumen.getJustificados()).isEqualTo(4L);
        assertThat(resumen.getPorcentajePresentesFormatted()).isEqualTo("83.3%");
        assertThat(resumen.getPorcentajeJustificadosFormatted()).isEqualTo("16.7%");
    }

    @Test
    @DisplayName("15. totalRegistros es la suma exacta de todos los estados")
    void conteos_totalRegistrosEsCorrecto() {
        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 20L});
        conteos.add(new Object[]{EstadoAsistencia.TARDANZA, 5L});
        conteos.add(new Object[]{EstadoAsistencia.AUSENTE, 2L});
        conteos.add(new Object[]{EstadoAsistencia.JUSTIFICADO, 3L});

        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        ReporteAsistenciaResumen resumen = reporteService.calcularResumen(filtro);

        assertThat(resumen.getTotalRegistros()).isEqualTo(30L);
    }

    @Test
    @DisplayName("16. porcentaje no divide entre cero cuando totalRegistros = 0")
    void porcentajes_sinRegistros_retornaCeroSinDividirPorCero() {
        ReporteAsistenciaResumen resumen = new ReporteAsistenciaResumen(0, 0, 0, 0, 0, 0);

        assertThat(resumen.getPorcentajePresentes()).isEqualTo(0.0);
        assertThat(resumen.getPorcentajeTardanzas()).isEqualTo(0.0);
        assertThat(resumen.getPorcentajeAusentes()).isEqualTo(0.0);
        assertThat(resumen.getPorcentajeJustificados()).isEqualTo(0.0);
        assertThat(resumen.getPorcentajeAsistencia()).isEqualTo(0.0);
        assertThat(resumen.getPorcentajePresentesFormatted()).isEqualTo("0.0%");
        assertThat(resumen.getPorcentajeAsistenciaFormatted()).isEqualTo("0.0%");
    }

    @Test
    @DisplayName("17. PDF se genera con Content-Type y cabecera binaria %PDF")
    void generarPdf_generaDocumentoBinarioValido() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.DIARIO_SECCION);
        filtro.setFecha(LocalDate.of(2026, 4, 10));

        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        byte[] pdfBytes = reporteService.generarPdf(filtro);

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);
        // Validar firma mágica del archivo PDF (%PDF)
        String magicHeader = new String(pdfBytes, 0, 4);
        assertThat(magicHeader).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("18. PDF no incluye datos sensibles (password, bcrypt, tokens)")
    void generarPdf_noIncluyeDatosSensibles() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.RANGO_FECHAS);
        filtro.setFechaInicio(LocalDate.of(2026, 4, 1));
        filtro.setFechaFin(LocalDate.of(2026, 4, 10));

        when(asistenciaRepository.buscarAsistenciasParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(asistenciaPresente, asistenciaTardanza));

        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 1L});
        conteos.add(new Object[]{EstadoAsistencia.TARDANZA, 1L});
        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        byte[] pdfBytes = reporteService.generarPdf(filtro);
        String pdfContent = new String(pdfBytes);

        assertThat(pdfContent).doesNotContain("password");
        assertThat(pdfContent).doesNotContain("$2a$");
        assertThat(pdfContent).doesNotContain("BCrypt");
        assertThat(pdfContent).doesNotContain("token");
    }

    @Test
    @DisplayName("19. PDF funciona sin emblema sin arrojar excepción")
    void generarPdf_sinEmblema_seGeneraCorrectamente() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.POR_ESTUDIANTE);
        filtro.setEstudianteId(10L);

        when(personaRepository.existsById(10L)).thenReturn(true);
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(10L, "ESTUDIANTE")).thenReturn(true);
        when(asistenciaRepository.buscarAsistenciasParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(asistenciaPresente));

        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 1L});
        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        byte[] pdf = reporteService.generarPdf(filtro);
        assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("21. Filtros del PDF corresponden con los solicitados")
    void generarNombreArchivoPdf_contieneDatosDeFiltro() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.DIARIO_SECCION);
        filtro.setFecha(LocalDate.of(2026, 10, 2));

        String nombreArchivo = reporteService.generarNombreArchivoPdf(filtro);

        assertThat(nombreArchivo).startsWith("reporte_asistencia");
        assertThat(nombreArchivo).contains("2026-10-02");
        assertThat(nombreArchivo).endsWith(".pdf");
    }

    @Test
    @DisplayName("22. ReporteAsistenciaService.generarExcel produce archivo XLSX con datos reales y resumen")
    void generarExcel_produceArchivoValido() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.RANGO_FECHAS);
        filtro.setFechaInicio(LocalDate.of(2026, 4, 1));
        filtro.setFechaFin(LocalDate.of(2026, 4, 10));

        when(asistenciaRepository.buscarAsistenciasParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(asistenciaPresente, asistenciaTardanza));

        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 1L});
        conteos.add(new Object[]{EstadoAsistencia.TARDANZA, 1L});
        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        byte[] excelBytes = reporteService.generarExcel(filtro);
        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(100);
    }

    @Test
    @DisplayName("23. ReporteAsistenciaService.generarCsv produce archivo CSV con UTF-8 BOM y delimitador")
    void generarCsv_produceArchivoValido() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.RANGO_FECHAS);
        filtro.setFechaInicio(LocalDate.of(2026, 4, 1));
        filtro.setFechaFin(LocalDate.of(2026, 4, 10));

        when(asistenciaRepository.buscarAsistenciasParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(asistenciaPresente, asistenciaJustificada));

        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{EstadoAsistencia.PRESENTE, 1L});
        conteos.add(new Object[]{EstadoAsistencia.JUSTIFICADO, 1L});
        when(asistenciaRepository.contarPorEstadoParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(conteos);

        byte[] csvBytes = reporteService.generarCsv(filtro);
        assertThat(csvBytes).isNotNull();
        assertThat(csvBytes.length).isGreaterThan(3);
        // Verificar BOM
        assertThat(csvBytes[0]).isEqualTo((byte) 0xEF);
        assertThat(csvBytes[1]).isEqualTo((byte) 0xBB);
        assertThat(csvBytes[2]).isEqualTo((byte) 0xBF);
    }

    @Test
    @DisplayName("24. Nombres de archivo Excel y CSV son seguros contra path traversal")
    void nombresArchivo_sonSegurosContraPathTraversal() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.DIARIO_SECCION);
        filtro.setFecha(LocalDate.of(2026, 10, 2));

        String nombreExcel = reporteService.generarNombreArchivoExcel(filtro);
        String nombreCsv = reporteService.generarNombreArchivoCsv(filtro);

        assertThat(nombreExcel).doesNotContain("..");
        assertThat(nombreExcel).doesNotContain("/");
        assertThat(nombreExcel).doesNotContain("\\");
        assertThat(nombreExcel).endsWith(".xlsx");

        assertThat(nombreCsv).doesNotContain("..");
        assertThat(nombreCsv).doesNotContain("/");
        assertThat(nombreCsv).doesNotContain("\\");
        assertThat(nombreCsv).endsWith(".csv");
    }

    @Test
    @DisplayName("25. Superar el límite máximo de exportación lanza BusinessException")
    void obtenerTodosItems_superaLimite_lanzaBusinessException() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.RANGO_FECHAS);
        filtro.setFechaInicio(LocalDate.of(2026, 1, 1));
        filtro.setFechaFin(LocalDate.of(2026, 12, 31));

        reporteService.setMaxRegistrosExportacion(2);

        when(asistenciaRepository.buscarAsistenciasParaReporte(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(asistenciaPresente, asistenciaTardanza, asistenciaAusente));

        assertThatThrownBy(() -> reporteService.obtenerTodosItems(filtro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("La consulta supera el límite permitido para exportación");
    }
}
