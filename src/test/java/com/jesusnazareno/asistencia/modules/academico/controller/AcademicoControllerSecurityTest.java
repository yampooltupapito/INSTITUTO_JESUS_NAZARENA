package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoResponse;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Seguridad y Web - Prompt 21: Módulo Académico")
class AcademicoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditorAware<String> auditorAware;

    @MockBean
    private AnioEscolarService anioEscolarService;

    @MockBean
    private GradoService gradoService;

    @MockBean
    private SeccionService seccionService;

    // ==========================================
    // 1. RUTAS REQUIEREN AUTENTICACIÓN
    // ==========================================
    @Test
    @DisplayName("1. /academico/anios sin autenticación redirige al login")
    void anios_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/academico/anios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("1.b /academico/grados sin autenticación redirige al login")
    void grados_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/academico/grados"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("1.c /academico/secciones sin autenticación redirige al login")
    void secciones_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/academico/secciones"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    // ==========================================
    // 2. ACADEMICO_VER PUEDE LISTAR
    // ==========================================
    @Test
    @WithMockUser(username = "directora", authorities = {"ACADEMICO_VER"})
    @DisplayName("2. ACADEMICO_VER puede listar años escolares (HTTP 200)")
    void anios_conPermisoVer_retorna200YVista() throws Exception {
        Page<AnioEscolarResponse> pageMock = new PageImpl<>(Collections.emptyList());
        when(anioEscolarService.listarPaginado(any(), any(Pageable.class))).thenReturn(pageMock);

        mockMvc.perform(get("/academico/anios"))
                .andExpect(status().isOk())
                .andExpect(view().name("academico/anios/list"))
                .andExpect(model().attributeExists("anios"));
    }

    @Test
    @WithMockUser(username = "directora", authorities = {"ACADEMICO_VER"})
    @DisplayName("2.b ACADEMICO_VER puede listar grados (HTTP 200)")
    void grados_conPermisoVer_retorna200YVista() throws Exception {
        Page<GradoResponse> pageMock = new PageImpl<>(Collections.emptyList());
        when(gradoService.listarPaginado(any(), any(), any(Pageable.class))).thenReturn(pageMock);

        mockMvc.perform(get("/academico/grados"))
                .andExpect(status().isOk())
                .andExpect(view().name("academico/grados/list"))
                .andExpect(model().attributeExists("grados"));
    }

    @Test
    @WithMockUser(username = "directora", authorities = {"ACADEMICO_VER"})
    @DisplayName("2.c ACADEMICO_VER puede listar secciones (HTTP 200)")
    void secciones_conPermisoVer_retorna200YVista() throws Exception {
        Page<SeccionResponse> pageMock = new PageImpl<>(Collections.emptyList());
        when(seccionService.buscarPaginado(any(), any(), any(), any(), any(Pageable.class))).thenReturn(pageMock);
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/academico/secciones"))
                .andExpect(status().isOk())
                .andExpect(view().name("academico/secciones/list"))
                .andExpect(model().attributeExists("secciones"));
    }

    // ==========================================
    // 3. SIN PERMISO DEVUELVE 403
    // ==========================================
    @Test
    @WithMockUser(username = "usuario_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("3. Sin permiso ACADEMICO_VER devuelve 403 Forbidden")
    void academico_sinPermisoVer_retorna403() throws Exception {
        mockMvc.perform(get("/academico/anios"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/academico/grados"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/academico/secciones"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 4. ACADEMICO_CREAR PERMITE CREAR
    // ==========================================
    @Test
    @WithMockUser(username = "admin_user", authorities = {"ACADEMICO_CREAR"})
    @DisplayName("4. ACADEMICO_CREAR permite crear año escolar con POST y CSRF")
    void anios_crearConPermiso_registraYRedirige() throws Exception {
        AnioEscolarResponse response = AnioEscolarResponse.builder()
                .id(1L)
                .anio(2027)
                .nombre("Año Escolar 2027")
                .fechaInicio(LocalDate.of(2027, 3, 1))
                .fechaFin(LocalDate.of(2027, 12, 20))
                .activo(true)
                .build();
        when(anioEscolarService.crearAnioEscolar(any(AnioEscolarRequest.class))).thenReturn(response);

        mockMvc.perform(post("/academico/anios")
                        .with(csrf())
                        .param("anio", "2027")
                        .param("nombre", "Año Escolar 2027")
                        .param("fechaInicio", "2027-03-01")
                        .param("fechaFin", "2027-12-20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/anios"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(anioEscolarService).crearAnioEscolar(any(AnioEscolarRequest.class));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ACADEMICO_CREAR"})
    @DisplayName("4.b ACADEMICO_CREAR permite crear sección con POST y CSRF")
    void seccion_crearConPermiso_registraYRedirige() throws Exception {
        SeccionResponse response = SeccionResponse.builder()
                .id(15L)
                .nombre("C")
                .turno(Turno.MANANA)
                .capacidad(30)
                .activo(true)
                .build();
        when(seccionService.crearSeccion(any(SeccionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/academico/secciones")
                        .with(csrf())
                        .param("gradoId", "1")
                        .param("nombre", "C")
                        .param("turno", "MANANA")
                        .param("capacidad", "30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/secciones"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(seccionService).crearSeccion(any(SeccionRequest.class));
    }

    // ==========================================
    // 5. CSRF OBLIGATORIO
    // ==========================================
    @Test
    @WithMockUser(username = "admin_user", authorities = {"ACADEMICO_CREAR"})
    @DisplayName("18. POST sin CSRF es rechazado con 403 Forbidden")
    void academico_postSinCsrf_esRechazado() throws Exception {
        mockMvc.perform(post("/academico/anios")
                        .param("anio", "2027")
                        .param("nombre", "Año 2027")
                        .param("fechaInicio", "2027-03-01")
                        .param("fechaFin", "2027-12-20"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/academico/secciones")
                        .param("gradoId", "1")
                        .param("nombre", "C")
                        .param("turno", "MANANA")
                        .param("capacidad", "30"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 6. ACTIVAR / DESACTIVAR CON ACADEMICO_EDITAR
    // ==========================================
    @Test
    @WithMockUser(username = "admin_user", authorities = {"ACADEMICO_EDITAR"})
    @DisplayName("ACADEMICO_EDITAR permite activar/desactivar año escolar con POST y CSRF")
    void anios_desactivarYActivar_conPermiso() throws Exception {
        mockMvc.perform(post("/academico/anios/1/desactivar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/anios"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(anioEscolarService).desactivarAnioEscolar(1L);

        mockMvc.perform(post("/academico/anios/1/activar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/anios"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(anioEscolarService).activarAnioEscolar(1L);
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ACADEMICO_EDITAR"})
    @DisplayName("ACADEMICO_EDITAR permite activar/desactivar sección con POST y CSRF")
    void seccion_desactivarYActivar_conPermiso() throws Exception {
        mockMvc.perform(post("/academico/secciones/5/desactivar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/secciones"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(seccionService).desactivarSeccion(5L);

        mockMvc.perform(post("/academico/secciones/5/activar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/secciones"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(seccionService).activarSeccion(5L);
    }

    // ==========================================
    // 7. FILTROS Y PAGINACIÓN
    // ==========================================
    @Test
    @WithMockUser(username = "directora", authorities = {"ACADEMICO_VER"})
    @DisplayName("19 y 20. Filtros y paginación de secciones delegan al servicio")
    void seccion_filtrosYPaginacion_deleganAServicio() throws Exception {
        Page<SeccionResponse> pageMock = new PageImpl<>(Collections.emptyList());
        when(seccionService.buscarPaginado(eq(NivelEducativo.PRIMARIA), eq(1L), eq(Turno.MANANA), eq("ACTIVOS"), any(Pageable.class)))
                .thenReturn(pageMock);
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/academico/secciones")
                        .param("nivel", "PRIMARIA")
                        .param("gradoId", "1")
                        .param("turno", "MANANA")
                        .param("estado", "ACTIVOS")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("paginaActual", 0))
                .andExpect(model().attribute("nivelSeleccionado", NivelEducativo.PRIMARIA))
                .andExpect(model().attribute("gradoIdSeleccionado", 1L))
                .andExpect(model().attribute("turnoSeleccionado", Turno.MANANA))
                .andExpect(model().attribute("estado", "ACTIVOS"));

        verify(seccionService).buscarPaginado(eq(NivelEducativo.PRIMARIA), eq(1L), eq(Turno.MANANA), eq("ACTIVOS"), any(Pageable.class));
    }

    // ==========================================
    // 8. AUDITORÍA
    // ==========================================
    @Test
    @WithMockUser(username = "admin_academico", roles = {"ADMIN"})
    @DisplayName("21. Auditoría continúa funcionando alimentada por usuario autenticado")
    void auditoria_usuarioAutenticado_alimentaAuditor() {
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        assertTrue(currentAuditor.isPresent());
        assertEquals("admin_academico", currentAuditor.get());
    }
}
