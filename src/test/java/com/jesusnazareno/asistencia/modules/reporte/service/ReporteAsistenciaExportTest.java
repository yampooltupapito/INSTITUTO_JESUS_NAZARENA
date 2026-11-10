package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaItem;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import com.jesusnazareno.asistencia.modules.reporte.dto.TipoReporteAsistencia;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas Unitarias - Exportación Excel XLSX y CSV de Reportes de Asistencia")
class ReporteAsistenciaExportTest {

    private ReporteAsistenciaExcelService excelService;
    private ReporteAsistenciaCsvService csvService;

    private ReporteAsistenciaFiltro filtro;
    private List<ReporteAsistenciaItem> items;
    private ReporteAsistenciaResumen resumen;

    @BeforeEach
    void setUp() {
        excelService = new ReporteAsistenciaExcelService();
        csvService = new ReporteAsistenciaCsvService();

        filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.DIARIO_SECCION);
        filtro.setFecha(LocalDate.of(2026, 4, 10));

        items = new ArrayList<>();

        ReporteAsistenciaItem item1 = new ReporteAsistenciaItem();
        item1.setNumero(1);
        item1.setFecha(LocalDate.of(2026, 4, 10));
        item1.setNumeroDocumento("70809010");
        item1.setEstudianteCompleto("QUISPE MAMANI, Juan");
        item1.setGrado("1° Grado");
        item1.setSeccion("A");
        item1.setEstado(EstadoAsistencia.PRESENTE);
        item1.setHoraIngreso(LocalTime.of(7, 45));
        item1.setHoraSalida(LocalTime.of(13, 0));
        item1.setObservacion("Asistencia normal");
        items.add(item1);

        ReporteAsistenciaItem item2 = new ReporteAsistenciaItem();
        item2.setNumero(2);
        item2.setFecha(LocalDate.of(2026, 4, 10));
        item2.setNumeroDocumento("70809020");
        item2.setEstudianteCompleto("GARCIA PEÑA, María");
        item2.setGrado("1° Grado");
        item2.setSeccion("A");
        item2.setEstado(EstadoAsistencia.TARDANZA);
        item2.setHoraIngreso(LocalTime.of(8, 15));
        item2.setObservacion("Llegó 15 minutos tarde");
        items.add(item2);

        ReporteAsistenciaItem item3 = new ReporteAsistenciaItem();
        item3.setNumero(3);
        item3.setFecha(LocalDate.of(2026, 4, 10));
        item3.setNumeroDocumento("70809030");
        item3.setEstudianteCompleto("RODRIGUEZ NIÑO, Carlos");
        item3.setGrado("1° Grado");
        item3.setSeccion("A");
        item3.setEstado(EstadoAsistencia.JUSTIFICADO);
        item3.setObservacion("Cita médica con constancia");
        items.add(item3);

        ReporteAsistenciaItem item4 = new ReporteAsistenciaItem();
        item4.setNumero(4);
        item4.setFecha(LocalDate.of(2026, 4, 10));
        item4.setNumeroDocumento("70809040");
        item4.setEstudianteCompleto("=SUM(A1:A10)"); // Formula injection attempt in name
        item4.setGrado("1° Grado");
        item4.setSeccion("A");
        item4.setEstado(EstadoAsistencia.AUSENTE);
        item4.setObservacion("+cmd|' /C calc'!A0"); // Formula injection attempt in note
        items.add(item4);

