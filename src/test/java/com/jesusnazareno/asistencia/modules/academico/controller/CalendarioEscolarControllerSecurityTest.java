package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
@DisplayName("Pruebas de Seguridad y Web - CalendarioEscolarController")
class CalendarioEscolarControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarioEscolarService calendarioEscolarService;

    @MockBean
    private AnioEscolarService anioEscolarService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private CalendarioEscolarResponse calendarioSample;

    @BeforeEach
    void setUp() {
        calendarioSample = CalendarioEscolarResponse.builder()
                .id(1L)
                .anioEscolarId(1L)
                .anioEscolarAnio(2026)
                .anioEscolarNombre("Año 2026")
                .fecha(LocalDate.of(2026, 5, 1))
                .diaSemana("Viernes")
                .tipoDia(TipoDiaCalendario.FERIADO)
                .tipoDiaDescripcion("Feriado")
                .descripcion("Día del Trabajo")
                .esLectivo(false)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("16. /academico/calendario sin autenticación redirige al login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/academico/calendario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("17. Con permiso CALENDARIO_VER permite listar correctamente")
    @WithMockUser(username = "admin", authorities = {"CALENDARIO_VER"})
    void listar_conPermisoCalendarioVer_retornaVistaOk() throws Exception {
        when(calendarioEscolarService.listar(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(calendarioSample)));
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/academico/calendario"))
                .andExpect(status().isOk())
                .andExpect(view().name("academico/calendario/list"))
                .andExpect(model().attributeExists("fechas"))
                .andExpect(model().attributeExists("tiposDia"));
    }

    @Test
    @DisplayName("18. Usuario autenticado sin permiso CALENDARIO_VER recibe 403 Forbidden")
    @WithMockUser(username = "docente", authorities = {"ASISTENCIA_VER"})
    void listar_sinPermisoCalendarioVer_devuelve403() throws Exception {
        mockMvc.perform(get("/academico/calendario"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("19. POST sin CSRF es rechazado con 403 Forbidden")
    @WithMockUser(username = "admin", authorities = {"CALENDARIO_CREAR"})
    void crear_sinCsrf_esRechazado() throws Exception {
        mockMvc.perform(post("/academico/calendario")
                        .param("anioEscolarId", "1")
                        .param("fecha", "2026-05-01")
                        .param("tipoDia", "FERIADO")
                        .param("esLectivo", "false"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Crear fecha con permiso CALENDARIO_CREAR y token CSRF redirige exitosamente")
    @WithMockUser(username = "admin", authorities = {"CALENDARIO_CREAR"})
    void crear_conPermisoYCsrf_redirigeExitosamente() throws Exception {
        mockMvc.perform(post("/academico/calendario")
                        .with(csrf())
                        .param("anioEscolarId", "1")
                        .param("fecha", "2026-05-01")
                        .param("tipoDia", "FERIADO")
                        .param("descripcion", "Día del Trabajo")
                        .param("esLectivo", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/calendario"));

        verify(calendarioEscolarService).crear(any());
    }

    @Test
    @DisplayName("Desactivar fecha con permiso CALENDARIO_EDITAR y token CSRF")
    @WithMockUser(username = "admin", authorities = {"CALENDARIO_EDITAR"})
    void desactivar_conPermisoYCsrf_desactivaYRedirige() throws Exception {
        mockMvc.perform(post("/academico/calendario/1/desactivar")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/calendario"));

        verify(calendarioEscolarService).desactivar(1L);
    }
}
