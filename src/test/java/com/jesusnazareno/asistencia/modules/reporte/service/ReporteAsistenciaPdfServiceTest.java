package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaItem;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import com.jesusnazareno.asistencia.modules.reporte.dto.TipoReporteAsistencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas Unitarias - ReporteAsistenciaPdfService")
class ReporteAsistenciaPdfServiceTest {

    private final ReporteAsistenciaPdfService pdfService = new ReporteAsistenciaPdfService();

    @Test
    @DisplayName("17. PDF se genera con Content-Type y cabecera binaria %PDF")
    void generarPdf_generaDocumentoBinarioValido() {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.DIARIO_SECCION);
        filtro.setFecha(LocalDate.of(2026, 4, 10));

        ReporteAsistenciaResumen resumen = new ReporteAsistenciaResumen(30, 2, 1, 1, 0, 0);

        ReporteAsistenciaItem item1 = new ReporteAsistenciaItem();
        item1.setNumero(1);
        item1.setNumeroDocumento("70809010");
        item1.setEstudianteCompleto("QUISPE MAMANI, JUAN");
        item1.setEstado(EstadoAsistencia.PRESENTE);
        item1.setHoraIngreso(LocalTime.of(7, 45));
        item1.setObservacion("Asistió a tiempo");

        byte[] pdfBytes = pdfService.generarPdf(filtro, List.of(item1), resumen, "Año: 2026 | Grado: 1° Sec");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);
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

        ReporteAsistenciaResumen resumen = new ReporteAsistenciaResumen(0, 1, 1, 0, 0, 0);

        ReporteAsistenciaItem item = new ReporteAsistenciaItem();
        item.setNumero(1);
        item.setFecha(LocalDate.of(2026, 4, 2));
        item.setNumeroDocumento("70809011");
        item.setEstudianteCompleto("FLORES ROJAS, MARIA");
        item.setGrado("1° Secundaria");
        item.setSeccion("A");
        item.setEstado(EstadoAsistencia.PRESENTE);

        byte[] pdfBytes = pdfService.generarPdf(filtro, List.of(item), resumen, "Periodo: 01/04/2026 al 10/04/2026");
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

        ReporteAsistenciaResumen resumen = new ReporteAsistenciaResumen(0, 0, 0, 0, 0, 0);

        byte[] pdf = pdfService.generarPdf(filtro, List.of(), resumen, "Estudiante: QUISPE JUAN");
        assertThat(pdf).isNotEmpty();
    }
}
