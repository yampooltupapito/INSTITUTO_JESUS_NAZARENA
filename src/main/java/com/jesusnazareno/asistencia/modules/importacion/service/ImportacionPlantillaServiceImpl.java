package com.jesusnazareno.asistencia.modules.importacion.service;

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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Implementación del servicio para la generación dinámica de plantillas Excel oficiales (.xlsx).
 * Genera libros limpios, sin macros (.xlsm), con metadatos de validación y hoja de instrucciones.
 */
@Service
public class ImportacionPlantillaServiceImpl implements ImportacionPlantillaService {

    private static final Logger log = LoggerFactory.getLogger(ImportacionPlantillaServiceImpl.class);

    private static final String[] CABECERAS_ESTUDIANTES = {
            "tipo_documento",
            "numero_documento",
            "nombres",
            "apellido_paterno",
            "apellido_materno",
            "fecha_nacimiento",
            "sexo",
            "telefono",
            "correo",
            "direccion"
    };

    private static final String[] EJEMPLO_ESTUDIANTES = {
            "DNI",
            "71234567",
            "Juan Carlos",
            "Perez",
            "Gomez",
            "15/04/2012",
            "MASCULINO",
            "987654321",
            "juan.perez@colegio.edu.pe",
            "Av. Los Jazmines 123"
    };

    private static final String[] CABECERAS_MATRICULAS = {
            "numero_documento_estudiante",
            "anio",
            "nivel",
            "grado",
            "seccion",
            "codigo_matricula",
            "fecha_matricula",
            "estado",
            "observaciones"
    };

    private static final String[] EJEMPLO_MATRICULAS = {
            "71234567",
            "2026",
            "SECUNDARIA",
            "1",
            "A",
            "MAT-2026-001",
            "01/03/2026",
            "MATRICULADO",
            "Matrícula regular 2026"
    };

    @Override
    public byte[] generarPlantillaEstudiantes() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("ESTUDIANTES");
            CellStyle headerStyle = crearEstiloCabecera(workbook);
            CellStyle dataStyle = crearEstiloDato(workbook);
            CellStyle exampleStyle = crearEstiloEjemplo(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < CABECERAS_ESTUDIANTES.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(CABECERAS_ESTUDIANTES[i]);
                cell.setCellStyle(headerStyle);
            }

            Row exampleRow = sheet.createRow(1);
            for (int i = 0; i < EJEMPLO_ESTUDIANTES.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(EJEMPLO_ESTUDIANTES[i]);
                cell.setCellStyle(exampleStyle);
            }

