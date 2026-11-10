package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
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
import java.time.LocalTime;
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
@DisplayName("Pruebas de Seguridad y Web - HorarioAsistenciaController")
class HorarioAsistenciaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HorarioAsistenciaService horarioAsistenciaService;

    @MockBean
    private AnioEscolarService anioEscolarService;

    @MockBean
    private GradoService gradoService;

    @MockBean
    private SeccionService seccionService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private HorarioAsistenciaResponse horarioSample;

    @BeforeEach
    void setUp() {
        horarioSample = HorarioAsistenciaResponse.builder()
                .id(1L)
                .anioEscolarId(1L)
                .anio(2026)
                .anioNombre("2026")
                .turno(Turno.MANANA)
                .horaEntrada(LocalTime.of(7, 45))
                .minutosTolerancia(10)
                .horaLimite(LocalTime.of(7, 55))
                .horaSalida(LocalTime.of(13, 15))
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .alcance("General")
                .build();
    }

    @Test
    @DisplayName("16. /academico/horarios sin autenticación redirige al login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/academico/horarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("17. Con permiso HORARIO_VER permite listar correctamente")
    @WithMockUser(username = "admin", authorities = {"HORARIO_VER"})
    void listar_conPermisoHorarioVer_retornaVistaOk() throws Exception {
        when(horarioAsistenciaService.listarPaginado(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(horarioSample)));
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/academico/horarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("academico/horarios/list"))
                .andExpect(model().attributeExists("horarios"));
    }

    @Test
    @DisplayName("18. Sin permiso devuelve 403 Forbidden al intentar listar")
    @WithMockUser(username = "docente", authorities = {"OTRO_PERMISO"})
    void listar_sinPermisoHorarioVer_devuelve403() throws Exception {
        mockMvc.perform(get("/academico/horarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("19. POST sin CSRF es rechazado con 403 Forbidden")
    @WithMockUser(username = "admin", authorities = {"HORARIO_CREAR"})
    void crear_sinCsrf_esRechazadoCon403() throws Exception {
        mockMvc.perform(post("/academico/horarios")
                        .param("anioEscolarId", "1")
                        .param("turno", "MANANA")
                        .param("horaEntrada", "07:45")
                        .param("minutosTolerancia", "10")
                        .param("horaSalida", "13:00")
                        .param("fechaInicio", "2026-03-01")
                        .param("fechaFin", "2026-12-20"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Con permiso HORARIO_CREAR y CSRF permite registrar nuevo horario")
    @WithMockUser(username = "admin", authorities = {"HORARIO_CREAR"})
    void crear_conPermisoYCsrf_redirigeExitosamente() throws Exception {
        when(horarioAsistenciaService.crearHorario(any(HorarioAsistenciaRequest.class)))
                .thenReturn(horarioSample);

        mockMvc.perform(post("/academico/horarios")
                        .with(csrf())
                        .param("anioEscolarId", "1")
                        .param("turno", "MANANA")
                        .param("horaEntrada", "07:45")
                        .param("minutosTolerancia", "10")
                        .param("horaSalida", "13:00")
                        .param("fechaInicio", "2026-03-01")
                        .param("fechaFin", "2026-12-20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/horarios"));

        verify(horarioAsistenciaService).crearHorario(any(HorarioAsistenciaRequest.class));
    }

    @Test
    @DisplayName("Con permiso HORARIO_EDITAR y CSRF permite activar y desactivar")
    @WithMockUser(username = "admin", authorities = {"HORARIO_EDITAR"})
    void desactivarYActivar_conPermisoYCsrf_redirigeExitosamente() throws Exception {
        mockMvc.perform(post("/academico/horarios/1/desactivar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/horarios"));
        verify(horarioAsistenciaService).desactivarHorario(1L);

        when(horarioAsistenciaService.activarHorario(1L)).thenReturn(horarioSample);
        mockMvc.perform(post("/academico/horarios/1/activar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academico/horarios"));
        verify(horarioAsistenciaService).activarHorario(1L);
    }
}
