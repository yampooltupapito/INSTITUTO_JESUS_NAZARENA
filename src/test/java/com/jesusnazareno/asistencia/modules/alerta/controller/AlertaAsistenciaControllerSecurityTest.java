package com.jesusnazareno.asistencia.modules.alerta.controller;

import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.dto.AlertaAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ResolucionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.RevisionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.service.AlertaAsistenciaService;
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

import java.time.LocalDate;
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
@DisplayName("Pruebas de Seguridad y Web - AlertaAsistenciaController")
class AlertaAsistenciaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertaAsistenciaService alertaService;

    @MockBean
    private AnioEscolarService anioEscolarService;

    @MockBean
    private GradoService gradoService;

    @MockBean
    private SeccionService seccionService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private AlertaAsistenciaResponse alertaPendiente;
    private Usuario usuarioDirector;

    @BeforeEach
    void setUp() {
        alertaPendiente = AlertaAsistenciaResponse.builder()
                .id(1L)
                .matriculaId(10L)
                .estudianteId(20L)
                .estudianteNombreCompleto("QUISPE MAMANI, JUAN")
                .estudianteDocumento("70809010")
                .gradoNombre("1° Grado")
                .seccionNombre("A")
                .anioEscolarNombre("2026")
                .tipo(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS)
                .tipoDescripcion("Ausencias Consecutivas")
                .cantidad(3)
                .fechaDeteccion(LocalDate.of(2026, 4, 10))
                .fechaInicioPeriodo(LocalDate.of(2026, 3, 1))
                .fechaFinPeriodo(LocalDate.of(2026, 4, 10))
                .estado(EstadoAlerta.PENDIENTE)
                .descripcion("Estudiante presenta 3 ausencias consecutivas en días lectivos.")
                .activo(true)
                .build();

        usuarioDirector = Usuario.builder()
                .id(99L)
                .username("director")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("1. /alertas sin autenticación redirige al login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/alertas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"ALERTA_VER"})
    @DisplayName("2. ALERTA_VER permite listar alertas")
    void listar_conPermiso_retornaVistaListado() throws Exception {
        when(alertaService.buscarAlertas(any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(alertaPendiente)));
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/alertas"))
                .andExpect(status().isOk())
                .andExpect(view().name("alertas/list"))
                .andExpect(model().attributeExists("alertas"))
                .andExpect(model().attributeExists("tiposAlerta"))
                .andExpect(model().attributeExists("estadosAlerta"));
    }

    @Test
    @WithMockUser(username = "usuario_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("3. Sin ALERTA_VER devuelve 403 Forbidden")
    void listar_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/alertas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"ALERTA_VER"})
    @DisplayName("4. ALERTA_VER permite ver detalle de alerta")
    void detalle_conPermiso_retornaVistaDetalle() throws Exception {
        when(alertaService.buscarPorId(1L)).thenReturn(alertaPendiente);

        mockMvc.perform(get("/alertas/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("alertas/detail"))
                .andExpect(model().attributeExists("alerta"));
    }

    @Test
    @WithMockUser(username = "usuario_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("5. Sin ALERTA_VER, ver detalle devuelve 403")
    void detalle_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/alertas/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"ALERTA_REVISAR"})
    @DisplayName("6. ALERTA_REVISAR permite marcar en revisión con CSRF y obtiene revisor de Security")
    void marcarEnRevision_conPermisoYCsrf_redirigeADetalle() throws Exception {
        when(usuarioRepository.findByUsername("director")).thenReturn(Optional.of(usuarioDirector));

        mockMvc.perform(post("/alertas/1/revisar")
                        .with(csrf())
                        .param("observacion", "Revisando caso"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alertas/1"))
                .andExpect(flash().attributeExists("successMessage"));

        // Se verifica que el usuario autenticado (ID 99) fue enviado al servicio
        verify(alertaService).marcarEnRevision(eq(1L), any(RevisionAlertaRequest.class), eq(99L));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"ALERTA_VER"})
    @DisplayName("7. Sin ALERTA_REVISAR, marcar en revisión devuelve 403")
    void marcarEnRevision_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(post("/alertas/1/revisar")
                        .with(csrf())
                        .param("observacion", "Intento sin permiso"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"ALERTA_REVISAR"})
    @DisplayName("8. POST /alertas/{id}/revisar sin CSRF devuelve 403 Forbidden")
    void marcarEnRevision_sinCsrf_devuelve403() throws Exception {
        mockMvc.perform(post("/alertas/1/revisar")
                        .param("observacion", "Sin token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"ALERTA_REVISAR"})
    @DisplayName("9. ALERTA_REVISAR permite resolver alerta con CSRF")
    void resolver_conPermisoYCsrf_redirigeADetalle() throws Exception {
        when(usuarioRepository.findByUsername("director")).thenReturn(Optional.of(usuarioDirector));

        mockMvc.perform(post("/alertas/1/resolver")
                        .with(csrf())
                        .param("observacion", "Compromiso de asistencia firmado"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alertas/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(alertaService).resolver(eq(1L), any(ResolucionAlertaRequest.class), eq(99L));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"ALERTA_REVISAR"})
    @DisplayName("10. POST /alertas/{id}/resolver sin CSRF devuelve 403 Forbidden")
    void resolver_sinCsrf_devuelve403() throws Exception {
        mockMvc.perform(post("/alertas/1/resolver")
                        .param("observacion", "Sin token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ALERTA_CONFIGURAR"})
    @DisplayName("11. ALERTA_CONFIGURAR permite ver configuración")
    void verConfiguracion_conPermiso_retornaVistaConfiguracion() throws Exception {
        when(alertaService.listarConfiguraciones()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/alertas/configuracion"))
                .andExpect(status().isOk())
                .andExpect(view().name("alertas/configuracion"))
                .andExpect(model().attributeExists("configuraciones"))
                .andExpect(model().attributeExists("tiposAlerta"))
                .andExpect(model().attributeExists("configuracionRequest"));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"ALERTA_VER", "ALERTA_REVISAR"})
    @DisplayName("12. Sin ALERTA_CONFIGURAR, ver configuración devuelve 403")
    void verConfiguracion_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/alertas/configuracion"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ALERTA_CONFIGURAR"})
    @DisplayName("13. ALERTA_CONFIGURAR permite actualizar umbral con CSRF")
    void actualizarConfiguracion_conPermisoYCsrf_actualizaExitosamente() throws Exception {
        mockMvc.perform(post("/alertas/configuracion")
                        .with(csrf())
                        .param("tipo", "AUSENCIAS_CONSECUTIVAS")
                        .param("umbral", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alertas/configuracion"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(alertaService).actualizarConfiguracion(any(ConfiguracionAlertaRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ALERTA_CONFIGURAR"})
    @DisplayName("14. POST /alertas/configuracion sin CSRF devuelve 403 Forbidden")
    void actualizarConfiguracion_sinCsrf_devuelve403() throws Exception {
        mockMvc.perform(post("/alertas/configuracion")
                        .param("tipo", "AUSENCIAS_CONSECUTIVAS")
                        .param("umbral", "4"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"ALERTA_VER"})
    @DisplayName("15. Filtros y paginación se delegan a la capa de persistencia")
    void listar_conFiltros_pasaParametrosAlService() throws Exception {
        when(alertaService.buscarAlertas(eq("QUISPE"), eq(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS),
                eq(EstadoAlerta.PENDIENTE), eq(1L), eq(2L), eq(3L),
                eq(LocalDate.of(2026, 4, 1)), eq(LocalDate.of(2026, 4, 30)), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(alertaPendiente)));

        mockMvc.perform(get("/alertas")
                        .param("q", "QUISPE")
                        .param("tipo", "AUSENCIAS_CONSECUTIVAS")
                        .param("estado", "PENDIENTE")
                        .param("anioEscolarId", "1")
                        .param("gradoId", "2")
                        .param("seccionId", "3")
                        .param("fechaInicio", "2026-04-01")
                        .param("fechaFin", "2026-04-30"))
                .andExpect(status().isOk())
                .andExpect(view().name("alertas/list"))
                .andExpect(model().attributeExists("alertas"));

        verify(alertaService).buscarAlertas(eq("QUISPE"), eq(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS),
                eq(EstadoAlerta.PENDIENTE), eq(1L), eq(2L), eq(3L),
                eq(LocalDate.of(2026, 4, 1)), eq(LocalDate.of(2026, 4, 30)), any(Pageable.class));
    }
}
