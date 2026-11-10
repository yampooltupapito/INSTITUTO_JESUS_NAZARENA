package com.jesusnazareno.asistencia.modules.importacion;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstadoFilaImportacion;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstudianteImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.ImportacionResultadoDTO;
import com.jesusnazareno.asistencia.modules.importacion.dto.MatriculaImportRow;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionExcelService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionExcelServiceImpl;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ImportacionExcelService")
class ImportacionExcelServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private PersonaActorRepository personaActorRepository;

    @Mock
    private AnioEscolarRepository anioEscolarRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private SeccionRepository seccionRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    private ImportacionExcelService excelService;

    @BeforeEach
    void setUp() {
        excelService = new ImportacionExcelServiceImpl(
                personaRepository,
                personaActorRepository,
                anioEscolarRepository,
                gradoRepository,
                seccionRepository,
                matriculaRepository
        );
        excelService.setMaxFilas(1000);
        excelService.setMaxArchivoBytes(5242880);
    }

    // =========================================================================
    // PRUEBAS DE ARCHIVO, FORMATO Y LÍMITES
    // =========================================================================

    @Test
    @DisplayName("Rechaza archivo nulo o vacío")
    void testRechazarArchivoVacio() {
        MockMultipartFile emptyFile = new MockMultipartFile("archivo", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        assertThatThrownBy(() -> excelService.leerYValidarEstudiantes(emptyFile, "admin"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Seleccione un archivo Excel");
    }

    @Test
    @DisplayName("Rechaza archivo que no es .xlsx (por ejemplo .csv o .xls)")
    void testRechazarArchivoNoXlsx() {
        MockMultipartFile csvFile = new MockMultipartFile("archivo", "datos.csv", "text/csv", "col1,col2".getBytes());

        assertThatThrownBy(() -> excelService.leerYValidarEstudiantes(csvFile, "admin"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("formato XLSX válido");
    }

    @Test
    @DisplayName("Rechaza archivo corrupto con extensión .xlsx")
    void testRechazarArchivoCorrupto() {
        MockMultipartFile corruptFile = new MockMultipartFile("archivo", "dañado.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "contenido no zip".getBytes());

        assertThatThrownBy(() -> excelService.leerYValidarEstudiantes(corruptFile, "admin"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("El archivo Excel está dañado");
    }

    @Test
    @DisplayName("Rechaza archivo que supera el tamaño máximo en bytes")
    void testRechazarArchivoExcedeTamano() {
        excelService.setMaxArchivoBytes(100);
        MockMultipartFile bigFile = new MockMultipartFile("archivo", "grande.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[500]);

        assertThatThrownBy(() -> excelService.leerYValidarEstudiantes(bigFile, "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("supera el tamaño máximo");
    }

    @Test
    @DisplayName("Rechaza archivo que supera el límite máximo de filas")
    void testRechazarArchivoExcedeMaxFilas() throws IOException {
        excelService.setMaxFilas(2);

        byte[] xlsxBytes = crearExcelEstudiantes(3); // 3 filas de datos
        MockMultipartFile file = new MockMultipartFile("archivo", "excede_filas.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", xlsxBytes);

        assertThatThrownBy(() -> excelService.leerYValidarEstudiantes(file, "admin"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("superando el límite máximo permitido de 2 filas");
    }

    @Test
    @DisplayName("Rechaza archivo sin encabezados obligatorios de estudiantes")
    void testRechazarEncabezadosFaltantesEstudiantes() throws IOException {
        byte[] xlsxBytes;
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("DATOS");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("tipo_documento");
            row.createCell(1).setCellValue("numero_documento");
            // Faltan nombres, apellidos, etc.
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("DNI");
            dataRow.createCell(1).setCellValue("71234567");

            wb.write(bos);
            xlsxBytes = bos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile("archivo", "faltan_headers.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", xlsxBytes);

        assertThatThrownBy(() -> excelService.leerYValidarEstudiantes(file, "admin"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("no contiene la columna obligatoria");
    }

    // =========================================================================
    // PRUEBAS DE VALIDACIÓN DE ESTUDIANTES
    // =========================================================================

    @Test
    @DisplayName("Valida correctamente estudiantes nuevos sin persistir en base de datos")
    void testValidarEstudiantesCorrecto() throws IOException {
        when(personaRepository.findByNumeroDocumento(anyString())).thenReturn(Optional.empty());

        byte[] bytes = crearExcelEstudiantes(1);
        MockMultipartFile file = new MockMultipartFile("archivo", "alumnos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        ImportacionResultadoDTO<EstudianteImportRow> resultado = excelService.leerYValidarEstudiantes(file, "admin");

        assertThat(resultado.getTotalFilas()).isEqualTo(1);
        assertThat(resultado.getFilasValidas()).isEqualTo(1);
        assertThat(resultado.getFilasConError()).isEqualTo(0);
        assertThat(resultado.getFilasExistentes()).isEqualTo(0);
        assertThat(resultado.esValidoParaImportar()).isTrue();
        assertThat(resultado.getToken()).isNotBlank();

        EstudianteImportRow fila = resultado.getFilas().get(0);
        assertThat(fila.getEstado()).isEqualTo(EstadoFilaImportacion.VALIDA);
        assertThat(fila.getNumeroDocumento()).isEqualTo("71234560");
        assertThat(fila.getTipoDocumento()).isEqualTo(TipoDocumento.DNI);
        assertThat(fila.getSexo()).isEqualTo(Sexo.MASCULINO);
    }

    @Test
    @DisplayName("Detecta DNI inválido y marca error de fila sin detener el procesamiento")
    void testDetectarDniInvalido() throws IOException {
        byte[] bytes;
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("ESTUDIANTES");
            crearCabeceraEstudiantes(sheet.createRow(0));

            Row r1 = sheet.createRow(1);
            llenarFilaEstudiante(r1, "DNI", "123", "Carlos", "Perez", "Gomez", "10/05/2012", "MASCULINO", "987654321", "c@test.com", "Dir 1");

            wb.write(bos);
            bytes = bos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile("archivo", "dni_invalido.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        ImportacionResultadoDTO<EstudianteImportRow> resultado = excelService.leerYValidarEstudiantes(file, "admin");

        assertThat(resultado.getFilasConError()).isEqualTo(1);
        assertThat(resultado.esValidoParaImportar()).isFalse();
        EstudianteImportRow fila = resultado.getFilas().get(0);
        assertThat(fila.getEstado()).isEqualTo(EstadoFilaImportacion.ERROR);
        assertThat(fila.getErrores()).anyMatch(err -> err.contains("exactamente 8 dígitos"));
    }

    @Test
    @DisplayName("Detecta documento duplicado dentro del mismo archivo Excel")
    void testDetectarDuplicadoDentroDeExcel() throws IOException {
        byte[] bytes;
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("ESTUDIANTES");
            crearCabeceraEstudiantes(sheet.createRow(0));

            Row r1 = sheet.createRow(1);
            llenarFilaEstudiante(r1, "DNI", "71234567", "Carlos", "Perez", "Gomez", "10/05/2012", "MASCULINO", "987654321", "c@test.com", "Dir 1");

            Row r2 = sheet.createRow(2);
            llenarFilaEstudiante(r2, "DNI", "71234567", "Maria", "Perez", "Gomez", "12/08/2012", "FEMENINO", "987654322", "m@test.com", "Dir 2");

            wb.write(bos);
            bytes = bos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile("archivo", "duplicados.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        ImportacionResultadoDTO<EstudianteImportRow> resultado = excelService.leerYValidarEstudiantes(file, "admin");

        assertThat(resultado.getTotalFilas()).isEqualTo(2);
        assertThat(resultado.getFilasConError()).isGreaterThanOrEqualTo(1);
        EstudianteImportRow fila2 = resultado.getFilas().get(1);
        assertThat(fila2.getEstado()).isEqualTo(EstadoFilaImportacion.ERROR);
        assertThat(fila2.getErrores()).anyMatch(err -> err.contains("duplicado en este mismo archivo"));
    }

    @Test
    @DisplayName("Detecta persona ya existente en PostgreSQL como YA_EXISTE y no la marca como error ni la duplica")
    void testDetectarPersonaExistenteEnPostgreSQL() throws IOException {
        Persona personaExistente = new Persona();
        personaExistente.setId(99L);
        personaExistente.setNumeroDocumento("71234567");

        when(personaRepository.findByNumeroDocumento("71234567")).thenReturn(Optional.of(personaExistente));

        byte[] bytes;
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("ESTUDIANTES");
            crearCabeceraEstudiantes(sheet.createRow(0));

            Row r1 = sheet.createRow(1);
            llenarFilaEstudiante(r1, "DNI", "71234567", "Carlos", "Perez", "Gomez", "10/05/2012", "MASCULINO", "987654321", "c@test.com", "Dir 1");

            wb.write(bos);
            bytes = bos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile("archivo", "existente.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        ImportacionResultadoDTO<EstudianteImportRow> resultado = excelService.leerYValidarEstudiantes(file, "admin");

        assertThat(resultado.getFilasExistentes()).isEqualTo(1);
        assertThat(resultado.getFilasConError()).isEqualTo(0);
        EstudianteImportRow fila = resultado.getFilas().get(0);
        assertThat(fila.getEstado()).isEqualTo(EstadoFilaImportacion.YA_EXISTE);
        assertThat(fila.getAdvertencias()).anyMatch(adv -> adv.contains("ya existe en la base de datos"));
    }

    // =========================================================================
    // PRUEBAS DE VALIDACIÓN DE MATRÍCULAS
    // =========================================================================

    @Test
    @DisplayName("Valida correctamente fila de matrícula cuando el estudiante y sección son válidos")
    void testValidarMatriculaCorrecta() throws IOException {
        Persona estudiante = new Persona();
        estudiante.setId(10L);
        estudiante.setNombres("Juan");
        estudiante.setApellidoPaterno("Perez");
        estudiante.setApellidoMaterno("Gomez");
        estudiante.setNumeroDocumento("71234567");

        AnioEscolar anio = new AnioEscolar();
        anio.setId(1L);
        anio.setAnio(2026);
        anio.setActivo(true);

        Grado grado = new Grado();
        grado.setId(2L);
        grado.setNivel(NivelEducativo.SECUNDARIA);
        grado.setNumero(1);
        grado.setNombre("1° SECUNDARIA");

        Seccion seccion = new Seccion();
        seccion.setId(3L);
        seccion.setNombre("A");
        seccion.setGrado(grado);
        seccion.setCapacidad(30);

        when(personaRepository.findByNumeroDocumento("71234567")).thenReturn(Optional.of(estudiante));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(10L, "ESTUDIANTE")).thenReturn(true);
        when(anioEscolarRepository.findByAnio(2026)).thenReturn(Optional.of(anio));
        when(gradoRepository.findByNivelAndNumero(NivelEducativo.SECUNDARIA, 1)).thenReturn(Optional.of(grado));
        when(seccionRepository.findByGradoIdAndNombre(2L, "A")).thenReturn(Optional.of(seccion));
        when(matriculaRepository.existsByCodigoMatricula("MAT-2026-001")).thenReturn(false);
        when(matriculaRepository.existsByEstudianteIdAndAnioEscolarId(10L, 1L)).thenReturn(false);
        when(matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(3L, 1L, EstadoMatricula.MATRICULADO)).thenReturn(10L);

        byte[] bytes;
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("MATRICULAS");
            crearCabeceraMatriculas(sheet.createRow(0));

            Row r1 = sheet.createRow(1);
            llenarFilaMatricula(r1, "71234567", "2026", "SECUNDARIA", "1", "A", "MAT-2026-001", "01/03/2026", "MATRICULADO", "Observación");

            wb.write(bos);
            bytes = bos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile("archivo", "matricula_ok.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        ImportacionResultadoDTO<MatriculaImportRow> resultado = excelService.leerYValidarMatriculas(file, "admin");

        assertThat(resultado.getTotalFilas()).isEqualTo(1);
        assertThat(resultado.getFilasValidas()).isEqualTo(1);
        assertThat(resultado.getFilasConError()).isEqualTo(0);
        assertThat(resultado.esValidoParaImportar()).isTrue();

        MatriculaImportRow fila = resultado.getFilas().get(0);
        assertThat(fila.getEstadoFila()).isEqualTo(EstadoFilaImportacion.VALIDA);
        assertThat(fila.getPersonaEstudianteId()).isEqualTo(10L);
        assertThat(fila.getAnioEscolarId()).isEqualTo(1L);
        assertThat(fila.getGradoId()).isEqualTo(2L);
        assertThat(fila.getSeccionId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Detecta error si el estudiante no existe para la matrícula")
    void testDetectarEstudianteInexistenteMatricula() throws IOException {
        when(personaRepository.findByNumeroDocumento("79999999")).thenReturn(Optional.empty());

        byte[] bytes;
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("MATRICULAS");
            crearCabeceraMatriculas(sheet.createRow(0));

            Row r1 = sheet.createRow(1);
            llenarFilaMatricula(r1, "79999999", "2026", "SECUNDARIA", "1", "A", "MAT-2026-001", "01/03/2026", "MATRICULADO", "Observación");

            wb.write(bos);
            bytes = bos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile("archivo", "estudiante_no_existe.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        ImportacionResultadoDTO<MatriculaImportRow> resultado = excelService.leerYValidarMatriculas(file, "admin");

        assertThat(resultado.getFilasConError()).isEqualTo(1);
        MatriculaImportRow fila = resultado.getFilas().get(0);
        assertThat(fila.getEstadoFila()).isEqualTo(EstadoFilaImportacion.ERROR);
        assertThat(fila.getErrores()).anyMatch(err -> err.contains("no existe en el sistema"));
    }

    @Test
    @DisplayName("Detecta sección sin vacantes acumuladas en el mismo archivo")
    void testDetectarSeccionSinVacantesEnArchivo() throws IOException {
        Persona e1 = new Persona(); e1.setId(1L); e1.setNumeroDocumento("71111111");
        Persona e2 = new Persona(); e2.setId(2L); e2.setNumeroDocumento("72222222");

        AnioEscolar anio = new AnioEscolar(); anio.setId(1L); anio.setAnio(2026); anio.setActivo(true);
        Grado grado = new Grado(); grado.setId(10L); grado.setNivel(NivelEducativo.PRIMARIA); grado.setNumero(1);
        Seccion seccion = new Seccion(); seccion.setId(100L); seccion.setNombre("A"); seccion.setGrado(grado); seccion.setCapacidad(2);

        when(personaRepository.findByNumeroDocumento("71111111")).thenReturn(Optional.of(e1));
        when(personaRepository.findByNumeroDocumento("72222222")).thenReturn(Optional.of(e2));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(anyLong(), eq("ESTUDIANTE"))).thenReturn(true);
        when(anioEscolarRepository.findByAnio(2026)).thenReturn(Optional.of(anio));
        when(gradoRepository.findByNivelAndNumero(NivelEducativo.PRIMARIA, 1)).thenReturn(Optional.of(grado));
        when(seccionRepository.findByGradoIdAndNombre(10L, "A")).thenReturn(Optional.of(seccion));

        // En BD ya hay 1 matriculado. Capacidad es 2.
        // Fila 1 toma cupo 2 (último vacante).
        // Fila 2 excede capacidad.
        when(matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(100L, 1L, EstadoMatricula.MATRICULADO)).thenReturn(1L);

        byte[] bytes;
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("MATRICULAS");
            crearCabeceraMatriculas(sheet.createRow(0));

            Row r1 = sheet.createRow(1);
            llenarFilaMatricula(r1, "71111111", "2026", "PRIMARIA", "1", "A", "MAT-001", "01/03/2026", "MATRICULADO", "");

            Row r2 = sheet.createRow(2);
            llenarFilaMatricula(r2, "72222222", "2026", "PRIMARIA", "1", "A", "MAT-002", "01/03/2026", "MATRICULADO", "");

            wb.write(bos);
            bytes = bos.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile("archivo", "excede_capacidad.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);

        ImportacionResultadoDTO<MatriculaImportRow> resultado = excelService.leerYValidarMatriculas(file, "admin");

        assertThat(resultado.getTotalFilas()).isEqualTo(2);
        assertThat(resultado.getFilasValidas()).isEqualTo(1);
        assertThat(resultado.getFilasConError()).isEqualTo(1);

        MatriculaImportRow fila2 = resultado.getFilas().get(1);
        assertThat(fila2.getEstadoFila()).isEqualTo(EstadoFilaImportacion.ERROR);
        assertThat(fila2.getErrores()).anyMatch(err -> err.contains("no cuenta con vacantes suficientes"));
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE CREACIÓN DE ARCHIVOS EXCEL
    // =========================================================================

    private byte[] crearExcelEstudiantes(int cantidadFilas) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("ESTUDIANTES");
            crearCabeceraEstudiantes(sheet.createRow(0));

            for (int i = 0; i < cantidadFilas; i++) {
                Row row = sheet.createRow(i + 1);
                String dni = "7123456" + i;
                llenarFilaEstudiante(row, "DNI", dni, "Nombre" + i, "Paterno" + i, "Materno" + i,
                        "15/04/2012", "MASCULINO", "98765432" + i, "alumno" + i + "@colegio.edu.pe", "Calle " + i);
            }

            wb.write(bos);
            return bos.toByteArray();
        }
    }

    private void crearCabeceraEstudiantes(Row row) {
        row.createCell(0).setCellValue("tipo_documento");
        row.createCell(1).setCellValue("numero_documento");
        row.createCell(2).setCellValue("nombres");
        row.createCell(3).setCellValue("apellido_paterno");
        row.createCell(4).setCellValue("apellido_materno");
        row.createCell(5).setCellValue("fecha_nacimiento");
        row.createCell(6).setCellValue("sexo");
        row.createCell(7).setCellValue("telefono");
        row.createCell(8).setCellValue("correo");
        row.createCell(9).setCellValue("direccion");
    }

    private void llenarFilaEstudiante(Row row, String tipoDoc, String numDoc, String nombres,
                                      String apePat, String apeMat, String fecNac, String sexo,
                                      String tel, String correo, String dir) {
        row.createCell(0).setCellValue(tipoDoc);
        row.createCell(1).setCellValue(numDoc);
        row.createCell(2).setCellValue(nombres);
        row.createCell(3).setCellValue(apePat);
        row.createCell(4).setCellValue(apeMat);
        row.createCell(5).setCellValue(fecNac);
        row.createCell(6).setCellValue(sexo);
        row.createCell(7).setCellValue(tel);
        row.createCell(8).setCellValue(correo);
        row.createCell(9).setCellValue(dir);
    }

    private void crearCabeceraMatriculas(Row row) {
        row.createCell(0).setCellValue("numero_documento_estudiante");
        row.createCell(1).setCellValue("anio");
        row.createCell(2).setCellValue("nivel");
        row.createCell(3).setCellValue("grado");
        row.createCell(4).setCellValue("seccion");
        row.createCell(5).setCellValue("codigo_matricula");
        row.createCell(6).setCellValue("fecha_matricula");
        row.createCell(7).setCellValue("estado");
        row.createCell(8).setCellValue("observaciones");
    }

    private void llenarFilaMatricula(Row row, String numDocEst, String anio, String nivel,
                                     String grado, String seccion, String codMat, String fecMat,
                                     String estado, String obs) {
        row.createCell(0).setCellValue(numDocEst);
        row.createCell(1).setCellValue(anio);
        row.createCell(2).setCellValue(nivel);
        row.createCell(3).setCellValue(grado);
        row.createCell(4).setCellValue(seccion);
        row.createCell(5).setCellValue(codMat);
        row.createCell(6).setCellValue(fecMat);
        row.createCell(7).setCellValue(estado);
        row.createCell(8).setCellValue(obs);
    }
}
