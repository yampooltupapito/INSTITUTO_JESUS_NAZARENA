package com.jesusnazareno.asistencia.modules.dashboard;

import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumen;
import com.jesusnazareno.asistencia.modules.dashboard.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Seguridad y Controlador - Dashboard")
class DashboardControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    @DisplayName("1. /dashboard sin autenticación redirige a /login")
    void dashboard_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "docente_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("2. /dashboard con usuario autenticado sin permiso DASHBOARD_VER recibe 403 Forbidden")
    void dashboard_sinPermisoDashboardVer_retorna403() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"DASHBOARD_VER"})
    @DisplayName("3. /dashboard con permiso DASHBOARD_VER retorna 200 OK y vista dashboard/index")
    void dashboard_conPermisoDashboardVer_retorna200YVista() throws Exception {
        DashboardResumen resumen = DashboardResumen.builder()
                .totalEstudiantes(150L)
                .totalMatriculas(148L)
                .presentesHoy(140L)
                .tardanzasHoy(8L)
                .ausentesHoy(2L)
                .justificacionesPendientes(1L)
                .fecha(LocalDate.now())
                .fechaFormateada("Viernes, 02 de Octubre de 2026")
                .build();

        when(dashboardService.obtenerResumen()).thenReturn(resumen);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("resumen"))
                .andExpect(model().attribute("resumen", resumen));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"DASHBOARD_VER"})
    @DisplayName("4. /inicio redirige a /dashboard preservando compatibilidad")
    void inicio_redirigeADashboard() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser(username = "admin_user")
    @DisplayName("5. Logout vía POST con CSRF invalida sesión y redirige a /login?logout")
    void logout_conCsrf_invalidaYRedirige() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}
