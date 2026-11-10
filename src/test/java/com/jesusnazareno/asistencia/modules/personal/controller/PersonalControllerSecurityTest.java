package com.jesusnazareno.asistencia.modules.personal.controller;

import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalActorDTO;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalCreateRequest;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalResponse;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalUpdateRequest;
import com.jesusnazareno.asistencia.modules.personal.service.PersonalService;
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
import java.time.LocalDateTime;
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
@DisplayName("Pruebas de Seguridad y Web - Prompt 19: PersonalController")
class PersonalControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditorAware<String> auditorAware;

    @MockBean
    private PersonalService personalService;

    private PersonalResponse crearPersonalMock(Long id, String dni, String nombres, String actorCodigo, boolean activo) {
        PersonalResponse resp = new PersonalResponse();
        resp.setId(id);
        resp.setTipoDocumento(TipoDocumento.DNI);
        resp.setNumeroDocumento(dni);
        resp.setNombres(nombres);
        resp.setApellidoPaterno("Pérez");
        resp.setApellidoMaterno("Quispe");
        resp.setFechaNacimiento(LocalDate.of(1985, 5, 20));
        resp.setSexo(Sexo.MASCULINO);
        resp.setTelefono("987654321");
        resp.setCorreo("jperez@colegio.edu.pe");
        resp.setDireccion("Av. Principal 123");
        resp.setActivo(activo);
        resp.setCreatedAt(LocalDateTime.now().minusMonths(6));
        resp.setUpdatedAt(LocalDateTime.now());
        resp.setCreatedBy("admin");
        resp.setUpdatedBy("admin");

        PersonalActorDTO actor = new PersonalActorDTO(1L, 2L, actorCodigo, actorCodigo, LocalDate.now().minusMonths(6), null, activo);
        resp.setActores(List.of(actor));
        resp.setActoresResumen(actorCodigo);
        resp.setTieneUsuario(false);

        return resp;
    }

    @Test
    @DisplayName("1. /personal requiere autenticación (anónimo redirige a /login)")
    void personal_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/personal"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"PERSONAL_VER"})
    @DisplayName("2. PERSONAL_VER permite listar personal exitosamente (HTTP 200)")
    void personal_conPermisoPersonalVer_retorna200YVista() throws Exception {
        Page<PersonalResponse> paginaMock = new PageImpl<>(List.of(crearPersonalMock(1L, "10203040", "Juan", "DOCENTE", true)));
        when(personalService.listarPersonal(any(), any(), any(), any(Pageable.class))).thenReturn(paginaMock);
        when(personalService.obtenerTiposPersonalPermitidos()).thenReturn(List.of("DIRECTOR", "DOCENTE", "AUXILIAR"));

        mockMvc.perform(get("/personal"))
                .andExpect(status().isOk())
                .andExpect(view().name("personal/list"))
                .andExpect(model().attributeExists("personalPage"))
                .andExpect(model().attributeExists("tiposPersonal"));
    }

    @Test
    @WithMockUser(username = "docente_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("3. Sin permiso PERSONAL_VER devuelve 403 Forbidden")
    void personal_sinPermisoPersonalVer_retorna403() throws Exception {
        mockMvc.perform(get("/personal"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"PERSONAL_CREAR"})
    @DisplayName("4. PERSONAL_CREAR permite registrar personal con POST y CSRF")
    void personal_conPermisoCrear_registraYRedirige() throws Exception {
        PersonalResponse mockResp = crearPersonalMock(10L, "44556677", "María", "AUXILIAR", true);
        when(personalService.registrarPersonal(any(PersonalCreateRequest.class))).thenReturn(mockResp);

        mockMvc.perform(post("/personal")
                        .with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "44556677")
                        .param("nombres", "María")
                        .param("apellidoPaterno", "Flores")
                        .param("apellidoMaterno", "Mendoza")
                        .param("fechaNacimiento", "1990-03-15")
                        .param("sexo", "FEMENINO")
                        .param("tipoActor", "AUXILIAR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/personal"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(personalService).registrarPersonal(any(PersonalCreateRequest.class));
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"PERSONAL_EDITAR"})
    @DisplayName("15. PERSONAL_EDITAR protege actualización mediante POST")
    void personal_conPermisoEditar_actualizaYRedirige() throws Exception {
        PersonalResponse mockResp = crearPersonalMock(1L, "10203040", "Juan", "DOCENTE", true);
        when(personalService.buscarPorId(1L)).thenReturn(mockResp);
        when(personalService.actualizarPersonal(eq(1L), any(PersonalUpdateRequest.class))).thenReturn(mockResp);

        mockMvc.perform(post("/personal/1")
                        .with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "10203040")
                        .param("nombres", "Juan Alberto")
                        .param("apellidoPaterno", "Pérez")
                        .param("apellidoMaterno", "Quispe")
                        .param("fechaNacimiento", "1985-05-20")
                        .param("sexo", "MASCULINO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/personal/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(personalService).actualizarPersonal(eq(1L), any(PersonalUpdateRequest.class));
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"PERSONAL_CREAR"})
    @DisplayName("16. POST sin CSRF es rechazado con 403 Forbidden")
    void personal_postSinCsrf_esRechazado() throws Exception {
        mockMvc.perform(post("/personal")
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "44556677")
                        .param("nombres", "María")
                        .param("apellidoPaterno", "Flores")
                        .param("apellidoMaterno", "Mendoza")
                        .param("fechaNacimiento", "1990-03-15")
                        .param("sexo", "FEMENINO")
                        .param("tipoActor", "AUXILIAR"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"PERSONAL_VER"})
    @DisplayName("17. Búsqueda por 'q' delega a servicio con parámetro correcto")
    void personal_busquedaQ_delegaAServicio() throws Exception {
        Page<PersonalResponse> paginaMock = new PageImpl<>(Collections.emptyList());
        when(personalService.listarPersonal(eq("Pérez"), any(), any(), any(Pageable.class))).thenReturn(paginaMock);
        when(personalService.obtenerTiposPersonalPermitidos()).thenReturn(List.of("DIRECTOR", "DOCENTE", "AUXILIAR"));

        mockMvc.perform(get("/personal").param("q", "Pérez"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("q", "Pérez"));

        verify(personalService).listarPersonal(eq("Pérez"), any(), any(), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"PERSONAL_VER"})
    @DisplayName("18. Filtros por función y estado delegan de forma combinada")
    void personal_filtros_deleganAServicio() throws Exception {
        Page<PersonalResponse> paginaMock = new PageImpl<>(Collections.emptyList());
        when(personalService.listarPersonal(eq("Juan"), eq("DOCENTE"), eq("ACTIVOS"), any(Pageable.class))).thenReturn(paginaMock);
        when(personalService.obtenerTiposPersonalPermitidos()).thenReturn(List.of("DIRECTOR", "DOCENTE", "AUXILIAR"));

        mockMvc.perform(get("/personal")
                        .param("q", "Juan")
                        .param("tipoActor", "DOCENTE")
                        .param("estado", "ACTIVOS"))
                .andExpect(status().isOk());

        verify(personalService).listarPersonal(eq("Juan"), eq("DOCENTE"), eq("ACTIVOS"), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "director_user", authorities = {"PERSONAL_VER"})
    @DisplayName("19. Paginación funciona correctamente (parámetros page y size)")
    void personal_paginacion_funciona() throws Exception {
        Page<PersonalResponse> paginaMock = new PageImpl<>(Collections.emptyList());
        when(personalService.listarPersonal(any(), any(), any(), any(Pageable.class))).thenReturn(paginaMock);
        when(personalService.obtenerTiposPersonalPermitidos()).thenReturn(List.of("DIRECTOR", "DOCENTE", "AUXILIAR"));

        mockMvc.perform(get("/personal")
                        .param("page", "1")
                        .param("size", "25"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("pageSize", 25));
    }

    @Test
    @WithMockUser(username = "admin_personal", roles = {"ADMIN"})
    @DisplayName("20. Auditoría continúa funcionando alimentada por usuario autenticado")
    void auditoria_usuarioAutenticado_alimentaAuditor() {
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        assertTrue(currentAuditor.isPresent());
        assertEquals("admin_personal", currentAuditor.get());
    }
}
