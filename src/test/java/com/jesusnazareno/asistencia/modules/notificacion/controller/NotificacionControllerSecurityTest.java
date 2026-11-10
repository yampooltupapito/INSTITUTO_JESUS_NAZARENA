package com.jesusnazareno.asistencia.modules.notificacion.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;
import com.jesusnazareno.asistencia.modules.notificacion.dto.NotificacionResponse;
import com.jesusnazareno.asistencia.modules.notificacion.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
@DisplayName("Pruebas de Seguridad y Web - NotificacionController")
class NotificacionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioDocente;
    private NotificacionResponse notificacionResp;

    @BeforeEach
    void setUp() {
        Rol rolDocente = Rol.builder().id(1L).codigo("DOCENTE").nombre("Docente").build();

        usuarioDocente = Usuario.builder()
                .id(15L)
                .username("docente1")
                .rol(rolDocente)
                .activo(true)
                .build();

        notificacionResp = NotificacionResponse.builder()
                .id(100L)
                .tipo(TipoNotificacion.ALERTA_ASISTENCIA)
                .tipoDescripcion("Alerta de Asistencia")
                .titulo("Nueva Alerta")
                .mensaje("Estudiante con 3 inasistencias.")
                .referenciaTipo("ALERTA")
                .referenciaId(5L)
                .urlDestino("/alertas/5")
                .leida(false)
                .fechaLectura(null)
                .createdAt(LocalDateTime.now())
                .fechaCreacionFormateada("03/10/2026 10:00")
                .tiempoRelativo("Hace 5 minutos")
                .build();

        when(usuarioRepository.findByUsername("docente1")).thenReturn(Optional.of(usuarioDocente));
    }

    @Test
    @DisplayName("1. /notificaciones sin autenticación redirige al login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/notificaciones"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "docente1")
    @DisplayName("2. Usuario autenticado puede listar sus notificaciones")
    void listar_autenticado_retornaVistaListado() throws Exception {
        when(notificacionService.listarDelUsuario(eq(15L), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notificacionResp)));
        when(notificacionService.contarNoLeidas(15L)).thenReturn(1L);
        when(notificacionService.obtenerUltimasNoLeidas(15L, 5)).thenReturn(List.of(notificacionResp));

        mockMvc.perform(get("/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(view().name("notificaciones/list"))
                .andExpect(model().attributeExists("notificaciones"))
                .andExpect(model().attributeExists("totalNoLeidas"));
    }

    @Test
    @WithMockUser(username = "docente1")
    @DisplayName("3. Usuario autenticado puede consultar el detalle de su notificación")
    void detalle_propietario_retornaVistaDetalle() throws Exception {
        when(notificacionService.marcarComoLeida(100L, 15L)).thenReturn(notificacionResp);

        mockMvc.perform(get("/notificaciones/100"))
                .andExpect(status().isOk())
                .andExpect(view().name("notificaciones/detail"))
                .andExpect(model().attributeExists("notificacion"));
    }

    @Test
    @WithMockUser(username = "docente1")
    @DisplayName("4. Acceder a notificación de otro usuario redirige con mensaje de error")
    void detalle_noPropietario_redirigeConError() throws Exception {
        when(notificacionService.marcarComoLeida(999L, 15L))
                .thenThrow(new BusinessException("No tiene acceso a esta notificación."));

        mockMvc.perform(get("/notificaciones/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notificaciones"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "docente1")
    @DisplayName("5. POST /notificaciones/{id}/leer con CSRF marca como leída y redirige")
    void marcarLeida_conCsrf_redirige() throws Exception {
        mockMvc.perform(post("/notificaciones/100/leer").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notificaciones"));

        verify(notificacionService).marcarComoLeida(100L, 15L);
    }

    @Test
    @WithMockUser(username = "docente1")
    @DisplayName("6. POST /notificaciones/{id}/leer SIN CSRF devuelve 403 Forbidden")
    void marcarLeida_sinCsrf_devuelve403() throws Exception {
        mockMvc.perform(post("/notificaciones/100/leer"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "docente1")
    @DisplayName("7. POST /notificaciones/leer-todas con CSRF marca todas y redirige")
    void marcarTodasLeidas_conCsrf_redirige() throws Exception {
        when(notificacionService.marcarTodasComoLeidas(15L)).thenReturn(3);

        mockMvc.perform(post("/notificaciones/leer-todas").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notificaciones"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(notificacionService).marcarTodasComoLeidas(15L);
    }

    @Test
    @WithMockUser(username = "docente1")
    @DisplayName("8. POST /notificaciones/leer-todas SIN CSRF devuelve 403 Forbidden")
    void marcarTodasLeidas_sinCsrf_devuelve403() throws Exception {
        mockMvc.perform(post("/notificaciones/leer-todas"))
                .andExpect(status().isForbidden());
    }
}