            for (int r = 2; r <= 10; r++) {
                Row emptyRow = sheet.createRow(r);
                for (int c = 0; c < CABECERAS_ESTUDIANTES.length; c++) {
                    Cell cell = emptyRow.createCell(c);
                    cell.setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < CABECERAS_ESTUDIANTES.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1200, 4200));
            }

            agregarHojaInstruccionesEstudiantes(workbook);

            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("Error al generar plantilla XLSX de estudiantes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar la plantilla de estudiantes: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generarPlantillaMatriculas() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("MATRICULAS");
            CellStyle headerStyle = crearEstiloCabecera(workbook);
            CellStyle dataStyle = crearEstiloDato(workbook);
            CellStyle exampleStyle = crearEstiloEjemplo(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < CABECERAS_MATRICULAS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(CABECERAS_MATRICULAS[i]);
                cell.setCellStyle(headerStyle);
            }

            Row exampleRow = sheet.createRow(1);
            for (int i = 0; i < EJEMPLO_MATRICULAS.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(EJEMPLO_MATRICULAS[i]);
                cell.setCellStyle(exampleStyle);
            }

            for (int r = 2; r <= 10; r++) {
                Row emptyRow = sheet.createRow(r);
                for (int c = 0; c < CABECERAS_MATRICULAS.length; c++) {
                    Cell cell = emptyRow.createCell(c);
                    cell.setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < CABECERAS_MATRICULAS.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1200, 4500));
            }

            agregarHojaInstruccionesMatriculas(workbook);

            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("Error al generar plantilla XLSX de matrículas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar la plantilla de matrículas: " + e.getMessage(), e);
        }
    }

    private void agregarHojaInstruccionesEstudiantes(Workbook workbook) {
        Sheet sheet = workbook.createSheet("INSTRUCCIONES");
        CellStyle titleStyle = crearEstiloTituloInstruccion(workbook);
        CellStyle bodyStyle = crearEstiloCuerpoInstruccion(workbook);

        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("INSTRUCCIONES PARA LA IMPORTACIÓN MASIVA DE ESTUDIANTES");
        titleCell.setCellStyle(titleStyle);

        String[] pautas = {
                "1. No modifique ni elimine los nombres de las columnas en la primera fila de la hoja 'ESTUDIANTES'.",
                "2. La fila 2 contiene un registro de ejemplo. Puede sobrescribirlo o borrarlo al completar los datos reales.",
                "3. 'tipo_documento': Valores permitidos: DNI, CARNET_EXTRANJERIA, PASAPORTE (Obligatorio).",
                "4. 'numero_documento': Para DNI debe tener exactamente 8 dígitos numéricos sin letras ni espacios (Obligatorio).",
                "5. 'nombres', 'apellido_paterno': Obligatorios. 'apellido_materno': Opcional.",
                "6. 'fecha_nacimiento': Formato dd/MM/yyyy (ejemplo: 15/04/2012).",
                "7. 'sexo': Valores permitidos: MASCULINO o FEMENINO (Obligatorio).",
                "8. 'telefono': 9 dígitos comenzando con 9 (Opcional).",
                "9. 'correo': Formato de correo electrónico válido (Opcional).",
                "10. No inserte fórmulas ni macros en el archivo.",
                "11. El archivo no debe superar las 1000 filas ni un tamaño de 5 MB."
        };

        for (int i = 0; i < pautas.length; i++) {
            Row row = sheet.createRow(3 + i);
            Cell cell = row.createCell(1);
            cell.setCellValue(pautas[i]);
            cell.setCellStyle(bodyStyle);
        }

        sheet.setColumnWidth(1, 28000);
    }

    private void agregarHojaInstruccionesMatriculas(Workbook workbook) {
        Sheet sheet = workbook.createSheet("INSTRUCCIONES");
        CellStyle titleStyle = crearEstiloTituloInstruccion(workbook);
        CellStyle bodyStyle = crearEstiloCuerpoInstruccion(workbook);

        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("INSTRUCCIONES PARA LA IMPORTACIÓN MASIVA DE MATRÍCULAS");
        titleCell.setCellStyle(titleStyle);

        String[] pautas = {
                "1. No modifique ni elimine los encabezados de la fila 1 en la hoja 'MATRICULAS'.",
                "2. El 'numero_documento_estudiante' debe pertenecer a un estudiante previamente registrado en el sistema.",
                "3. 'anio': Año escolar registrado y vigente (ejemplo: 2026).",
                "4. 'nivel': Valores permitidos: INICIAL, PRIMARIA o SECUNDARIA.",
                "5. 'grado': Número del grado dentro del nivel (ejemplo: 1, 2, 3, etc.).",
                "6. 'seccion': Letra o nombre de la sección configurada (ejemplo: A, B, C).",
                "7. 'codigo_matricula': Código alfanumérico único para la matrícula (ejemplo: MAT-2026-001).",
                "8. 'fecha_matricula': Formato dd/MM/yyyy (ejemplo: 01/03/2026).",
                "9. 'estado': Valores permitidos: MATRICULADO, TRASLADADO, RETIRADO (Por defecto: MATRICULADO).",
                "10. Se valida que la sección tenga capacidad suficiente considerando también las filas de este archivo.",
                "11. Un estudiante no puede tener más de una matrícula en el mismo año escolar."
        };

        for (int i = 0; i < pautas.length; i++) {
            Row row = sheet.createRow(3 + i);
            Cell cell = row.createCell(1);
            cell.setCellValue(pautas[i]);
            cell.setCellStyle(bodyStyle);
        }

        sheet.setColumnWidth(1, 28000);
    }

    private CellStyle crearEstiloCabecera(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        aplicarBordes(style, BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloDato(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        aplicarBordes(style, BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloEjemplo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setItalic(true);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        aplicarBordes(style, BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloTituloInstruccion(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_TEAL.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle crearEstiloCuerpoInstruccion(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

    private void aplicarBordes(CellStyle style, BorderStyle borderStyle) {
        style.setBorderBottom(borderStyle);
        style.setBorderTop(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }
}
