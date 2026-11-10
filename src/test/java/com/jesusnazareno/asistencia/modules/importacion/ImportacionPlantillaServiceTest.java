package com.jesusnazareno.asistencia.modules.importacion;

import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionPlantillaService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionPlantillaServiceImpl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas Unitarias - ImportacionPlantillaService")
class ImportacionPlantillaServiceTest {

    private ImportacionPlantillaService plantillaService;

    @BeforeEach
    void setUp() {
        plantillaService = new ImportacionPlantillaServiceImpl();
    }

    @Test
    @DisplayName("Generar plantilla de estudiantes devuelve XLSX válido con hojas DATOS e INSTRUCCIONES")
    void testGenerarPlantillaEstudiantesValida() throws IOException {
        byte[] bytes = plantillaService.generarPlantillaEstudiantes();
        assertThat(bytes).isNotNull().isNotEmpty();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(2);

            Sheet sheetDatos = workbook.getSheet("ESTUDIANTES");
            assertThat(sheetDatos).isNotNull();

            Row headerRow = sheetDatos.getRow(0);
            assertThat(headerRow).isNotNull();
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("tipo_documento");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("numero_documento");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("nombres");
            assertThat(headerRow.getCell(3).getStringCellValue()).isEqualTo("apellido_paterno");
            assertThat(headerRow.getCell(4).getStringCellValue()).isEqualTo("apellido_materno");
            assertThat(headerRow.getCell(5).getStringCellValue()).isEqualTo("fecha_nacimiento");
            assertThat(headerRow.getCell(6).getStringCellValue()).isEqualTo("sexo");
            assertThat(headerRow.getCell(7).getStringCellValue()).isEqualTo("telefono");
            assertThat(headerRow.getCell(8).getStringCellValue()).isEqualTo("correo");
            assertThat(headerRow.getCell(9).getStringCellValue()).isEqualTo("direccion");

            // Fila de ejemplo
            Row sampleRow = sheetDatos.getRow(1);
            assertThat(sampleRow).isNotNull();
            assertThat(sampleRow.getCell(0).getStringCellValue()).isEqualTo("DNI");
            assertThat(sampleRow.getCell(1).getStringCellValue()).isEqualTo("71234567");

            // Hoja de instrucciones
            Sheet sheetInstrucciones = workbook.getSheet("INSTRUCCIONES");
            assertThat(sheetInstrucciones).isNotNull();
            assertThat(sheetInstrucciones.getPhysicalNumberOfRows()).isGreaterThan(2);
        }
    }

    @Test
    @DisplayName("Generar plantilla de matrículas devuelve XLSX válido con hojas DATOS e INSTRUCCIONES")
    void testGenerarPlantillaMatriculasValida() throws IOException {
        byte[] bytes = plantillaService.generarPlantillaMatriculas();
        assertThat(bytes).isNotNull().isNotEmpty();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(2);

            Sheet sheetDatos = workbook.getSheet("MATRICULAS");
            assertThat(sheetDatos).isNotNull();

            Row headerRow = sheetDatos.getRow(0);
            assertThat(headerRow).isNotNull();
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("numero_documento_estudiante");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("anio");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("nivel");
            assertThat(headerRow.getCell(3).getStringCellValue()).isEqualTo("grado");
            assertThat(headerRow.getCell(4).getStringCellValue()).isEqualTo("seccion");
            assertThat(headerRow.getCell(5).getStringCellValue()).isEqualTo("codigo_matricula");
            assertThat(headerRow.getCell(6).getStringCellValue()).isEqualTo("fecha_matricula");
            assertThat(headerRow.getCell(7).getStringCellValue()).isEqualTo("estado");
            assertThat(headerRow.getCell(8).getStringCellValue()).isEqualTo("observaciones");

            // Fila de ejemplo
            Row sampleRow = sheetDatos.getRow(1);
            assertThat(sampleRow).isNotNull();
            assertThat(sampleRow.getCell(0).getStringCellValue()).isEqualTo("71234567");
            assertThat(sampleRow.getCell(1).getStringCellValue()).isEqualTo("2026");

            // Hoja de instrucciones
            Sheet sheetInstrucciones = workbook.getSheet("INSTRUCCIONES");
            assertThat(sheetInstrucciones).isNotNull();
            assertThat(sheetInstrucciones.getPhysicalNumberOfRows()).isGreaterThan(2);
        }
    }
}
