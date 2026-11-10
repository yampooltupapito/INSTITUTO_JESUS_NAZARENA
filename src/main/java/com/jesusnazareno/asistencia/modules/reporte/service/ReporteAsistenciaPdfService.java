package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaItem;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio encargado de la generación física de documentos PDF mediante OpenPDF.
 * Configura fuentes seguras para idioma español, márgenes, tablas profesionales y resumen ejecutivo.
 */
@Service
public class ReporteAsistenciaPdfService {

    private static final Logger log = LoggerFactory.getLogger(ReporteAsistenciaPdfService.class);
    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // Colores institucionales
    private static final Color COLOR_PRIMARIO = new Color(6, 95, 70);       // Emerald 800
    private static final Color COLOR_SECUNDARIO = new Color(15, 23, 42);    // Slate 900
    private static final Color COLOR_HEADER_BG = new Color(4, 120, 87);     // Emerald 700
    private static final Color COLOR_ZEBRA = new Color(248, 250, 252);      // Slate 50
    private static final Color COLOR_BORDE = new Color(226, 232, 240);      // Slate 200

    // Colores de estado
    private static final Color COLOR_PRESENTE = new Color(21, 128, 61);     // Green 700
    private static final Color COLOR_TARDANZA = new Color(180, 83, 9);      // Amber 700
    private static final Color COLOR_AUSENTE = new Color(185, 28, 28);      // Red 700
    private static final Color COLOR_JUSTIFICADO = new Color(29, 78, 216);  // Blue 700
    private static final Color COLOR_TEXTO = new Color(51, 65, 85);         // Slate 700

