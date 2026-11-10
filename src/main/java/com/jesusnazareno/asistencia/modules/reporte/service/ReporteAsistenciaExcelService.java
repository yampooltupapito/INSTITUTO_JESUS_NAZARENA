package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaItem;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio encargado de la generación física de documentos Excel (.xlsx) mediante Apache POI.
 * Incluye formato profesional institucional, resumen estadístico y protección contra formula injection.
 */
@Service
public class ReporteAsistenciaExcelService {

    private static final Logger log = LoggerFactory.getLogger(ReporteAsistenciaExcelService.class);
    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // Color institucional primario: Emerald 700 (#047857)
    private static final byte[] COLOR_EMERALD_RGB = new byte[]{(byte) 4, (byte) 120, (byte) 87};
    // Color secundario encabezado claro: Slate 100 (#F1F5F9)
    private static final byte[] COLOR_SLATE_LIGHT_RGB = new byte[]{(byte) 241, (byte) 245, (byte) 249};

    public byte[] generarExcel(ReporteAsistenciaFiltro filtro,
                               List<ReporteAsistenciaItem> items,
                               ReporteAsistenciaResumen resumen,
                               String infoFiltros) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 1. Hoja 1: Detalle de Asistencia
            Sheet sheetDetalle = workbook.createSheet("Asistencia");
            sheetDetalle.setDisplayGridlines(true);

            // Estilos
            CellStyle styleTitulo = crearEstiloTitulo(workbook);
            CellStyle styleSubtitulo = crearEstiloSubtitulo(workbook);
            CellStyle styleMeta = crearEstiloMetadatos(workbook);
            CellStyle styleHeader = crearEstiloHeader(workbook);
            CellStyle styleDataText = crearEstiloData(workbook, HorizontalAlignment.LEFT);
            CellStyle styleDataCenter = crearEstiloData(workbook, HorizontalAlignment.CENTER);
            CellStyle styleDataEstadoPresente = crearEstiloEstado(workbook, IndexedColors.GREEN.getIndex());
            CellStyle styleDataEstadoTardanza = crearEstiloEstado(workbook, IndexedColors.DARK_YELLOW.getIndex());
            CellStyle styleDataEstadoAusente = crearEstiloEstado(workbook, IndexedColors.RED.getIndex());
            CellStyle styleDataEstadoJustificado = crearEstiloEstado(workbook, IndexedColors.ROYAL_BLUE.getIndex());

            int rowIdx = 0;

