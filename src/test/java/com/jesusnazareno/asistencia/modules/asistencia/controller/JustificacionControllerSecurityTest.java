package com.jesusnazareno.asistencia.modules.asistencia.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.RevisionJustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.service.AsistenciaService;
import com.jesusnazareno.asistencia.modules.asistencia.service.JustificacionService;
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
import static org.mockito.Mockito.atLeastOnce;
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
@DisplayName("Pruebas de Seguridad y Web - JustificacionController")
class JustificacionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JustificacionService justificacionService;

    @MockBean
    private AsistenciaService asistenciaService;

    @MockBean
    private AnioEscolarService anioEscolarService;

    @MockBean
    private GradoService gradoService;

    @MockBean
    private SeccionService seccionService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private AsistenciaResponse asistenciaAusente;
    private AsistenciaResponse asistenciaTardanza;
    private AsistenciaResponse asistenciaPresente;
    private AsistenciaResponse asistenciaJustificada;
    private JustificacionResponse justificacionPendiente;
    private Usuario usuarioRevisor;

    @BeforeEach
    void setUp() {
        asistenciaAusente = AsistenciaResponse.builder()
                .id(100L)
                .matriculaId(1L)
                .estudianteId(10L)
                .estudianteNombreCompleto("QUISPE MAMANI, JUAN")
                .estudianteNumeroDocumento("70809010")
                .gradoNombre("1° Grado")
                .seccionNombre("A")
                .fecha(LocalDate.of(2026, 4, 10))
                .estado(EstadoAsistencia.AUSENTE)
                .build();

        asistenciaTardanza = AsistenciaResponse.builder()
                .id(101L)
                .matriculaId(2L)
                .estudianteId(11L)
                .estudianteNombreCompleto("FLORES ROJAS, MARIA")
                .estudianteNumeroDocumento("70809011")
                .gradoNombre("1° Grado")
                .seccionNombre("B")
                .fecha(LocalDate.of(2026, 4, 10))
                .estado(EstadoAsistencia.TARDANZA)
                .build();

        asistenciaPresente = AsistenciaResponse.builder()
                .id(102L)
                .matriculaId(3L)
                .estudianteId(12L)
                .estudianteNombreCompleto("RODRIGUEZ DIAZ, CARLOS")
                .estudianteNumeroDocumento("70809012")
                .gradoNombre("1° Grado")
                .seccionNombre("A")
                .fecha(LocalDate.of(2026, 4, 10))
                .estado(EstadoAsistencia.PRESENTE)
                .build();

        asistenciaJustificada = AsistenciaResponse.builder()
                .id(103L)
                .matriculaId(4L)
                .estudianteId(13L)
                .estudianteNombreCompleto("LOPEZ VEGA, ANA")
                .estudianteNumeroDocumento("70809013")
                .gradoNombre("1° Grado")
                .seccionNombre("A")
                .fecha(LocalDate.of(2026, 4, 10))
                .estado(EstadoAsistencia.JUSTIFICADO)
                .build();

        justificacionPendiente = JustificacionResponse.builder()
                .id(500L)
                .asistenciaId(100L)
                .estudianteId(10L)
                .estudianteNombreCompleto("QUISPE MAMANI, JUAN")
                .estudianteNumeroDocumento("70809010")
                .asistenciaFecha(LocalDate.of(2026, 4, 10))
                .asistenciaEstado(EstadoAsistencia.AUSENTE)
                .motivo("Cita Médica")
                .descripcion("Atención en posta médica")
                .fechaSolicitud(LocalDate.of(2026, 4, 10))
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();

        usuarioRevisor = Usuario.builder()
                .id(1L)
                .username("director")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("1. /justificaciones sin autenticación redirige al login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/justificaciones"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "docente", authorities = {"JUSTIFICACION_VER"})
    @DisplayName("2. JUSTIFICACION_VER permite listar justificaciones")
    void listar_conPermiso_retornaVistaListado() throws Exception {
        when(justificacionService.buscarJustificaciones(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(justificacionPendiente)));
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/justificaciones"))
                .andExpect(status().isOk())
                .andExpect(view().name("justificaciones/list"))
                .andExpect(model().attributeExists("justificaciones"))
                .andExpect(model().attributeExists("estados"));
    }

    @Test
    @WithMockUser(username = "estudiante", authorities = {"OTRO_PERMISO"})
    @DisplayName("3. Sin JUSTIFICACION_VER devuelve 403 Forbidden")
    void listar_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/justificaciones"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"JUSTIFICACION_CREAR"})
    @DisplayName("4. JUSTIFICACION_CREAR permite abrir formulario nueva justificación")
    void nueva_conPermisoYAsistenciaAusente_retornaVistaFormulario() throws Exception {
        when(asistenciaService.buscarPorId(100L)).thenReturn(asistenciaAusente);
        when(justificacionService.buscarActivaPorAsistencia(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/justificaciones/nueva").param("asistenciaId", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("justificaciones/form"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("asistencia"));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"JUSTIFICACION_CREAR"})
    @DisplayName("5. PRESENTE no puede justificarse y redirige con error")
    void nueva_conAsistenciaPresente_rechazaYRedirige() throws Exception {
        when(asistenciaService.buscarPorId(102L)).thenReturn(asistenciaPresente);

        mockMvc.perform(get("/justificaciones/nueva").param("asistenciaId", "102"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/asistencias/102"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"JUSTIFICACION_CREAR"})
    @DisplayName("6. AUSENTE puede justificarse")
    void nueva_conAsistenciaAusente_permiteAcceso() throws Exception {
        when(asistenciaService.buscarPorId(100L)).thenReturn(asistenciaAusente);
        when(justificacionService.buscarActivaPorAsistencia(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/justificaciones/nueva").param("asistenciaId", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("justificaciones/form"));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"JUSTIFICACION_CREAR"})
    @DisplayName("7. TARDANZA puede justificarse")
    void nueva_conAsistenciaTardanza_permiteAcceso() throws Exception {
        when(asistenciaService.buscarPorId(101L)).thenReturn(asistenciaTardanza);
        when(justificacionService.buscarActivaPorAsistencia(101L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/justificaciones/nueva").param("asistenciaId", "101"))
                .andExpect(status().isOk())
                .andExpect(view().name("justificaciones/form"));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"JUSTIFICACION_CREAR"})
    @DisplayName("8. JUSTIFICADO no admite nueva justificación y redirige con error")
    void nueva_conAsistenciaJustificada_rechazaYRedirige() throws Exception {
        when(asistenciaService.buscarPorId(103L)).thenReturn(asistenciaJustificada);

        mockMvc.perform(get("/justificaciones/nueva").param("asistenciaId", "103"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/asistencias/103"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"JUSTIFICACION_CREAR"})
    @DisplayName("9. Estado inicial siempre PENDIENTE y guardado exitoso redirige con PRG")
    void crear_conDatosValidos_guardaEnPendienteYRedirigeADetalle() throws Exception {
        when(justificacionService.crear(any(JustificacionRequest.class)))
                .thenReturn(justificacionPendiente);

        mockMvc.perform(post("/justificaciones")
                        .with(csrf())
                        .param("asistenciaId", "100")
                        .param("motivo", "Cita Médica")
                        .param("descripcion", "Atención odontológica"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/justificaciones/500"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(justificacionService).crear(any(JustificacionRequest.class));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"JUSTIFICACION_CREAR"})
    @DisplayName("10. No permite justificación duplicada para asistencia con justificación activa")
    void nueva_conJustificacionDuplicada_rechazaYRedirige() throws Exception {
        when(asistenciaService.buscarPorId(100L)).thenReturn(asistenciaAusente);
        when(justificacionService.buscarActivaPorAsistencia(100L))
                .thenReturn(Optional.of(justificacionPendiente));

        mockMvc.perform(get("/justificaciones/nueva").param("asistenciaId", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/asistencias/100"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"JUSTIFICACION_REVISAR"})
    @DisplayName("11. JUSTIFICACION_REVISAR permite abrir pantalla de revisión de justificación PENDIENTE")
    void revisar_conPermisoYEstadoPendiente_retornaVistaReview() throws Exception {
        when(justificacionService.buscarPorId(500L)).thenReturn(justificacionPendiente);
        when(asistenciaService.buscarPorId(100L)).thenReturn(asistenciaAusente);

        mockMvc.perform(get("/justificaciones/500/revisar"))
                .andExpect(status().isOk())
                .andExpect(view().name("justificaciones/review"))
                .andExpect(model().attributeExists("justificacion"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"JUSTIFICACION_REVISAR"})
    @DisplayName("12 y 13. Aprobar llama al servicio para actualizar Justificación a APROBADA y Asistencia a JUSTIFICADO")
    void aprobar_conPermiso_ejecutaAprobacionYRedirige() throws Exception {
        when(usuarioRepository.findByUsername("director")).thenReturn(Optional.of(usuarioRevisor));

        JustificacionResponse aprobada = JustificacionResponse.builder()
                .id(500L)
                .asistenciaId(100L)
                .estado(EstadoJustificacion.APROBADA)
                .asistenciaEstado(EstadoAsistencia.JUSTIFICADO)
                .build();
        when(justificacionService.aprobar(eq(500L), any(RevisionJustificacionRequest.class)))
                .thenReturn(aprobada);

        mockMvc.perform(post("/justificaciones/500/aprobar")
                        .with(csrf())
                        .param("observacionRevision", "Certificado médico verificado"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/justificaciones/500"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(justificacionService).aprobar(eq(500L), any(RevisionJustificacionRequest.class));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"JUSTIFICACION_REVISAR"})
    @DisplayName("15 y 16. Rechazar llama al servicio para cambiar a RECHAZADA conservando AUSENTE/TARDANZA")
    void rechazar_conPermiso_ejecutaRechazoYRedirige() throws Exception {
        when(usuarioRepository.findByUsername("director")).thenReturn(Optional.of(usuarioRevisor));

        JustificacionResponse rechazada = JustificacionResponse.builder()
                .id(500L)
                .asistenciaId(100L)
                .estado(EstadoJustificacion.RECHAZADA)
                .asistenciaEstado(EstadoAsistencia.AUSENTE)
                .build();
        when(justificacionService.rechazar(eq(500L), any(RevisionJustificacionRequest.class)))
                .thenReturn(rechazada);

        mockMvc.perform(post("/justificaciones/500/rechazar")
                        .with(csrf())
                        .param("observacionRevision", "Documento no válido"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/justificaciones/500"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(justificacionService).rechazar(eq(500L), any(RevisionJustificacionRequest.class));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"JUSTIFICACION_REVISAR"})
    @DisplayName("17. Transiciones inválidas son rechazadas (revisar una ya aprobada redirige con error)")
    void revisar_justificacionYaAprobada_rechazaAcceso() throws Exception {
        JustificacionResponse yaAprobada = JustificacionResponse.builder()
                .id(501L)
                .asistenciaId(100L)
                .estado(EstadoJustificacion.APROBADA)
                .build();
        when(justificacionService.buscarPorId(501L)).thenReturn(yaAprobada);

        mockMvc.perform(get("/justificaciones/501/revisar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/justificaciones/501"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"JUSTIFICACION_REVISAR"})
    @DisplayName("18. revisadoPor procede del usuario autenticado (SecurityContext)")
    void aprobar_revisadoPorProcedeDeAutenticacion() throws Exception {
        when(usuarioRepository.findByUsername("director")).thenReturn(Optional.of(usuarioRevisor));

        mockMvc.perform(post("/justificaciones/500/aprobar")
                        .with(csrf())
                        .param("observacionRevision", "OK"))
                .andExpect(status().is3xxRedirection());

        verify(usuarioRepository, atLeastOnce()).findByUsername("director");
    }

    @Test
    @WithMockUser(username = "director", authorities = {"JUSTIFICACION_REVISAR"})
    @DisplayName("20. POST sin CSRF es rechazado con 403 Forbidden")
    void aprobar_sinCsrf_esRechazadoCon403() throws Exception {
        mockMvc.perform(post("/justificaciones/500/aprobar")
                        .param("observacionRevision", "Intento sin CSRF"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"JUSTIFICACION_VER"})
    @DisplayName("21 y 22. Filtros y paginación funcionan correctamente")
    void listar_conFiltrosYPaginacion_llamaServicioConParametros() throws Exception {
        when(justificacionService.buscarJustificaciones(
                eq("QUISPE"), eq(EstadoJustificacion.PENDIENTE),
                eq(LocalDate.of(2026, 4, 10)), eq(LocalDate.of(2026, 4, 10)),
                eq(1L), eq(2L), eq(3L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(justificacionPendiente)));
        when(anioEscolarService.listarTodos()).thenReturn(Collections.emptyList());
        when(gradoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/justificaciones")
                        .param("q", "QUISPE")
                        .param("estado", "PENDIENTE")
                        .param("fechaSolicitud", "2026-04-10")
                        .param("fechaAsistencia", "2026-04-10")
                        .param("anioEscolarId", "1")
                        .param("gradoId", "2")
                        .param("seccionId", "3")
                        .param("page", "0")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(view().name("justificaciones/list"))
                .andExpect(model().attribute("q", "QUISPE"))
                .andExpect(model().attribute("estado", EstadoJustificacion.PENDIENTE))
                .andExpect(model().attribute("size", 15));
    }
}
