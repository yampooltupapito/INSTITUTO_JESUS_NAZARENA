package com.jesusnazareno.asistencia.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SistemaSeguridadEstabilizacionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Rutas protegidas redirigen al login para usuarios anónimos")
    void rutasProtegidasRedirigenAlLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/estudiantes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/auditoria"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Endpoint /ping es público e inocuo")
    void pingEsPublicoEInocuo() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("SGE OK"));
    }

    @Test
    @DisplayName("CSRF es estrictamente requerido en peticiones POST")
    @WithMockUser(username = "admin", authorities = {"USUARIO_CREAR"})
    void csrfRequeridoEnPost() throws Exception {
        // Petición POST sin token CSRF debe ser rechazada con 403 Forbidden
        mockMvc.perform(post("/usuarios/nuevo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Endpoints sensibles de Actuator están deshabilitados o bloqueados")
    void actuatorEndpointsSensiblesBloqueados() throws Exception {
        // /actuator/env no debe estar expuesto ni accesible públicamente
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().is3xxRedirection()); // redirige a login al no ser público
    }

    @Test
    @DisplayName("Auditoría es estrictamente READ ONLY: peticiones modificadoras POST o DELETE devuelven 405 Method Not Allowed")
    @WithMockUser(username = "admin", authorities = {"AUDITORIA_VER"})
    void auditoriaEsEstrictamenteReadOnly() throws Exception {
        mockMvc.perform(post("/auditoria").with(csrf()))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(delete("/auditoria/1").with(csrf()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Usuario sin permiso específico recibe 403 Forbidden en operaciones administrativas")
    @WithMockUser(username = "docente", authorities = {"DASHBOARD_VER"})
    void usuarioSinPermisoRecibe403() throws Exception {
        mockMvc.perform(get("/auditoria"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }
}