    public byte[] generarPdf(ReporteAsistenciaFiltro filtro,
                             List<ReporteAsistenciaItem> items,
                             ReporteAsistenciaResumen resumen,
                             String infoEncabezado) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 28, 28, 32, 32);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new EncabezadoPiePaginaPdf());
            document.open();

            // 1. Encabezado institucional
            agregarEncabezadoInstitucional(document, filtro);

            // 2. Bloque de filtros y metadatos
            agregarMetadatosReporte(document, filtro, infoEncabezado);

            // 3. Resumen ejecutivo (totales y porcentajes)
            if (resumen != null) {
                agregarTablaResumen(document, resumen);
            }

            // 4. Tabla de datos
            agregarTablaDatos(document, filtro, items);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            log.error("Error al generar PDF de reporte de asistencia: {}", e.getMessage(), e);
            throw new RuntimeException("Error en la construcción del archivo PDF: " + e.getMessage(), e);
        }
    }

    private void agregarEncabezadoInstitucional(Document document, ReporteAsistenciaFiltro filtro) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{12f, 88f});
        headerTable.setSpacingAfter(8f);

        // Intento de carga de emblema institucional (sin fallar si no existe)
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        try {
            ClassPathResource logoResource = new ClassPathResource("static/img/emblema.png");
            if (logoResource.exists()) {
                try (InputStream is = logoResource.getInputStream()) {
                    byte[] logoBytes = is.readAllBytes();
                    Image logo = Image.getInstance(logoBytes);
                    logo.scaleToFit(48, 48);
                    logoCell.addElement(logo);
                }
            }
        } catch (Exception e) {
            log.debug("No se cargó emblema para el reporte PDF (se continúa sin imagen): {}", e.getMessage());
        }
        headerTable.addCell(logoCell);

        // Textos del encabezado
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font fontColegio = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 13f, Font.BOLD, COLOR_PRIMARIO);
        Font fontSistema = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 9.5f, Font.NORMAL, COLOR_SECUNDARIO);
        Font fontReporte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 11f, Font.BOLD, COLOR_SECUNDARIO);

        String nombreReporte = "REPORTE DE ASISTENCIA - " + filtro.getTipoReporte().getDescripcion().toUpperCase();

        titleCell.addElement(new Phrase("INSTITUCIÓN EDUCATIVA JESÚS NAZARENO", fontColegio));
        titleCell.addElement(new Phrase("Sistema Web de Control de Asistencia", fontSistema));
        titleCell.addElement(new Phrase(nombreReporte, fontReporte));

        headerTable.addCell(titleCell);
        document.add(headerTable);
    }

    private void agregarMetadatosReporte(Document document, ReporteAsistenciaFiltro filtro, String infoEncabezado) throws DocumentException {
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setWidths(new float[]{70f, 30f});
        metaTable.setSpacingAfter(8f);

        Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 8.5f, Font.BOLD, COLOR_TEXTO);
        Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 8.5f, Font.NORMAL, COLOR_TEXTO);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBackgroundColor(COLOR_ZEBRA);
        leftCell.setBorderColor(COLOR_BORDE);
        leftCell.setPadding(6f);

        Phrase phraseParametros = new Phrase();
        phraseParametros.add(new Phrase("Filtros aplicados: ", fontLabel));
        phraseParametros.add(new Phrase(infoEncabezado != null && !infoEncabezado.isBlank() ? infoEncabezado : "General", fontValue));
        leftCell.addElement(phraseParametros);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBackgroundColor(COLOR_ZEBRA);
        rightCell.setBorderColor(COLOR_BORDE);
        rightCell.setPadding(6f);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        String fechaEmision = LocalDateTime.now(ZONA_LIMA).format(FORMATO_FECHA_HORA);
        Phrase phraseEmision = new Phrase();
        phraseEmision.add(new Phrase("Generado: ", fontLabel));
        phraseEmision.add(new Phrase(fechaEmision, fontValue));
        rightCell.addElement(phraseEmision);

        metaTable.addCell(leftCell);
        metaTable.addCell(rightCell);
        document.add(metaTable);
    }

    private void agregarTablaResumen(Document document, ReporteAsistenciaResumen resumen) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{16f, 16f, 16f, 16f, 18f, 18f});
        table.setSpacingAfter(10f);

        Font fontResumenHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 8f, Font.BOLD, Color.WHITE);

        String[] headers = {"PRESENTES", "TARDANZAS", "AUSENCIAS", "JUSTIFICADOS", "TOTAL REGISTROS", "% ASISTENCIA"};
        Color[] headColors = {COLOR_PRESENTE, COLOR_TARDANZA, COLOR_AUSENTE, COLOR_JUSTIFICADO, COLOR_SECUNDARIO, COLOR_PRIMARIO};

        for (int i = 0; i < headers.length; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(headers[i], fontResumenHead));
            cell.setBackgroundColor(headColors[i]);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            cell.setBorderColor(Color.WHITE);
            table.addCell(cell);
        }

        Font fontVal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 11f, Font.BOLD, COLOR_SECUNDARIO);
        Font fontPct = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 7.5f, Font.NORMAL, COLOR_TEXTO);

        agregarCeldaKpi(table, String.valueOf(resumen.getPresentes()), resumen.getPorcentajePresentesFormatted(), fontVal, fontPct);
        agregarCeldaKpi(table, String.valueOf(resumen.getTardanzas()), resumen.getPorcentajeTardanzasFormatted(), fontVal, fontPct);
        agregarCeldaKpi(table, String.valueOf(resumen.getAusentes()), resumen.getPorcentajeAusentesFormatted(), fontVal, fontPct);
        agregarCeldaKpi(table, String.valueOf(resumen.getJustificados()), resumen.getPorcentajeJustificadosFormatted(), fontVal, fontPct);
        agregarCeldaKpi(table, String.valueOf(resumen.getTotalRegistros()), "100%", fontVal, fontPct);
        agregarCeldaKpi(table, resumen.getPorcentajeAsistenciaFormatted(), "(Pres. + Tard.)", fontVal, fontPct);

        document.add(table);
    }

    private void agregarCeldaKpi(PdfPTable table, String valor, String subtexto, Font fontVal, Font fontSub) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        cell.setBorderColor(COLOR_BORDE);
        cell.setBackgroundColor(COLOR_ZEBRA);

        Phrase p = new Phrase();
        p.add(new Phrase(valor + "\n", fontVal));
        p.add(new Phrase(subtexto, fontSub));
        cell.addElement(p);

        table.addCell(cell);
    }

    private void agregarTablaDatos(Document document, ReporteAsistenciaFiltro filtro, List<ReporteAsistenciaItem> items) throws DocumentException {
        // Determinamos columnas según tipo de reporte
        boolean esPorEstudiante = filtro.getTipoReporte() == com.jesusnazareno.asistencia.modules.reporte.dto.TipoReporteAsistencia.POR_ESTUDIANTE;
        boolean esDiarioSeccion = filtro.getTipoReporte() == com.jesusnazareno.asistencia.modules.reporte.dto.TipoReporteAsistencia.DIARIO_SECCION;

        PdfPTable table;
        if (esPorEstudiante) {
            table = new PdfPTable(6);
            table.setWidths(new float[]{6f, 14f, 18f, 14f, 12f, 36f});
        } else if (esDiarioSeccion) {
            table = new PdfPTable(6);
            table.setWidths(new float[]{6f, 14f, 34f, 14f, 12f, 20f});
        } else {
            table = new PdfPTable(7);
            table.setWidths(new float[]{5f, 12f, 12f, 26f, 16f, 12f, 17f});
        }

        table.setWidthPercentage(100);

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 8f, Font.BOLD, Color.WHITE);
        Font fontRow = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 7.5f, Font.NORMAL, COLOR_TEXTO);
        Font fontRowBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 7.5f, Font.BOLD, COLOR_SECUNDARIO);

        // Cabeceras
        if (esPorEstudiante) {
            agregarCabecera(table, "N°", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Fecha", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Grado y Sección", fontHeader, Element.ALIGN_LEFT);
            agregarCabecera(table, "Estado", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Ingreso", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Observación", fontHeader, Element.ALIGN_LEFT);
        } else if (esDiarioSeccion) {
            agregarCabecera(table, "N°", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Documento", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Estudiante (Apellidos y Nombres)", fontHeader, Element.ALIGN_LEFT);
            agregarCabecera(table, "Estado", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Ingreso", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Observación", fontHeader, Element.ALIGN_LEFT);
        } else {
            agregarCabecera(table, "N°", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Fecha", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Documento", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Estudiante", fontHeader, Element.ALIGN_LEFT);
            agregarCabecera(table, "Grado / Sección", fontHeader, Element.ALIGN_LEFT);
            agregarCabecera(table, "Estado", fontHeader, Element.ALIGN_CENTER);
            agregarCabecera(table, "Observación", fontHeader, Element.ALIGN_LEFT);
        }

        if (items == null || items.isEmpty()) {
            int numCols = esPorEstudiante || esDiarioSeccion ? 6 : 7;
            PdfPCell emptyCell = new PdfPCell(new Phrase("No se encontraron registros de asistencia para los filtros seleccionados.", fontRow));
            emptyCell.setColspan(numCols);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(12f);
            emptyCell.setBorderColor(COLOR_BORDE);
            table.addCell(emptyCell);
            document.add(table);
            return;
        }

        int index = 1;
        for (ReporteAsistenciaItem item : items) {
            Color rowBg = (index % 2 == 0) ? COLOR_ZEBRA : Color.WHITE;

            // N°
            agregarCelda(table, String.valueOf(index), fontRow, rowBg, Element.ALIGN_CENTER);

            if (esPorEstudiante) {
                String strFecha = item.getFecha() != null ? item.getFecha().format(FORMATO_FECHA) : "-";
                agregarCelda(table, strFecha, fontRow, rowBg, Element.ALIGN_CENTER);

                String gradoSec = (item.getGrado() != null ? item.getGrado() : "") + " - " + (item.getSeccion() != null ? item.getSeccion() : "");
                agregarCelda(table, gradoSec, fontRow, rowBg, Element.ALIGN_LEFT);

                agregarCeldaEstado(table, item.getEstado(), item.getEstadoNombre(), rowBg);

                String strHora = item.getHoraIngreso() != null ? item.getHoraIngreso().format(FORMATO_HORA) : "-";
                agregarCelda(table, strHora, fontRow, rowBg, Element.ALIGN_CENTER);

                agregarCelda(table, sanitizar(item.getObservacion()), fontRow, rowBg, Element.ALIGN_LEFT);

            } else if (esDiarioSeccion) {
                agregarCelda(table, item.getNumeroDocumento() != null ? item.getNumeroDocumento() : "-", fontRow, rowBg, Element.ALIGN_CENTER);
                agregarCelda(table, item.getEstudianteCompleto() != null ? item.getEstudianteCompleto() : "-", fontRowBold, rowBg, Element.ALIGN_LEFT);

                agregarCeldaEstado(table, item.getEstado(), item.getEstadoNombre(), rowBg);

                String strHora = item.getHoraIngreso() != null ? item.getHoraIngreso().format(FORMATO_HORA) : "-";
                agregarCelda(table, strHora, fontRow, rowBg, Element.ALIGN_CENTER);

                agregarCelda(table, sanitizar(item.getObservacion()), fontRow, rowBg, Element.ALIGN_LEFT);

            } else {
                String strFecha = item.getFecha() != null ? item.getFecha().format(FORMATO_FECHA) : "-";
                agregarCelda(table, strFecha, fontRow, rowBg, Element.ALIGN_CENTER);

                agregarCelda(table, item.getNumeroDocumento() != null ? item.getNumeroDocumento() : "-", fontRow, rowBg, Element.ALIGN_CENTER);
                agregarCelda(table, item.getEstudianteCompleto() != null ? item.getEstudianteCompleto() : "-", fontRowBold, rowBg, Element.ALIGN_LEFT);

                String gradoSec = (item.getGrado() != null ? item.getGrado() : "") + " " + (item.getSeccion() != null ? item.getSeccion() : "");
                agregarCelda(table, gradoSec, fontRow, rowBg, Element.ALIGN_LEFT);

                agregarCeldaEstado(table, item.getEstado(), item.getEstadoNombre(), rowBg);

                agregarCelda(table, sanitizar(item.getObservacion()), fontRow, rowBg, Element.ALIGN_LEFT);
            }

            index++;
        }

        document.add(table);
    }

    private void agregarCabecera(PdfPTable table, String texto, Font font, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(COLOR_HEADER_BG);
        cell.setHorizontalAlignment(alineacion);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        cell.setBorderColor(COLOR_HEADER_BG);
        table.addCell(cell);
    }

    private void agregarCelda(PdfPTable table, String texto, Font font, Color bgColor, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "-", font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alineacion);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        cell.setBorderColor(COLOR_BORDE);
        table.addCell(cell);
    }

    private void agregarCeldaEstado(PdfPTable table, EstadoAsistencia estado, String nombreEstado, Color bgColor) {
        Color colorTexto = COLOR_TEXTO;
        if (estado != null) {
            switch (estado) {
                case PRESENTE -> colorTexto = COLOR_PRESENTE;
                case TARDANZA -> colorTexto = COLOR_TARDANZA;
                case AUSENTE -> colorTexto = COLOR_AUSENTE;
                case JUSTIFICADO -> colorTexto = COLOR_JUSTIFICADO;
            }
        }

        Font fontEstado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 7.5f, Font.BOLD, colorTexto);
        PdfPCell cell = new PdfPCell(new Phrase(nombreEstado != null ? nombreEstado : "-", fontEstado));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        cell.setBorderColor(COLOR_BORDE);
        table.addCell(cell);
    }

    private String sanitizar(String str) {
        if (str == null || str.isBlank()) {
            return "-";
        }
        return str.trim();
    }

    /**
     * Manejador de eventos de página para dibujar número de página y pie institucional.
     */
    private static class EncabezadoPiePaginaPdf extends PdfPageEventHelper {
        private final Font fontPie = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 7f, Font.NORMAL, new Color(148, 163, 184));

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            float x = (document.right() - document.left()) / 2 + document.left();
            float y = document.bottom() - 14;

            Phrase footerText = new Phrase("I.E. Jesús Nazareno — Sistema Web de Control de Asistencia | Documento Institucional Oficial", fontPie);
            com.lowagie.text.pdf.ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footerText, x, y, 0);

            Phrase pageNumber = new Phrase("Pág. " + writer.getPageNumber(), fontPie);
            com.lowagie.text.pdf.ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, pageNumber, document.right(), y, 0);
        }
    }
}
