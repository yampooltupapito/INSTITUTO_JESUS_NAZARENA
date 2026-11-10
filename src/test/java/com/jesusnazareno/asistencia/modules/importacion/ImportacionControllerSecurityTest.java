package com.jesusnazareno.asistencia.modules.importacion;

import com.jesusnazareno.asistencia.modules.importacion.dto.EstudianteImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.ImportacionResultadoDTO;
import com.jesusnazareno.asistencia.modules.importacion.dto.TipoImportacion;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionEjecucionService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionExcelService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionPlantillaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Seguridad y Web - ImportacionController")
class ImportacionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportacionPlantillaService plantillaService;

    @MockBean
    private ImportacionExcelService excelService;

    @MockBean
    private ImportacionEjecucionService ejecucionService;

    // =========================================================================
    // 1. CONTROL DE ACCESO ANÓNIMO
    // =========================================================================

    @Test
    @DisplayName("Usuario anónimo que accede a /importaciones es redirigido a login")
    void testAnonimoRedirigidoALogin() throws Exception {
        mockMvc.perform(get("/importaciones"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    // =========================================================================
    // 2. PERMISOS DE ESTUDIANTES (IMPORTACION_ESTUDIANTE)
    // =========================================================================

    @Test
    @WithMockUser(username = "docente", authorities = {"ASISTENCIA_REGISTRAR"})
    @DisplayName("Usuario sin permiso IMPORTACION_ESTUDIANTE recibe 403 Forbidden al intentar ver formulario")
    void testSinPermisoEstudianteAccesoDenegado() throws Exception {
        mockMvc.perform(get("/importaciones/estudiantes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"IMPORTACION_ESTUDIANTE"})
    @DisplayName("Usuario con permiso IMPORTACION_ESTUDIANTE accede exitosamente a /importaciones/estudiantes")
    void testConPermisoEstudianteAccesoPermitido() throws Exception {
        mockMvc.perform(get("/importaciones/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(view().name("importaciones/estudiantes"))
                .andExpect(model().attribute("currentMenu", "importaciones"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"IMPORTACION_ESTUDIANTE"})
    @DisplayName("Descarga de plantilla de estudiantes retorna XLSX con Content-Disposition adecuado")
    void testDescargarPlantillaEstudiantes() throws Exception {
        when(plantillaService.generarPlantillaEstudiantes()).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/importaciones/estudiantes/plantilla"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"plantilla_estudiantes.xlsx\""));
    }

    // =========================================================================
    // 3. PERMISOS DE MATRÍCULAS (IMPORTACION_MATRICULA)
    // =========================================================================

    @Test
    @WithMockUser(username = "docente", authorities = {"ASISTENCIA_REGISTRAR"})
    @DisplayName("Usuario sin permiso IMPORTACION_MATRICULA recibe 403 Forbidden")
    void testSinPermisoMatriculaAccesoDenegado() throws Exception {
        mockMvc.perform(get("/importaciones/matriculas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"IMPORTACION_MATRICULA"})
    @DisplayName("Usuario con permiso IMPORTACION_MATRICULA accede exitosamente a /importaciones/matriculas")
    void testConPermisoMatriculaAccesoPermitido() throws Exception {
        mockMvc.perform(get("/importaciones/matriculas"))
                .andExpect(status().isOk())
                .andExpect(view().name("importaciones/matriculas"))
                .andExpect(model().attribute("currentMenu", "importaciones"));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"IMPORTACION_MATRICULA"})
    @DisplayName("Descarga de plantilla de matrículas retorna XLSX con Content-Disposition adecuado")
    void testDescargarPlantillaMatriculas() throws Exception {
        when(plantillaService.generarPlantillaMatriculas()).thenReturn(new byte[]{4, 5, 6});

        mockMvc.perform(get("/importaciones/matriculas/plantilla"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"plantilla_matriculas.xlsx\""));
    }

    // =========================================================================
    // 4. PROTECCIÓN CSRF
    // =========================================================================

    @Test
    @WithMockUser(username = "admin", authorities = {"IMPORTACION_ESTUDIANTE"})
    @DisplayName("POST a /importaciones/estudiantes/validar sin token CSRF es rechazado con 403 Forbidden")
    void testValidarEstudiantesSinCsrfRechazado() throws Exception {
        MockMultipartFile file = new MockMultipartFile("archivo", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1, 2});

        mockMvc.perform(multipart("/importaciones/estudiantes/validar").file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"IMPORTACION_ESTUDIANTE"})
    @DisplayName("POST a /importaciones/estudiantes/validar con CSRF procesa el archivo y redirige a preview")
    void testValidarEstudiantesConCsrfExitoso() throws Exception {
        MockMultipartFile file = new MockMultipartFile("archivo", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1, 2});

        ImportacionResultadoDTO<EstudianteImportRow> resultado = new ImportacionResultadoDTO<>("tok-1", "test.xlsx", TipoImportacion.ESTUDIANTES, "admin");
        when(excelService.leerYValidarEstudiantes(any(), eq("admin"))).thenReturn(resultado);

        mockMvc.perform(multipart("/importaciones/estudiantes/validar")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("importaciones/preview"))
                .andExpect(model().attributeExists("resultado"))
                .andExpect(model().attribute("tipoImportacion", "ESTUDIANTES"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"IMPORTACION_ESTUDIANTE"})
    @DisplayName("POST a /importaciones/estudiantes/confirmar sin token CSRF es rechazado con 403 Forbidden")
    void testConfirmarEstudiantesSinCsrfRechazado() throws Exception {
        mockMvc.perform(post("/importaciones/estudiantes/confirmar")
                        .param("token", "tok-123"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"IMPORTACION_ESTUDIANTE"})
    @DisplayName("POST a /importaciones/estudiantes/confirmar con CSRF invoca confirmación y redirige con mensaje")
    void testConfirmarEstudiantesConCsrfExitoso() throws Exception {
        when(ejecucionService.confirmarImportacionEstudiantes("tok-123", "admin")).thenReturn(5);

        mockMvc.perform(post("/importaciones/estudiantes/confirmar")
                        .param("token", "tok-123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudiantes"));
    }
}
