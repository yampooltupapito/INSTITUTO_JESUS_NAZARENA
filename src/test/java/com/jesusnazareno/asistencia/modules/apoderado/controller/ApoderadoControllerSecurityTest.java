package com.jesusnazareno.asistencia.modules.apoderado.controller;

import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoCreateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoUpdateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.service.EstudianteApoderadoService;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
@DisplayName("Pruebas de Seguridad y Web - Prompt 20: ApoderadoController")
class ApoderadoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstudianteApoderadoService estudianteApoderadoService;

    @MockBean
    private EstudianteService estudianteService;

    @MockBean
    private PersonaActorRepository personaActorRepository;

    private ApoderadoResponse crearApoderadoMock(Long id, String dni, String nombres) {
        return new ApoderadoResponse(
                id,
                "DNI",
                dni,
                nombres,
                "Flores",
                "Ramos",
                LocalDate.of(1985, 3, 15),
                "FEMENINO",
                "987654321",
                "madre@correo.com",
                "Av. Los Incas 123",
                true,
                false,
                Collections.emptyList(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("01. /apoderados sin autenticación redirige a /login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/apoderados"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "auxiliar", authorities = {"APODERADO_VER"})
    @DisplayName("02. /apoderados con APODERADO_VER permite listar con filtros y paginación")
    void listar_conPermiso_retornaVistaListado() throws Exception {
        ApoderadoResponse ap = crearApoderadoMock(1L, "12345678", "María Elena");
        Page<ApoderadoResponse> page = new PageImpl<>(List.of(ap));

        when(estudianteApoderadoService.buscarApoderados(eq("Flores"), eq(true), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/apoderados")
                        .param("q", "Flores")
                        .param("estado", "ACTIVOS")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("apoderados/list"))
                .andExpect(model().attributeExists("apoderados", "q", "estado", "page", "size"));
    }

    @Test
    @WithMockUser(username = "estudiante", authorities = {"ESTUDIANTE_VER"})
    @DisplayName("03. /apoderados sin autoridad APODERADO_VER devuelve 403 Forbidden")
    void listar_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/apoderados"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"APODERADO_CREAR"})
    @DisplayName("04. GET /apoderados/nuevo con APODERADO_CREAR retorna vista de formulario")
    void nuevo_conPermiso_retornaFormulario() throws Exception {
        mockMvc.perform(get("/apoderados/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("apoderados/form"))
                .andExpect(model().attributeExists("apoderado", "tiposDocumento", "sexos", "parentescos"));
    }

    @Test
    @WithMockUser(username = "docente", authorities = {"DOCENTE_VER"})
    @DisplayName("05. GET /apoderados/nuevo sin APODERADO_CREAR devuelve 403 Forbidden")
    void nuevo_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/apoderados/nuevo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"APODERADO_CREAR"})
    @DisplayName("06. POST /apoderados sin CSRF token es rechazado con 403 Forbidden")
    void crear_sinCsrf_devuelve403() throws Exception {
        mockMvc.perform(post("/apoderados")
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "12345678")
                        .param("nombres", "María")
                        .param("apellidoPaterno", "Flores")
                        .param("apellidoMaterno", "Ramos")
                        .param("fechaNacimiento", "1985-05-15")
                        .param("sexo", "FEMENINO"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"APODERADO_CREAR"})
    @DisplayName("07. POST /apoderados con APODERADO_CREAR y CSRF registra y redirige")
    void crear_conPermisoYCsrf_registraYRedirige() throws Exception {
        ApoderadoResponse creado = crearApoderadoMock(5L, "87654321", "Rosa");
        when(estudianteApoderadoService.registrarApoderado(any(ApoderadoCreateRequest.class)))
                .thenReturn(creado);

        mockMvc.perform(post("/apoderados").with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "87654321")
                        .param("nombres", "Rosa")
                        .param("apellidoPaterno", "Flores")
                        .param("apellidoMaterno", "Ramos")
                        .param("fechaNacimiento", "1985-05-15")
                        .param("sexo", "FEMENINO")
                        .param("telefono", "987654321"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apoderados/5"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(estudianteApoderadoService).registrarApoderado(any(ApoderadoCreateRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"APODERADO_EDITAR"})
    @DisplayName("08. POST /apoderados/{id} con APODERADO_EDITAR y CSRF actualiza y redirige")
    void actualizar_conPermisoYCsrf_actualizaYRedirige() throws Exception {
        mockMvc.perform(post("/apoderados/5").with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "87654321")
                        .param("nombres", "Rosa Actualizada")
                        .param("apellidoPaterno", "Flores")
                        .param("apellidoMaterno", "Ramos")
                        .param("fechaNacimiento", "1985-05-15")
                        .param("sexo", "FEMENINO")
                        .param("telefono", "987654321"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apoderados/5"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(estudianteApoderadoService).actualizarApoderado(eq(5L), any(ApoderadoUpdateRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"APODERADO_CREAR"})
    @DisplayName("09. GET /estudiantes/{id}/apoderados/nuevo con APODERADO_CREAR muestra formulario de asignación")
    void nuevoParaEstudiante_conPermiso_muestraVistaAsignar() throws Exception {
        PersonaResponse estudianteMock = new PersonaResponse();
        estudianteMock.setId(10L);
        estudianteMock.setNumeroDocumento("77889900");
        estudianteMock.setNombres("Carlos");
        estudianteMock.setApellidoPaterno("Mendoza");
        estudianteMock.setApellidoMaterno("Flores");

        when(estudianteService.obtenerEstudiantePorId(10L)).thenReturn(estudianteMock);
        when(personaActorRepository.findPersonasActivasByTipoActorCodigoOrderByApellidos("PADRE_APODERADO"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/estudiantes/10/apoderados/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("apoderados/asignar"))
                .andExpect(model().attributeExists("estudiante", "relacion", "apoderados", "parentescos"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"APODERADO_EDITAR"})
    @DisplayName("10. POST /estudiantes/{estId}/apoderados/{relId}/principal con APODERADO_EDITAR y CSRF cambia principal")
    void marcarPrincipal_conPermisoYCsrf_ejecutaYRedirige() throws Exception {
        mockMvc.perform(post("/estudiantes/10/apoderados/50/principal").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudiantes/10"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(estudianteApoderadoService).marcarComoPrincipal(10L, 50L);
    }
}