        resumen = new ReporteAsistenciaResumen(4, 4, 1, 1, 1, 1);
    }

    @Test
    @DisplayName("1. Excel generado es un archivo XLSX válido con hojas Asistencia y Resumen")
    void generarExcel_produceArchivoValidoConHojas() throws Exception {
        byte[] bytes = excelService.generarExcel(filtro, items, resumen, "Fecha: 10/04/2026 | Grado: 1° | Sección: A");

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(2, workbook.getNumberOfSheets());

            Sheet sheetAsistencia = workbook.getSheet("Asistencia");
            assertNotNull(sheetAsistencia);

            Sheet sheetResumen = workbook.getSheet("Resumen");
            assertNotNull(sheetResumen);

            // Verificar encabezado institucional en fila 0
            Row rowTitulo = sheetAsistencia.getRow(0);
            assertNotNull(rowTitulo);
            assertEquals("INSTITUCIÓN EDUCATIVA JESÚS NAZARENO", rowTitulo.getCell(0).getStringCellValue());

            // Verificar encabezado de tabla en fila 5
            Row rowHeader = sheetAsistencia.getRow(5);
            assertNotNull(rowHeader);
            assertEquals("N°", rowHeader.getCell(0).getStringCellValue());
            assertEquals("Estudiante", rowHeader.getCell(3).getStringCellValue());
            assertEquals("Estado", rowHeader.getCell(6).getStringCellValue());

            // Verificar primer dato en fila 6
            Row rowData1 = sheetAsistencia.getRow(6);
            assertEquals(1.0, rowData1.getCell(0).getNumericCellValue());
            assertEquals("70809010", rowData1.getCell(2).getStringCellValue());
            assertEquals("QUISPE MAMANI, Juan", rowData1.getCell(3).getStringCellValue());
            assertEquals("PRESENTE", rowData1.getCell(6).getStringCellValue());

            // Verificar JUSTIFICADO en fila 8 (separado)
            Row rowData3 = sheetAsistencia.getRow(8);
            assertEquals("JUSTIFICADO", rowData3.getCell(6).getStringCellValue());
        }
    }

    @Test
    @DisplayName("2. Excel sanitiza campos contra formula injection")
    void generarExcel_sanitizaFormulaInjection() throws Exception {
        byte[] bytes = excelService.generarExcel(filtro, items, resumen, "Info");

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Asistencia");
            // Fila 4 con formula injection (=SUM en estudiante y +cmd en observación)
            Row rowInjected = sheet.getRow(9);
            assertNotNull(rowInjected);

            String estudiante = rowInjected.getCell(3).getStringCellValue();
            assertTrue(estudiante.startsWith("'="), "El estudiante con '=' debe estar prefijado con apóstrofe: " + estudiante);

            String observacion = rowInjected.getCell(9).getStringCellValue();
            assertTrue(observacion.startsWith("'+"), "La observación con '+' debe estar prefijada con apóstrofe: " + observacion);
        }
    }

    @Test
    @DisplayName("3. Excel maneja exportación vacía sin fallar")
    void generarExcel_sinDatos_generaArchivoConMensaje() throws Exception {
        byte[] bytes = excelService.generarExcel(filtro, Collections.emptyList(), null, "Sin filtros");
        assertNotNull(bytes);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Asistencia");
            assertNotNull(sheet);
            Row emptyRow = sheet.getRow(6);
            assertNotNull(emptyRow);
            assertTrue(emptyRow.getCell(0).getStringCellValue().contains("No se encontraron registros"));
        }
    }

    @Test
    @DisplayName("4. CSV incluye UTF-8 BOM y delimitador punto y coma")
    void generarCsv_contieneBomYDelimitador() {
        byte[] bytes = csvService.generarCsv(filtro, items, resumen);

        assertNotNull(bytes);
        assertTrue(bytes.length > 3);

        // Verificar BOM UTF-8 (0xEF, 0xBB, 0xBF)
        assertEquals((byte) 0xEF, bytes[0]);
        assertEquals((byte) 0xBB, bytes[1]);
        assertEquals((byte) 0xBF, bytes[2]);

        String contenido = new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);

        assertTrue(contenido.contains("N°;Fecha;Documento;Estudiante;Grado;Seccion;Estado;Hora_Ingreso;Hora_Salida;Observacion"));
        assertTrue(contenido.contains("QUISPE MAMANI, Juan"));
        assertTrue(contenido.contains("GARCIA PEÑA, María")); // Acentos y eñes
        assertTrue(contenido.contains("RODRIGUEZ NIÑO, Carlos"));
        assertTrue(contenido.contains("PRESENTE"));
        assertTrue(contenido.contains("JUSTIFICADO"));
    }

    @Test
    @DisplayName("5. CSV sanitiza formula injection y escapa delimitadores")
    void generarCsv_sanitizaFormulaInjectionYEscapaDelimitadores() {
        byte[] bytes = csvService.generarCsv(filtro, items, resumen);
        String contenido = new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);

        // '=SUM(A1:A10)' -> "'=SUM(A1:A10)"
        assertTrue(contenido.contains("'=SUM(A1:A10)"));
        assertTrue(contenido.contains("'+cmd"));

        // Verificar bloque de resumen al final del CSV
        assertTrue(contenido.contains("# --- RESUMEN ESTADÍSTICO DE ASISTENCIA ---"));
        assertTrue(contenido.contains("Presentes;1;25.0%"));
        assertTrue(contenido.contains("Tardanzas;1;25.0%"));
        assertTrue(contenido.contains("Ausencias;1;25.0%"));
        assertTrue(contenido.contains("Justificados;1;25.0%"));
    }

    @Test
    @DisplayName("6. Método estático de sanitización protege cadenas peligrosas")
    void sanitizeText_neutralizaPrefijosPeligrosos() {
        assertEquals("'=1+1", ReporteAsistenciaExcelService.sanitizeText("=1+1"));
        assertEquals("'+cmd", ReporteAsistenciaExcelService.sanitizeText("+cmd"));
        assertEquals("'-2+3", ReporteAsistenciaExcelService.sanitizeText("-2+3"));
        assertEquals("'@SUM", ReporteAsistenciaExcelService.sanitizeText("@SUM"));
        assertEquals("Juan Perez", ReporteAsistenciaExcelService.sanitizeText("Juan Perez"));
        assertEquals("", ReporteAsistenciaExcelService.sanitizeText(null));
        assertEquals("", ReporteAsistenciaExcelService.sanitizeText("   "));

        assertEquals("'=1+1", ReporteAsistenciaCsvService.escaparCampo("=1+1"));
        assertEquals("\"GARCIA; Juan\"", ReporteAsistenciaCsvService.escaparCampo("GARCIA; Juan"));
        assertEquals("\"Texto con \"\"comillas\"\"\"", ReporteAsistenciaCsvService.escaparCampo("Texto con \"comillas\""));
    }

    @Test
    @DisplayName("7. JUSTIFICADO permanece como estado independiente en Excel y CSV sin convertirse en PRESENTE")
    void exportacion_mantieneEstadoJustificadoSeparado() throws Exception {
        byte[] excelBytes = excelService.generarExcel(filtro, items, resumen, "Filtro Test");
        byte[] csvBytes = csvService.generarCsv(filtro, items, resumen);

        // Excel
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheet("Asistencia");
            Row rowJustificado = sheet.getRow(8);
            assertEquals("JUSTIFICADO", rowJustificado.getCell(6).getStringCellValue());
        }

        // CSV
        String csvContent = new String(csvBytes, 3, csvBytes.length - 3, StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("JUSTIFICADO"));
        assertTrue(csvContent.contains("Justificados;1;25.0%"));
    }

    @Test
    @DisplayName("8. Ni Excel ni CSV exportan contraseñas, hashes, BCrypt o tokens de seguridad")
    void exportacion_noExportaDatosSensibles() throws Exception {
        byte[] excelBytes = excelService.generarExcel(filtro, items, resumen, "Filtro Test");
        byte[] csvBytes = csvService.generarCsv(filtro, items, resumen);

        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(!csvContent.contains("password"));
        assertTrue(!csvContent.contains("$2a$"));
        assertTrue(!csvContent.contains("BCrypt"));
        assertTrue(!csvContent.contains("token"));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheet("Asistencia");
            for (Row row : sheet) {
                for (org.apache.poi.ss.usermodel.Cell cell : row) {
                    if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                        String val = cell.getStringCellValue();
                        assertTrue(!val.contains("password"));
                        assertTrue(!val.contains("$2a$"));
                        assertTrue(!val.contains("BCrypt"));
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("9. Caracteres peruanos con tildes y eñes (á, é, í, ó, ú, ñ, Ñ) se conservan intactos")
    void exportacion_conservaCaracteresEspanol() {
        ReporteAsistenciaItem itemEspanol = new ReporteAsistenciaItem();
        itemEspanol.setNumero(1);
        itemEspanol.setEstudianteCompleto("PEÑA OCHOA, Ítalo Ñandú Ángel");
        itemEspanol.setObservacion("Llegó con tardanza por trámite en la RENIEC de Cañete");
        itemEspanol.setEstado(EstadoAsistencia.TARDANZA);

        byte[] csvBytes = csvService.generarCsv(filtro, List.of(itemEspanol), null);
        String csv = new String(csvBytes, 3, csvBytes.length - 3, StandardCharsets.UTF_8);

        assertTrue(csv.contains("PEÑA OCHOA, Ítalo Ñandú Ángel"));
        assertTrue(csv.contains("trámite en la RENIEC de Cañete"));
    }

    @Test
    @DisplayName("10. CSV escapa comillas dobles y saltos de línea correctamente")
    void csv_escapaComillasYSaltosDeLinea() {
        String textoConSalto = "Línea 1\nLínea 2";
        String escapado = ReporteAsistenciaCsvService.escaparCampo(textoConSalto);
        assertEquals("\"Línea 1\nLínea 2\"", escapado);

        String textoConComilla = "Alumno \"Becado\"";
        String escapadoComilla = ReporteAsistenciaCsvService.escaparCampo(textoConComilla);
        assertEquals("\"Alumno \"\"Becado\"\"\"", escapadoComilla);
    }
}
