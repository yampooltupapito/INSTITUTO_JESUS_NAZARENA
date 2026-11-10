package com.jesusnazareno.asistencia.modules.importacion;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstadoFilaImportacion;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstudianteImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.ImportacionResultadoDTO;
import com.jesusnazareno.asistencia.modules.importacion.dto.MatriculaImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.TipoImportacion;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionEjecucionService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionEjecucionServiceImpl;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionExcelService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionExcelServiceImpl;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.matricula.service.MatriculaService;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ImportacionEjecucionService")
class ImportacionEjecucionServiceTest {

    @Mock
    private EstudianteService estudianteService;

    @Mock
    private MatriculaService matriculaService;

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

    private ImportacionExcelService importacionExcelService;
    private ImportacionEjecucionService ejecucionService;

    @BeforeEach
    void setUp() {
        importacionExcelService = new ImportacionExcelServiceImpl(
                personaRepository,
                personaActorRepository,
                anioEscolarRepository,
                gradoRepository,
                seccionRepository,
                matriculaRepository
        );
        ejecucionService = new ImportacionEjecucionServiceImpl(
                importacionExcelService,
                estudianteService,
                matriculaService,
                personaRepository,
                matriculaRepository
        );
    }

    @Test
    @DisplayName("Confirmar estudiantes persiste los alumnos válidos llamando a EstudianteService")
    void testConfirmarImportacionEstudiantesExitoso() {
        String token = "tok-123";
        String username = "admin";

        ImportacionResultadoDTO<EstudianteImportRow> resultado = new ImportacionResultadoDTO<>(token, "estudiantes.xlsx", TipoImportacion.ESTUDIANTES, username);
        resultado.setFilasConError(0);

        EstudianteImportRow fila = new EstudianteImportRow(2);
        fila.setTipoDocumento(TipoDocumento.DNI);
        fila.setNumeroDocumento("71234567");
        fila.setNombres("Juan");
        fila.setApellidoPaterno("Perez");
        fila.setApellidoMaterno("Gomez");
        fila.setFechaNacimiento(LocalDate.of(2012, 5, 10));
        fila.setSexo(Sexo.MASCULINO);
        fila.setEstado(EstadoFilaImportacion.VALIDA);
        resultado.setFilas(List.of(fila));

        importacionExcelService.guardarResultadoEnCache(token, resultado, username, 30);
        when(personaRepository.existsByNumeroDocumento("71234567")).thenReturn(false);

        int insertados = ejecucionService.confirmarImportacionEstudiantes(token, username);

        assertThat(insertados).isEqualTo(1);
        verify(estudianteService).registrarEstudiante(any(PersonaRequest.class));

        // Debe limpiar la caché tras procesar
        assertThatThrownBy(() -> importacionExcelService.obtenerResultadoTemporal(token, username))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Rechaza confirmación de estudiantes si el archivo contenía errores")
    void testConfirmarEstudiantesRechazaSiHayErrores() {
        String token = "tok-err";
        String username = "admin";

        ImportacionResultadoDTO<EstudianteImportRow> resultado = new ImportacionResultadoDTO<>(token, "con_error.xlsx", TipoImportacion.ESTUDIANTES, username);
        resultado.setFilasConError(1);

        importacionExcelService.guardarResultadoEnCache(token, resultado, username, 30);

        assertThatThrownBy(() -> ejecucionService.confirmarImportacionEstudiantes(token, username))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No se puede confirmar la importación porque el archivo contiene errores");

        verify(estudianteService, never()).registrarEstudiante(any());
    }

    @Test
    @DisplayName("Re-valida concurrencia y omite inserción si la persona fue creada entre validación y confirmación")
    void testConfirmarEstudiantesOmiteSiYaExisteEnBD() {
        String token = "tok-dup";
        String username = "admin";

        ImportacionResultadoDTO<EstudianteImportRow> resultado = new ImportacionResultadoDTO<>(token, "alumnos.xlsx", TipoImportacion.ESTUDIANTES, username);
        resultado.setFilasConError(0);

        EstudianteImportRow fila = new EstudianteImportRow(2);
        fila.setNumeroDocumento("71234567");
        fila.setEstado(EstadoFilaImportacion.VALIDA);
        resultado.setFilas(List.of(fila));

        importacionExcelService.guardarResultadoEnCache(token, resultado, username, 30);
        // Simular que otro usuario creó la persona justo antes de confirmar
        when(personaRepository.existsByNumeroDocumento("71234567")).thenReturn(true);

        int insertados = ejecucionService.confirmarImportacionEstudiantes(token, username);

        assertThat(insertados).isEqualTo(0);
        verify(estudianteService, never()).registrarEstudiante(any());
    }

    @Test
    @DisplayName("Confirmar matrículas persiste registros válidos llamando a MatriculaService")
    void testConfirmarImportacionMatriculasExitoso() {
        String token = "tok-mat-123";
        String username = "admin";

        ImportacionResultadoDTO<MatriculaImportRow> resultado = new ImportacionResultadoDTO<>(token, "matriculas.xlsx", TipoImportacion.MATRICULAS, username);
        resultado.setFilasConError(0);

        MatriculaImportRow fila = new MatriculaImportRow(2);
        fila.setPersonaEstudianteId(10L);
        fila.setAnioEscolarId(1L);
        fila.setGradoId(2L);
        fila.setSeccionId(3L);
        fila.setCodigoMatricula("MAT-2026-001");
        fila.setFechaMatricula(LocalDate.of(2026, 3, 1));
        fila.setEstado(EstadoMatricula.MATRICULADO);
        fila.setEstadoFila(EstadoFilaImportacion.VALIDA);
        resultado.setFilas(List.of(fila));

        importacionExcelService.guardarResultadoEnCache(token, resultado, username, 30);
        when(matriculaRepository.existsByCodigoMatricula("MAT-2026-001")).thenReturn(false);
        when(matriculaRepository.existsByEstudianteIdAndAnioEscolarId(10L, 1L)).thenReturn(false);

        int creadas = ejecucionService.confirmarImportacionMatriculas(token, username);

        assertThat(creadas).isEqualTo(1);
        verify(matriculaService).crearMatricula(any(MatriculaRequest.class));
    }

    @Test
    @DisplayName("Re-valida duplicidad de matrícula contra BD en confirmación y aborta con excepción para rollback")
    void testConfirmarMatriculasRechazaSiYaExisteMatriculaEnBD() {
        String token = "tok-mat-dup";
        String username = "admin";

        ImportacionResultadoDTO<MatriculaImportRow> resultado = new ImportacionResultadoDTO<>(token, "matriculas.xlsx", TipoImportacion.MATRICULAS, username);
        resultado.setFilasConError(0);

        MatriculaImportRow fila = new MatriculaImportRow(2);
        fila.setNumeroDocumentoEstudiante("71234567");
        fila.setPersonaEstudianteId(10L);
        fila.setAnioEscolarId(1L);
        fila.setCodigoMatricula("MAT-2026-001");
        fila.setEstadoFila(EstadoFilaImportacion.VALIDA);
        resultado.setFilas(List.of(fila));

        importacionExcelService.guardarResultadoEnCache(token, resultado, username, 30);
        when(matriculaRepository.existsByCodigoMatricula("MAT-2026-001")).thenReturn(false);
        when(matriculaRepository.existsByEstudianteIdAndAnioEscolarId(10L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> ejecucionService.confirmarImportacionMatriculas(token, username))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya cuenta con matrícula en el año seleccionado");

        verify(matriculaService, never()).crearMatricula(any());
    }
}
