package com.jesusnazareno.asistencia.modules.estudiante;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
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
@DisplayName("Pruebas de Seguridad y Web - EstudianteController")
class EstudianteControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstudianteService estudianteService;

    @Test
    @DisplayName("1. /estudiantes sin autenticación redirige al login")
    void listar_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/estudiantes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "docente_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("2. /estudiantes sin permiso ESTUDIANTE_VER retorna 403 Forbidden")
    void listar_sinPermiso_retorna403() throws Exception {
        mockMvc.perform(get("/estudiantes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "auxiliar_user", authorities = {"ESTUDIANTE_VER"})
    @DisplayName("3. /estudiantes con ESTUDIANTE_VER retorna 200 OK y vista estudiantes/list")
    void listar_conPermiso_retorna200YVista() throws Exception {
        Page<PersonaResponse> paginaMock = new PageImpl<>(List.of(
                PersonaResponse.builder().id(1L).numeroDocumento("77889900").nombres("Juan").apellidoPaterno("Pérez").apellidoMaterno("Gómez").activo(true).build()
        ));

        when(estudianteService.buscarEstudiantes(any(), any(), any(Pageable.class))).thenReturn(paginaMock);

        mockMvc.perform(get("/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/list"))
                .andExpect(model().attributeExists("estudiantes"));
    }

    @Test
    @WithMockUser(username = "docente_user", authorities = {"ESTUDIANTE_VER"})
    @DisplayName("4. /estudiantes/nuevo sin ESTUDIANTE_CREAR retorna 403 Forbidden")
    void nuevo_sinPermisoCrear_retorna403() throws Exception {
        mockMvc.perform(get("/estudiantes/nuevo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_CREAR"})
    @DisplayName("5. /estudiantes/nuevo con ESTUDIANTE_CREAR retorna 200 y vista estudiantes/form")
    void nuevo_conPermisoCrear_retorna200() throws Exception {
        mockMvc.perform(get("/estudiantes/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/form"))
                .andExpect(model().attributeExists("estudiante", "tiposDocumento", "sexos"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_CREAR"})
    @DisplayName("6. POST /estudiantes sin token CSRF es rechazado con 403")
    void crear_sinCsrf_retorna403() throws Exception {
        mockMvc.perform(post("/estudiantes")
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "77889900")
                        .param("nombres", "Carlos")
                        .param("apellidoPaterno", "Mendoza")
                        .param("apellidoMaterno", "Flores")
                        .param("fechaNacimiento", "2010-05-15")
                        .param("sexo", "MASCULINO"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_CREAR"})
    @DisplayName("7. POST /estudiantes con datos inválidos vuelve al formulario con errores")
    void crear_datosInvalidos_retornaFormularioConErrores() throws Exception {
        mockMvc.perform(post("/estudiantes").with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "123") // DNI inválido (no 8 dígitos)
                        .param("nombres", "") // Campo obligatorio vacío
                        .param("apellidoPaterno", "Mendoza")
                        .param("apellidoMaterno", "Flores")
                        .param("fechaNacimiento", "2010-05-15")
                        .param("sexo", "MASCULINO"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_CREAR"})
    @DisplayName("8. POST /estudiantes con documento duplicado maneja BusinessException y vuelve al formulario")
    void crear_documentoDuplicado_retornaFormulario() throws Exception {
        when(estudianteService.registrarEstudiante(any(PersonaRequest.class)))
                .thenThrow(new BusinessException("Ya existe una persona con este número de documento."));

        mockMvc.perform(post("/estudiantes").with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "77889900")
                        .param("nombres", "Carlos")
                        .param("apellidoPaterno", "Mendoza")
                        .param("apellidoMaterno", "Flores")
                        .param("fechaNacimiento", "2010-05-15")
                        .param("sexo", "MASCULINO"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_CREAR"})
    @DisplayName("9. POST /estudiantes con datos válidos registra y redirige con mensaje flash")
    void crear_datosValidos_redirigeConMensaje() throws Exception {
        PersonaResponse creado = PersonaResponse.builder().id(100L).build();
        when(estudianteService.registrarEstudiante(any(PersonaRequest.class))).thenReturn(creado);

        mockMvc.perform(post("/estudiantes").with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "77889900")
                        .param("nombres", "Carlos")
                        .param("apellidoPaterno", "Mendoza")
                        .param("apellidoMaterno", "Flores")
                        .param("fechaNacimiento", "2010-05-15")
                        .param("sexo", "MASCULINO")
                        .param("telefono", "987654321")
                        .param("correo", "carlos@correo.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudiantes"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_VER"})
    @DisplayName("10. GET /estudiantes/{id} retorna ficha de detalle")
    void detalle_conPermiso_retorna200() throws Exception {
        PersonaResponse estudiante = PersonaResponse.builder()
                .id(100L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("77889900")
                .nombres("Carlos")
                .apellidoPaterno("Mendoza")
                .apellidoMaterno("Flores")
                .sexo(Sexo.MASCULINO)
                .activo(true)
                .build();

        when(estudianteService.obtenerEstudiantePorId(100L)).thenReturn(estudiante);

        mockMvc.perform(get("/estudiantes/100"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/detail"))
                .andExpect(model().attribute("estudiante", estudiante));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_EDITAR"})
    @DisplayName("11. GET /estudiantes/{id}/editar retorna formulario de edición")
    void editar_conPermiso_retornaFormulario() throws Exception {
        PersonaResponse estudiante = PersonaResponse.builder()
                .id(100L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("77889900")
                .nombres("Carlos")
                .apellidoPaterno("Mendoza")
                .apellidoMaterno("Flores")
                .fechaNacimiento(LocalDate.of(2010, 5, 15))
                .sexo(Sexo.MASCULINO)
                .activo(true)
                .build();

        when(estudianteService.obtenerEstudiantePorId(100L)).thenReturn(estudiante);

        mockMvc.perform(get("/estudiantes/100/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/form"))
                .andExpect(model().attribute("modoEdicion", true));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_EDITAR"})
    @DisplayName("12. POST /estudiantes/{id} con datos válidos actualiza y redirige con mensaje flash")
    void actualizar_datosValidos_redirigeConMensaje() throws Exception {
        PersonaResponse actualizado = PersonaResponse.builder().id(100L).build();
        when(estudianteService.actualizarEstudiante(eq(100L), any(PersonaRequest.class))).thenReturn(actualizado);

        mockMvc.perform(post("/estudiantes/100").with(csrf())
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "77889900")
                        .param("nombres", "Carlos Eduardo")
                        .param("apellidoPaterno", "Mendoza")
                        .param("apellidoMaterno", "Flores")
                        .param("fechaNacimiento", "2010-05-15")
                        .param("sexo", "MASCULINO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudiantes/100"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_EDITAR"})
    @DisplayName("13. POST /estudiantes/{id}/desactivar desactiva y redirige")
    void desactivar_conCsrf_desactivaYRedirige() throws Exception {
        mockMvc.perform(post("/estudiantes/100/desactivar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudiantes"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(estudianteService).desactivarEstudiante(100L);
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ESTUDIANTE_EDITAR"})
    @DisplayName("14. POST /estudiantes/{id}/activar reactiva y redirige")
    void activar_conCsrf_activaYRedirige() throws Exception {
        mockMvc.perform(post("/estudiantes/100/activar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudiantes"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(estudianteService).activarEstudiante(100L);
    }
}
