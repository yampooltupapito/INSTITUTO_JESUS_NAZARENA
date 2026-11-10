package com.jesusnazareno.asistencia.config.security;

import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolPermisoRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditorAware<String> auditorAware;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private RolPermisoRepository rolPermisoRepository;

    @Test
    @DisplayName("1. /login es accesible sin autenticación")
    void login_publico_retorna200YVistaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("2. /ping es accesible sin autenticación")
    void ping_publico_retorna200() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("3. /inicio requiere autenticación (anónimo es redirigido a /login)")
    void inicio_anonimo_redirigeALogin() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("3.b /inicio con usuario autenticado redirige a /dashboard")
    void inicio_autenticado_redirigeADashboard() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @DisplayName("4. Credenciales válidas permiten autenticación y redirigen a /dashboard")
    void login_credencialesValidas_redirigeADashboard() throws Exception {
        Rol rolAdmin = Rol.builder().id(1L).codigo("ADMIN").activo(true).build();
        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("director")
                .password(passwordEncoder.encode("ClaveValida123"))
                .rol(rolAdmin)
                .activo(true)
                .build();

        when(usuarioRepository.findByUsernameWithDetails("director")).thenReturn(Optional.of(usuario));
        when(rolPermisoRepository.findPermisosActivosByRolId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(formLogin("/login").user("director").password("ClaveValida123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @DisplayName("5. Credenciales incorrectas fallan y redirigen a /login?error")
    void login_credencialesIncorrectas_redirigeAError() throws Exception {
        Rol rolAdmin = Rol.builder().id(1L).codigo("ADMIN").activo(true).build();
        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("director")
                .password(passwordEncoder.encode("ClaveValida123"))
                .rol(rolAdmin)
                .activo(true)
                .build();

        when(usuarioRepository.findByUsernameWithDetails("director")).thenReturn(Optional.of(usuario));

        mockMvc.perform(formLogin("/login").user("director").password("ClaveErronea"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @DisplayName("6. Usuario inactivo no puede autenticarse y redirige a /login?error")
    void login_usuarioInactivo_fallaAutenticacion() throws Exception {
        Rol rolAdmin = Rol.builder().id(1L).codigo("ADMIN").activo(true).build();
        Usuario usuarioInactivo = Usuario.builder()
                .id(11L)
                .username("usuario_inactivo")
                .password(passwordEncoder.encode("ClaveValida123"))
                .rol(rolAdmin)
                .activo(false)
                .build();

        when(usuarioRepository.findByUsernameWithDetails("usuario_inactivo")).thenReturn(Optional.of(usuarioInactivo));

        mockMvc.perform(formLogin("/login").user("usuario_inactivo").password("ClaveValida123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser
    @DisplayName("7. Logout mediante POST con CSRF funciona y redirige a /login?logout")
    void logout_postConCsrf_redirigeALoginConParametroLogout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    @WithMockUser
    @DisplayName("8. CSRF permanece habilitado (rechaza POST a /logout sin token)")
    void csrf_activo_rechazaPostSinToken() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "docente", roles = {"DOCENTE"})
    @DisplayName("9. Usuario autenticado sin permiso recibe 403 (o reenvío a /403)")
    void accesoDenegado_sinRolApropiado_retorna403() throws Exception {
        mockMvc.perform(get("/test/admin-only"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/403"));
    }

    @Test
    @WithMockUser(username = "admin_real", roles = {"ADMIN"})
    @DisplayName("9.b Usuario con rol ADMIN accede a endpoint protegido")
    void accesoPermitido_conRolAdmin_retorna200() throws Exception {
        mockMvc.perform(get("/test/admin-only"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("10. AuditorAware retorna username de usuario autenticado y 'system' cuando es anónimo")
    void auditorAware_retornaUsuarioAutenticadoOSystem() {
        // Sin autenticación
        SecurityContextHolder.clearContext();
        assertEquals("system", auditorAware.getCurrentAuditor().orElse(""));

        // Con autenticación
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("juan_perez", "pass", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals("juan_perez", auditorAware.getCurrentAuditor().orElse(""));

        SecurityContextHolder.clearContext();
    }
}
