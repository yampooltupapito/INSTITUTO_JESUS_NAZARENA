package com.jesusnazareno.asistencia.modules.matricula;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoResponse;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaResponse;
import com.jesusnazareno.asistencia.modules.matricula.dto.SeccionCapacidadDTO;
import com.jesusnazareno.asistencia.modules.matricula.service.MatriculaService;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Seguridad y Web - MatriculaController")
class MatriculaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatriculaService matriculaService;

    @MockBean
    private EstudianteService estudianteService;

    @MockBean
    private AnioEscolarService anioEscolarService;

    @MockBean
    private GradoService gradoService;

    @MockBean
    private SeccionService seccionService;

    @Test
    @DisplayName("1. /matriculas sin autenticación redirige al login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/matriculas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "docente_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("2. /matriculas sin permiso MATRICULA_VER retorna 403 Forbidden")
    void listar_sinPermiso_retorna403() throws Exception {
        mockMvc.perform(get("/matriculas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "auxiliar_user", authorities = {"MATRICULA_VER"})
    @DisplayName("3. /matriculas con MATRICULA_VER retorna 200 OK y vista matriculas/list")
    void listar_conPermiso_retorna200YVista() throws Exception {
        Page<MatriculaResponse> paginaMock = new PageImpl<>(List.of(
                MatriculaResponse.builder()
                        .id(1L)
                        .codigoMatricula("MAT-2026-001")
                        .estudianteNumeroDocumento("77889900")
                        .estudianteNombreCompleto("Pérez, Juan")
                        .anioEscolarAnio(2026)
                        .gradoNombre("1° Secundaria")
                        .seccionNombre("A")
                        .fechaMatricula(LocalDate.of(2026, 3, 1))
                        .estado(EstadoMatricula.MATRICULADO)
                        .build()
        ));

        when(matriculaService.buscarMatriculas(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(paginaMock);
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/matriculas"))
                .andExpect(status().isOk())
                .andExpect(view().name("matriculas/list"))
                .andExpect(model().attributeExists("matriculas", "aniosEscolares", "grados", "estados"));
    }

    @Test
    @WithMockUser(username = "usuario_lector", authorities = {"MATRICULA_VER"})
    @DisplayName("4. /matriculas/nueva sin MATRICULA_CREAR retorna 403 Forbidden")
    void nueva_sinPermisoCrear_retorna403() throws Exception {
        mockMvc.perform(get("/matriculas/nueva"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"MATRICULA_CREAR"})
    @DisplayName("5. /matriculas/nueva con MATRICULA_CREAR retorna 200 y vista matriculas/form")
    void nueva_conPermisoCrear_retorna200() throws Exception {
        when(estudianteService.listarTodosEstudiantesActivos()).thenReturn(Collections.emptyList());
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/matriculas/nueva"))
                .andExpect(status().isOk())
                .andExpect(view().name("matriculas/form"))
                .andExpect(model().attributeExists("matricula", "estudiantes", "aniosEscolares", "grados", "estados"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"MATRICULA_CREAR"})
    @DisplayName("6. POST /matriculas sin token CSRF es rechazado con 403")
    void crear_sinCsrf_retorna403() throws Exception {
        mockMvc.perform(post("/matriculas")
                        .param("personaEstudianteId", "1")
                        .param("anioEscolarId", "2")
                        .param("gradoId", "3")
                        .param("seccionId", "4")
                        .param("codigoMatricula", "MAT-2026-001")
                        .param("fechaMatricula", "2026-03-01")
                        .param("estado", "MATRICULADO"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"MATRICULA_CREAR"})
    @DisplayName("7. POST /matriculas con campos vacíos mantiene vista y muestra errores")
    void crear_conCamposInvalidos_mantieneVistaYErrores() throws Exception {
        when(estudianteService.listarTodosEstudiantesActivos()).thenReturn(Collections.emptyList());
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/matriculas")
                        .with(csrf())
                        .param("personaEstudianteId", "")
                        .param("codigoMatricula", "")
                        .param("fechaMatricula", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("matriculas/form"))
                .andExpect(model().attributeHasFieldErrors("matricula", "personaEstudianteId", "codigoMatricula", "fechaMatricula"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"MATRICULA_CREAR"})
    @DisplayName("8. POST /matriculas con datos válidos registra y aplica PRG")
    void crear_conDatosValidos_aplicaPRG() throws Exception {
        MatriculaResponse matriculaCreada = MatriculaResponse.builder()
                .id(100L)
                .codigoMatricula("MAT-2026-100")
                .build();

        when(matriculaService.crearMatricula(any(MatriculaRequest.class))).thenReturn(matriculaCreada);

        mockMvc.perform(post("/matriculas")
                        .with(csrf())
                        .param("personaEstudianteId", "1")
                        .param("anioEscolarId", "2")
                        .param("gradoId", "3")
                        .param("seccionId", "4")
                        .param("codigoMatricula", "MAT-2026-100")
                        .param("fechaMatricula", "2026-03-01")
                        .param("estado", "MATRICULADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/matriculas"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(matriculaService).crearMatricula(any(MatriculaRequest.class));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"MATRICULA_CREAR"})
    @DisplayName("9. POST /matriculas cuando falla regla de negocio mantiene vista y muestra mensaje")
    void crear_cuandoFallaReglaNegocio_mantieneVista() throws Exception {
        when(matriculaService.crearMatricula(any(MatriculaRequest.class)))
                .thenThrow(new BusinessException("El estudiante ya tiene una matrícula para este año escolar."));
        when(estudianteService.listarTodosEstudiantesActivos()).thenReturn(Collections.emptyList());
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/matriculas")
                        .with(csrf())
                        .param("personaEstudianteId", "1")
                        .param("anioEscolarId", "2")
                        .param("gradoId", "3")
                        .param("seccionId", "4")
                        .param("codigoMatricula", "MAT-2026-100")
                        .param("fechaMatricula", "2026-03-01")
                        .param("estado", "MATRICULADO"))
                .andExpect(status().isOk())
                .andExpect(view().name("matriculas/form"))
                .andExpect(model().attributeHasErrors("matricula"));
    }

    @Test
    @WithMockUser(username = "auxiliar_user", authorities = {"MATRICULA_VER"})
    @DisplayName("10. GET /matriculas/{id} con MATRICULA_VER retorna 200 y vista detail")
    void detalle_conPermiso_retorna200YVista() throws Exception {
        MatriculaResponse matriculaMock = MatriculaResponse.builder()
                .id(1L)
                .codigoMatricula("MAT-2026-001")
                .estudianteNumeroDocumento("77889900")
                .estudianteNombreCompleto("Carlos Mendoza")
                .anioEscolarAnio(2026)
                .gradoNombre("1° Secundaria")
                .seccionNombre("A")
                .estado(EstadoMatricula.MATRICULADO)
                .fechaMatricula(LocalDate.of(2026, 3, 1))
                .build();

        when(matriculaService.buscarPorId(1L)).thenReturn(matriculaMock);

        mockMvc.perform(get("/matriculas/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("matriculas/detail"))
                .andExpect(model().attributeExists("matricula", "estados"));
    }

    @Test
    @WithMockUser(username = "usuario_lector", authorities = {"MATRICULA_VER"})
    @DisplayName("11. GET /matriculas/{id}/editar sin MATRICULA_EDITAR retorna 403")
    void editar_sinPermisoEditar_retorna403() throws Exception {
        mockMvc.perform(get("/matriculas/1/editar"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"MATRICULA_EDITAR"})
    @DisplayName("12. GET /matriculas/{id}/editar con MATRICULA_EDITAR retorna 200 y form")
    void editar_conPermisoEditar_retorna200() throws Exception {
        MatriculaResponse matriculaMock = MatriculaResponse.builder()
                .id(1L)
                .personaEstudianteId(10L)
                .anioEscolarId(2L)
                .gradoId(3L)
                .seccionId(4L)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 3, 1))
                .estado(EstadoMatricula.MATRICULADO)
                .build();

        when(matriculaService.buscarPorId(1L)).thenReturn(matriculaMock);
        when(estudianteService.listarTodosEstudiantesActivos()).thenReturn(Collections.emptyList());
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/matriculas/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("matriculas/form"))
                .andExpect(model().attributeExists("matricula", "modoEdicion", "matriculaId", "matriculaOriginal"));
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"MATRICULA_EDITAR"})
    @DisplayName("13. POST /matriculas/{id} con MATRICULA_EDITAR y CSRF actualiza y aplica PRG")
    void actualizar_conDatosValidos_aplicaPRG() throws Exception {
        MatriculaResponse actualizada = MatriculaResponse.builder().id(1L).build();
        when(matriculaService.actualizarMatricula(eq(1L), any(MatriculaRequest.class))).thenReturn(actualizada);

        mockMvc.perform(post("/matriculas/1")
                        .with(csrf())
                        .param("personaEstudianteId", "10")
                        .param("anioEscolarId", "2")
                        .param("gradoId", "3")
                        .param("seccionId", "4")
                        .param("codigoMatricula", "MAT-2026-001")
                        .param("fechaMatricula", "2026-03-01")
                        .param("estado", "MATRICULADO")
                        .param("observaciones", "Actualizado"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/matriculas/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(matriculaService).actualizarMatricula(eq(1L), any(MatriculaRequest.class));
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"MATRICULA_EDITAR"})
    @DisplayName("14. POST /matriculas/{id}/estado con MATRICULA_EDITAR actualiza estado y aplica PRG")
    void cambiarEstado_conPermiso_aplicaPRG() throws Exception {
        when(matriculaService.cambiarEstado(1L, EstadoMatricula.RETIRADO))
                .thenReturn(MatriculaResponse.builder().id(1L).estado(EstadoMatricula.RETIRADO).build());

        mockMvc.perform(post("/matriculas/1/estado")
                        .with(csrf())
                        .param("estado", "RETIRADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/matriculas/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(matriculaService).cambiarEstado(1L, EstadoMatricula.RETIRADO);
    }

    @Test
    @WithMockUser(username = "auxiliar_user", authorities = {"MATRICULA_VER"})
    @DisplayName("15. GET /matriculas/secciones devuelve lista JSON con cupos y vacantes")
    void secciones_conPermiso_retornaJson() throws Exception {
        List<SeccionCapacidadDTO> seccionesMock = List.of(
                new SeccionCapacidadDTO(4L, "A", Turno.MANANA, 30, 25L, 5L),
                new SeccionCapacidadDTO(5L, "B", Turno.MANANA, 30, 30L, 0L)
        );

        when(matriculaService.listarSeccionesConCapacidad(3L, 2L)).thenReturn(seccionesMock);

        mockMvc.perform(get("/matriculas/secciones")
                        .param("gradoId", "3")
                        .param("anioEscolarId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("A"))
                .andExpect(jsonPath("$[0].vacantes").value(5))
                .andExpect(jsonPath("$[1].nombre").value("B"))
                .andExpect(jsonPath("$[1].vacantes").value(0));
    }
}