            // Encabezado institucional
            Row rowTitulo = sheetDetalle.createRow(rowIdx++);
            Cell cellTitulo = rowTitulo.createCell(0);
            cellTitulo.setCellValue("INSTITUCIÓN EDUCATIVA JESÚS NAZARENO");
            cellTitulo.setCellStyle(styleTitulo);
            sheetDetalle.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            Row rowSub = sheetDetalle.createRow(rowIdx++);
            Cell cellSub = rowSub.createCell(0);
            cellSub.setCellValue("Sistema Web de Control de Asistencia — Reporte Oficial");
            cellSub.setCellStyle(styleSubtitulo);
            sheetDetalle.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            // Metadatos y filtros
            Row rowMeta1 = sheetDetalle.createRow(rowIdx++);
            Cell cellMeta1 = rowMeta1.createCell(0);
            cellMeta1.setCellValue("Modalidad: " + (filtro != null && filtro.getTipoReporte() != null ? filtro.getTipoReporte().getDescripcion() : "General")
                    + "  |  Filtros: " + sanitizeText(infoFiltros));
            cellMeta1.setCellStyle(styleMeta);
            sheetDetalle.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));

            Row rowMeta2 = sheetDetalle.createRow(rowIdx++);
            Cell cellMeta2 = rowMeta2.createCell(0);
            cellMeta2.setCellValue("Fecha de generación: " + LocalDateTime.now(ZONA_LIMA).format(FORMATO_FECHA_HORA)
                    + "  |  Total registros: " + (items != null ? items.size() : 0));
            cellMeta2.setCellStyle(styleMeta);
            sheetDetalle.addMergedRegion(new CellRangeAddress(3, 3, 0, 9));

            rowIdx++; // Fila en blanco separadora

            // Encabezados de tabla de datos
            Row headerRow = sheetDetalle.createRow(rowIdx++);
            String[] headers = {
                    "N°", "Fecha", "Documento", "Estudiante", "Grado", "Sección",
                    "Estado", "Hora Ingreso", "Hora Salida", "Observación"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }

            // Filas de datos
            if (items != null && !items.isEmpty()) {
                for (ReporteAsistenciaItem item : items) {
                    Row dataRow = sheetDetalle.createRow(rowIdx++);

                    // 0. N°
                    Cell cNum = dataRow.createCell(0);
                    cNum.setCellValue(item.getNumero() != null ? item.getNumero() : 0);
                    cNum.setCellStyle(styleDataCenter);

                    // 1. Fecha
                    Cell cFecha = dataRow.createCell(1);
                    cFecha.setCellValue(item.getFecha() != null ? item.getFecha().format(FORMATO_FECHA) : "-");
                    cFecha.setCellStyle(styleDataCenter);

                    // 2. Documento
                    Cell cDoc = dataRow.createCell(2);
                    cDoc.setCellValue(sanitizeText(item.getNumeroDocumento()));
                    cDoc.setCellStyle(styleDataCenter);

                    // 3. Estudiante
                    Cell cEst = dataRow.createCell(3);
                    cEst.setCellValue(sanitizeText(item.getEstudianteCompleto()));
                    cEst.setCellStyle(styleDataText);

                    // 4. Grado
                    Cell cGrado = dataRow.createCell(4);
                    cGrado.setCellValue(sanitizeText(item.getGrado()));
                    cGrado.setCellStyle(styleDataCenter);

                    // 5. Sección
                    Cell cSec = dataRow.createCell(5);
                    cSec.setCellValue(sanitizeText(item.getSeccion()));
                    cSec.setCellStyle(styleDataCenter);

                    // 6. Estado
                    Cell cEstado = dataRow.createCell(6);
                    EstadoAsistencia estado = item.getEstado();
                    if (estado != null) {
                        cEstado.setCellValue(estado.name());
                        switch (estado) {
                            case PRESENTE -> cEstado.setCellStyle(styleDataEstadoPresente);
                            case TARDANZA -> cEstado.setCellStyle(styleDataEstadoTardanza);
                            case AUSENTE -> cEstado.setCellStyle(styleDataEstadoAusente);
                            case JUSTIFICADO -> cEstado.setCellStyle(styleDataEstadoJustificado);
                        }
                    } else {
                        cEstado.setCellValue("NO REGISTRADO");
                        cEstado.setCellStyle(styleDataCenter);
                    }

                    // 7. Hora Ingreso
                    Cell cIngreso = dataRow.createCell(7);
                    cIngreso.setCellValue(item.getHoraIngreso() != null ? item.getHoraIngreso().format(FORMATO_HORA) : "-");
                    cIngreso.setCellStyle(styleDataCenter);

                    // 8. Hora Salida
                    Cell cSalida = dataRow.createCell(8);
                    cSalida.setCellValue(item.getHoraSalida() != null ? item.getHoraSalida().format(FORMATO_HORA) : "-");
                    cSalida.setCellStyle(styleDataCenter);

                    // 9. Observación
                    Cell cObs = dataRow.createCell(9);
                    cObs.setCellValue(sanitizeText(item.getObservacion()));
                    cObs.setCellStyle(styleDataText);
                }
            } else {
                Row emptyRow = sheetDetalle.createRow(rowIdx++);
                Cell emptyCell = emptyRow.createCell(0);
                emptyCell.setCellValue("No se encontraron registros de asistencia para los filtros seleccionados.");
                emptyCell.setCellStyle(styleDataCenter);
                sheetDetalle.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 9));
            }

            // Autoajuste de anchos de columna con límites razonables
            for (int i = 0; i < headers.length; i++) {
                sheetDetalle.autoSizeColumn(i);
                int currentWidth = sheetDetalle.getColumnWidth(i);
                if (currentWidth < 3000) {
                    sheetDetalle.setColumnWidth(i, 3000);
                } else if (currentWidth > 12000) {
                    sheetDetalle.setColumnWidth(i, 12000);
                }
            }

            // 2. Hoja 2: Resumen Ejecutivo
            if (resumen != null) {
                Sheet sheetResumen = workbook.createSheet("Resumen");
                sheetResumen.setDisplayGridlines(true);

                int rIdx = 0;

                Row rTitulo = sheetResumen.createRow(rIdx++);
                Cell cRTitulo = rTitulo.createCell(0);
                cRTitulo.setCellValue("RESUMEN ESTADÍSTICO DE ASISTENCIA");
                cRTitulo.setCellStyle(styleTitulo);
                sheetResumen.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

                Row rSub = sheetResumen.createRow(rIdx++);
                Cell cRSub = rSub.createCell(0);
                cRSub.setCellValue("Generado el " + LocalDateTime.now(ZONA_LIMA).format(FORMATO_FECHA_HORA));
                cRSub.setCellStyle(styleSubtitulo);
                sheetResumen.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

                rIdx++; // Espacio

                // Tabla de Indicadores
                Row rHeader = sheetResumen.createRow(rIdx++);
                Cell rh1 = rHeader.createCell(0);
                rh1.setCellValue("Categoría / Estado");
                rh1.setCellStyle(styleHeader);

                Cell rh2 = rHeader.createCell(1);
                rh2.setCellValue("Cantidad");
                rh2.setCellStyle(styleHeader);

                Cell rh3 = rHeader.createCell(2);
                rh3.setCellValue("Porcentaje");
                rh3.setCellStyle(styleHeader);

                Cell rh4 = rHeader.createCell(3);
                rh4.setCellValue("Detalle");
                rh4.setCellStyle(styleHeader);

                // Filas de Resumen
                agregarFilaResumen(sheetResumen, rIdx++, "Presentes", resumen.getPresentes(),
                        resumen.getPorcentajePresentes() + "%", "Asistencia puntual registrada", styleDataText, styleDataCenter);
                agregarFilaResumen(sheetResumen, rIdx++, "Tardanzas", resumen.getTardanzas(),
                        resumen.getPorcentajeTardanzas() + "%", "Ingreso fuera de horario", styleDataText, styleDataCenter);
                agregarFilaResumen(sheetResumen, rIdx++, "Ausencias", resumen.getAusentes(),
                        resumen.getPorcentajeAusentes() + "%", "Inasistencia no justificada", styleDataText, styleDataCenter);
                agregarFilaResumen(sheetResumen, rIdx++, "Justificados", resumen.getJustificados(),
                        resumen.getPorcentajeJustificados() + "%", "Inasistencias con justificación aprobada", styleDataText, styleDataCenter);

                rIdx++; // Espacio

                // Totales
                agregarFilaResumen(sheetResumen, rIdx++, "Total Registros", resumen.getTotalRegistros(),
                        "100.0%", "Total evaluaciones de asistencia", styleHeader, styleHeader);
                if (resumen.getTotalMatriculados() > 0) {
                    agregarFilaResumen(sheetResumen, rIdx++, "Total Estudiantes Matriculados", resumen.getTotalMatriculados(),
                            "-", "Matrículas activas en el periodo", styleDataText, styleDataCenter);
                }
                agregarFilaResumen(sheetResumen, rIdx++, "% Asistencia General", resumen.getPresentes() + resumen.getTardanzas(),
                        resumen.getPorcentajeAsistencia() + "%", "Presentes + Tardanzas sobre total", styleHeader, styleHeader);

                for (int i = 0; i < 4; i++) {
                    sheetResumen.autoSizeColumn(i);
                    if (sheetResumen.getColumnWidth(i) < 4500) {
                        sheetResumen.setColumnWidth(i, 4500);
                    }
                }
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error al generar archivo Excel de reporte: {}", e.getMessage(), e);
            throw new RuntimeException("Error en la construcción del archivo Excel: " + e.getMessage(), e);
        }
    }

    private void agregarFilaResumen(Sheet sheet, int rowIdx, String categoria, long cantidad,
                                    String porcentaje, String detalle, CellStyle styleText, CellStyle styleCenter) {
        Row row = sheet.createRow(rowIdx);

        Cell c1 = row.createCell(0);
        c1.setCellValue(categoria);
        c1.setCellStyle(styleText);

        Cell c2 = row.createCell(1);
        c2.setCellValue(cantidad);
        c2.setCellStyle(styleCenter);

        Cell c3 = row.createCell(2);
        c3.setCellValue(porcentaje);
        c3.setCellStyle(styleCenter);

        Cell c4 = row.createCell(3);
        c4.setCellValue(detalle);
        c4.setCellStyle(styleText);
    }

    /**
     * Sanitiza el texto para neutralizar ataques de Formula Injection en hojas de cálculo.
     * Si el texto inicia con '=', '+', '-', '@', '\t', o '\r', antepone un apóstrofe (').
     */
    public static String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        char first = trimmed.charAt(0);
        if (first == '=' || first == '+' || first == '-' || first == '@' || first == '\t' || first == '\r') {
            return "'" + trimmed;
        }
        return trimmed;
    }

    private CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloSubtitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private CellStyle crearEstiloMetadatos(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private CellStyle crearEstiloHeader(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));
        style.setFont(font);

        style.setFillForegroundColor(new XSSFColor(COLOR_EMERALD_RGB, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        aplicarBordes(style, BorderStyle.THIN, IndexedColors.GREY_40_PERCENT.getIndex());
        return style;
    }

    private CellStyle crearEstiloData(Workbook workbook, HorizontalAlignment alignment) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        aplicarBordes(style, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
        return style;
    }

    private CellStyle crearEstiloEstado(Workbook workbook, short colorIndex) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(colorIndex);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        aplicarBordes(style, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
        return style;
    }

    private void aplicarBordes(CellStyle style, BorderStyle borderStyle, short colorIndex) {
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
        style.setTopBorderColor(colorIndex);
        style.setBottomBorderColor(colorIndex);
        style.setLeftBorderColor(colorIndex);
        style.setRightBorderColor(colorIndex);
    }
}
