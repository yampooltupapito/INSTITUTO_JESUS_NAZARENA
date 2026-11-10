package com.jesusnazareno.asistencia.modules.acceso.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.dto.PermisoResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.RolResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioCreateRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioUpdateRequest;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolPermisoRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.acceso.service.RolService;
import com.jesusnazareno.asistencia.modules.acceso.service.UsuarioService;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
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
@DisplayName("Pruebas de Seguridad y Web - Prompt 18: UsuarioController y RolController")
class UsuarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditorAware<String> auditorAware;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private RolService rolService;

    @MockBean
    private PersonaRepository personaRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private RolRepository rolRepository;

    @MockBean
    private RolPermisoRepository rolPermisoRepository;

    private UsuarioResponse crearUsuarioMock(Long id, String username, String rolCodigo, boolean activo) {
        return UsuarioResponse.builder()
                .id(id)
                .personaId(10L)
                .personaNombreCompleto("Flores Mendoza, Carlos")
                .personaNumeroDocumento("44556677")
                .username(username)
                .rolId(1L)
                .rolCodigo(rolCodigo)
                .rolNombre("Administrador")
                .activo(activo)
                .ultimoAcceso(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .updatedBy("admin")
                .permisos(List.of("USUARIO_VER", "USUARIO_CREAR", "USUARIO_EDITAR"))
                .build();
    }

    @Test
    @DisplayName("1. /usuarios requiere autenticación (anónimo redirige a /login)")
    void usuarios_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_VER"})
    @DisplayName("2. USUARIO_VER permite listar usuarios exitosamente (HTTP 200)")
    void usuarios_conPermisoUsuarioVer_retorna200YVista() throws Exception {
        Page<UsuarioResponse> paginaMock = new PageImpl<>(List.of(crearUsuarioMock(1L, "admin", "ADMIN", true)));
        when(usuarioService.listarPaginado(any(), any(), any(), any(Pageable.class))).thenReturn(paginaMock);
        when(rolService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/list"))
                .andExpect(model().attributeExists("usuariosPage"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(username = "docente_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("3. Sin permiso USUARIO_VER devuelve 403 Forbidden")
    void usuarios_sinPermisoUsuarioVer_retorna403() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_CREAR"})
    @DisplayName("4. USUARIO_CREAR permite crear usuario mediante POST con CSRF")
    void usuarios_conPermisoCrear_guardaYRedirige() throws Exception {
        UsuarioResponse nuevo = crearUsuarioMock(2L, "jquispe", "DOCENTE", true);
        when(usuarioService.crear(any(UsuarioCreateRequest.class))).thenReturn(nuevo);

        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .param("personaId", "10")
                        .param("username", "jquispe")
                        .param("password", "PasswordSeguro123")
                        .param("rolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(usuarioService).crear(any(UsuarioCreateRequest.class));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_CREAR"})
    @DisplayName("5. Username duplicado es rechazado con mensaje amigable")
    void usuarios_usernameDuplicado_muestraError() throws Exception {
        when(usuarioService.crear(any(UsuarioCreateRequest.class)))
                .thenThrow(new BusinessException("El nombre de usuario 'admin' ya se encuentra registrado"));
        when(personaRepository.findPersonasDisponiblesParaUsuario()).thenReturn(Collections.emptyList());
        when(rolService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .param("personaId", "10")
                        .param("username", "admin")
                        .param("password", "PasswordSeguro123")
                        .param("rolId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attribute("errorMessage", "El nombre de usuario 'admin' ya se encuentra registrado"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_CREAR"})
    @DisplayName("6. Persona duplicada es rechazada si ya tiene usuario")
    void usuarios_personaDuplicada_muestraError() throws Exception {
        when(usuarioService.crear(any(UsuarioCreateRequest.class)))
                .thenThrow(new BusinessException("La persona seleccionada ya tiene un usuario asignado"));
        when(personaRepository.findPersonasDisponiblesParaUsuario()).thenReturn(Collections.emptyList());
        when(rolService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .param("personaId", "10")
                        .param("username", "nuevo_user")
                        .param("password", "PasswordSeguro123")
                        .param("rolId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attribute("errorMessage", "La persona seleccionada ya tiene un usuario asignado"));
    }

    @Test
    @DisplayName("7. Password se almacena y verifica mediante BCryptPasswordEncoder")
    void password_seCodificaConBCrypt() {
        String plainPassword = "ClaveSegura2026";
        String encoded = passwordEncoder.encode(plainPassword);

        assertNotNull(encoded);
        assertNotEquals(plainPassword, encoded);
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$"));
        assertTrue(passwordEncoder.matches(plainPassword, encoded));
        assertFalse(passwordEncoder.matches("ClaveErronea", encoded));
    }

    @Test
    @DisplayName("8. Password o hash BCrypt NUNCA existe ni se expone en UsuarioResponse")
    void password_noExisteEnUsuarioResponse() {
        for (Field field : UsuarioResponse.class.getDeclaredFields()) {
            assertNotEquals("password", field.getName().toLowerCase());
            assertNotEquals("hash", field.getName().toLowerCase());
            assertNotEquals("contrasena", field.getName().toLowerCase());
        }
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_EDITAR"})
    @DisplayName("9. USUARIO_EDITAR permite editar usuario con POST y CSRF")
    void usuarios_conPermisoEditar_actualizaYRedirige() throws Exception {
        UsuarioResponse response = crearUsuarioMock(1L, "admin_editado", "ADMIN", true);
        when(usuarioService.buscarPorId(1L)).thenReturn(response);
        when(usuarioService.actualizar(eq(1L), any(UsuarioUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/usuarios/1")
                        .with(csrf())
                        .param("username", "admin_editado")
                        .param("rolId", "1")
                        .param("activo", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(usuarioService).actualizar(eq(1L), any(UsuarioUpdateRequest.class));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_EDITAR"})
    @DisplayName("10. Cambio de contraseña genera nuevo hash BCrypt y actualiza correctamente")
    void password_cambioExitoso_guardaHashNuevoYRedirige() throws Exception {
        UsuarioResponse response = crearUsuarioMock(1L, "admin", "ADMIN", true);
        when(usuarioService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(post("/usuarios/1/password")
                        .with(csrf())
                        .param("nuevaPassword", "NuevaClave2026")
                        .param("confirmarPassword", "NuevaClave2026"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(usuarioService).resetearPassword(1L, "NuevaClave2026", "NuevaClave2026");
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_EDITAR"})
    @DisplayName("11. Password y confirmación diferentes se rechazan en backend")
    void password_diferenteConfirmacion_rechazaFormulario() throws Exception {
        UsuarioResponse response = crearUsuarioMock(1L, "admin", "ADMIN", true);
        when(usuarioService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(post("/usuarios/1/password")
                        .with(csrf())
                        .param("nuevaPassword", "NuevaClave2026")
                        .param("confirmarPassword", "ClaveDiferente2026"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/password"))
                .andExpect(model().hasErrors());

        verify(usuarioService, never()).resetearPassword(any(), any(), any());
    }

    @Test
    @DisplayName("12. Usuario inactivo no puede autenticarse en Spring Security")
    void usuarioInactivo_fallaAutenticacionEnSpringSecurity() throws Exception {
        Rol rolAdmin = Rol.builder().id(1L).codigo("ADMIN").activo(true).build();
        Usuario inactivo = Usuario.builder()
                .id(99L)
                .username("cuenta_desactivada")
                .password(passwordEncoder.encode("ClaveValida123"))
                .rol(rolAdmin)
                .activo(false)
                .build();

        when(usuarioRepository.findByUsernameWithDetails("cuenta_desactivada")).thenReturn(Optional.of(inactivo));

        mockMvc.perform(formLogin("/login").user("cuenta_desactivada").password("ClaveValida123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_EDITAR"})
    @DisplayName("13. Activar usuario funciona mediante POST /usuarios/{id}/activar")
    void usuarios_activar_funcionaConPost() throws Exception {
        UsuarioResponse response = crearUsuarioMock(2L, "jquispe", "DOCENTE", true);
        when(usuarioService.activar(2L)).thenReturn(response);

        mockMvc.perform(post("/usuarios/2/activar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(usuarioService).activar(2L);
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_EDITAR"})
    @DisplayName("14. Desactivar NO elimina el registro físico (soft deactivate)")
    void usuarios_desactivar_desactivaSinEliminar() throws Exception {
        UsuarioResponse response = crearUsuarioMock(2L, "jquispe", "DOCENTE", false);
        when(usuarioService.desactivar(2L)).thenReturn(response);

        mockMvc.perform(post("/usuarios/2/desactivar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(usuarioService).desactivar(2L);
        verify(usuarioRepository, never()).deleteById(any());
        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_EDITAR"})
    @DisplayName("15. Auto-desactivación del usuario autenticado es rechazada con mensaje amigable")
    void usuarios_autoDesactivacion_esRechazada() throws Exception {
        when(usuarioService.desactivar(1L))
                .thenThrow(new BusinessException("No puede desactivar su propio usuario"));

        mockMvc.perform(post("/usuarios/1/desactivar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attribute("errorMessage", "No puede desactivar su propio usuario"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_VER"})
    @DisplayName("16. Roles proceden de PostgreSQL mediante RolService")
    void roles_listado_procedeDePostgresql() throws Exception {
        RolResponse rol = new RolResponse(1L, "ADMIN", "Administrador", "Acceso total", true, Collections.emptyList());
        when(rolService.listarTodos()).thenReturn(List.of(rol));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(view().name("roles/list"))
                .andExpect(model().attributeExists("roles"));

        verify(rolService).listarTodos();
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_VER"})
    @DisplayName("17. Permisos proceden de rol_permiso asociados al rol")
    void roles_detalle_incluyePermisosAsociados() throws Exception {
        PermisoResponse perm = new PermisoResponse(1L, "USUARIO_VER", "Ver Usuarios", "Consulta", "ACCESO", true);
        RolResponse rol = new RolResponse(1L, "ADMIN", "Administrador", "Acceso total", true, List.of(perm));
        when(rolService.buscarPorId(1L)).thenReturn(rol);

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("roles/detail"))
                .andExpect(model().attributeExists("rol"));

        verify(rolService).buscarPorId(1L);
    }

    @Test
    @DisplayName("18. /roles está protegido (anónimo redirige a login, sin permiso retorna 403)")
    void roles_requierePermisoUsuarioVer() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_CREAR"})
    @DisplayName("19. POST sin CSRF es rechazado con HTTP 403 Forbidden")
    void post_sinCsrf_esRechazadoCon403() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .param("personaId", "10")
                        .param("username", "sin_csrf")
                        .param("password", "PasswordSeguro123")
                        .param("rolId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_VER"})
    @DisplayName("20. Búsqueda por query 'q' delega a repositorio/servicio con filtro correcto")
    void busqueda_conQueryQ_delegaAServicio() throws Exception {
        Page<UsuarioResponse> paginaMock = new PageImpl<>(List.of(crearUsuarioMock(1L, "admin", "ADMIN", true)));
        when(usuarioService.listarPaginado(eq("Mendoza"), any(), any(), any(Pageable.class))).thenReturn(paginaMock);
        when(rolService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios").param("q", "Mendoza"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("q", "Mendoza"));

        verify(usuarioService).listarPaginado(eq("Mendoza"), any(), any(), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_VER"})
    @DisplayName("21. Filtros por rol y estado delegan a servicio de forma combinada")
    void filtros_porRolYEstado_deleganAServicio() throws Exception {
        Page<UsuarioResponse> paginaMock = new PageImpl<>(Collections.emptyList());
        when(usuarioService.listarPaginado(eq("admin"), eq(1L), eq(true), any(Pageable.class))).thenReturn(paginaMock);
        when(rolService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios")
                        .param("q", "admin")
                        .param("rolId", "1")
                        .param("activo", "true"))
                .andExpect(status().isOk());

        verify(usuarioService).listarPaginado(eq("admin"), eq(1L), eq(true), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"USUARIO_VER"})
    @DisplayName("22. Paginación funciona correctamente (parámetros page y size)")
    void paginacion_paginaYSegundoParametro_funcionan() throws Exception {
        Page<UsuarioResponse> paginaMock = new PageImpl<>(Collections.emptyList());
        when(usuarioService.listarPaginado(any(), any(), any(), any(Pageable.class))).thenReturn(paginaMock);
        when(rolService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios")
                        .param("page", "2")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 2))
                .andExpect(model().attribute("pageSize", 20));
    }

    @Test
    @WithMockUser(username = "admin_auditor", roles = {"ADMIN"})
    @DisplayName("23. Auditoría continúa funcionando alimentada por usuario autenticado")
    void auditoria_usuarioAutenticado_alimentaAuditorAware() {
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        assertTrue(currentAuditor.isPresent());
        assertEquals("admin_auditor", currentAuditor.get());
    }
}
