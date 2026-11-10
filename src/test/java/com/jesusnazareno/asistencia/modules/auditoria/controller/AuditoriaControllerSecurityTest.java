package com.jesusnazareno.asistencia.modules.auditoria.controller;

import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaEventoDTO;
import com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Seguridad y Control de Acceso - Prompt 28: Auditoría Funcional")
class AuditoriaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditoriaService auditoriaService;

    @Test
    @DisplayName("GET /auditoria sin autenticación redirige al login")
    void listarSinAutenticacionRedirigeLogin() throws Exception {
        mockMvc.perform(get("/auditoria"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("GET /auditoria sin permiso AUDITORIA_VER devuelve 403 Forbidden")
    @WithMockUser(username = "docente1", authorities = {"ASISTENCIA_REGISTRAR"})
    void listarSinPermisoDevuelve403() throws Exception {
        mockMvc.perform(get("/auditoria"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /auditoria con permiso AUDITORIA_VER devuelve 200 OK y vista auditoria/list")
    @WithMockUser(username = "admin", authorities = {"AUDITORIA_VER"})
    void listarConPermisoDevuelve200() throws Exception {
        when(auditoriaService.buscarEventos(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/auditoria"))
                .andExpect(status().isOk())
                .andExpect(view().name("auditoria/list"))
                .andExpect(model().attributeExists("eventos"))
                .andExpect(model().attributeExists("acciones"))
                .andExpect(model().attributeExists("modulos"))
                .andExpect(model().attributeExists("resultados"));
    }

    @Test
    @DisplayName("GET /auditoria/{id} con permiso AUDITORIA_VER devuelve 200 OK y vista auditoria/detail")
    @WithMockUser(username = "admin", authorities = {"AUDITORIA_VER"})
    void detalleConPermisoDevuelve200() throws Exception {
        AuditoriaEventoDTO eventoDTO = AuditoriaEventoDTO.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .username("admin")
                .accion(AccionAuditoria.LOGIN_EXITOSO)
                .modulo(ModuloAuditoria.SEGURIDAD)
                .resultado(ResultadoAuditoria.EXITO)
                .descripcion("Inicio de sesión exitoso")
                .direccionIp("127.0.0.1")
                .build();

        when(auditoriaService.buscarPorId(1L)).thenReturn(eventoDTO);

        mockMvc.perform(get("/auditoria/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("auditoria/detail"))
                .andExpect(model().attributeExists("evento"));
    }

    @Test
    @DisplayName("POST /auditoria no existe y no es permitido (Read-only)")
    @WithMockUser(username = "admin", authorities = {"AUDITORIA_VER"})
    void noExistePostParaAuditoria() throws Exception {
        mockMvc.perform(post("/auditoria").with(csrf()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("DELETE /auditoria/{id} no existe y no es permitido (Read-only)")
    @WithMockUser(username = "admin", authorities = {"AUDITORIA_VER"})
    void noExisteDeleteParaAuditoria() throws Exception {
        mockMvc.perform(delete("/auditoria/1").with(csrf()))
                .andExpect(status().isMethodNotAllowed());
    }
}
