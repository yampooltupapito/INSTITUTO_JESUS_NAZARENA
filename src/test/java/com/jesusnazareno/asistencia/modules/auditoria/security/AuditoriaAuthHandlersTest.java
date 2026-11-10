package com.jesusnazareno.asistencia.modules.auditoria.security;

import com.jesusnazareno.asistencia.config.security.CustomAuthenticationFailureHandler;
import com.jesusnazareno.asistencia.config.security.CustomAuthenticationSuccessHandler;
import com.jesusnazareno.asistencia.config.security.CustomLogoutSuccessHandler;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditoriaAuthHandlersTest {

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Login exitoso registra evento LOGIN_EXITOSO con usuario e IP")
    void loginExitosoRegistraAuditoria() throws Exception {
        CustomAuthenticationSuccessHandler handler = new CustomAuthenticationSuccessHandler(usuarioRepository, auditoriaService);

        Usuario usuario = Usuario.builder()
                .id(1L)
                .username("admin")
                .rol(Rol.builder().codigo("ADMIN").build())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", "password", List.of());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.50");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        handler.onAuthenticationSuccess(request, response, authentication);

        ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);
        verify(auditoriaService).registrarEventoConContexto(
                eq("admin"),
                eq(AccionAuditoria.LOGIN_EXITOSO),
                eq(ModuloAuditoria.SEGURIDAD),
                eq(ResultadoAuditoria.EXITO),
                descCaptor.capture(),
                eq("192.168.1.50"),
                eq("USUARIO"),
                eq(1L),
                isNull()
        );

        assertThat(descCaptor.getValue()).contains("Inicio de sesión exitoso");
    }

    @Test
    @DisplayName("Login fallido registra evento LOGIN_FALLIDO pero NUNCA la contraseña")
    void loginFallidoNoRegistraPassword() throws Exception {
        CustomAuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler(auditoriaService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "intruso");
        request.setParameter("password", "PasswordSecreto123!");
        request.setRemoteAddr("200.48.10.2");
        MockHttpServletResponse response = new MockHttpServletResponse();

        BadCredentialsException exception = new BadCredentialsException("Bad credentials");
        handler.onAuthenticationFailure(request, response, exception);

        ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);

        verify(auditoriaService).registrarEventoConContexto(
                eq("intruso"),
                eq(AccionAuditoria.LOGIN_FALLIDO),
                eq(ModuloAuditoria.SEGURIDAD),
                eq(ResultadoAuditoria.FALLO),
                descCaptor.capture(),
                eq("200.48.10.2"),
                eq("USUARIO"),
                isNull(),
                isNull()
        );

        String descripcion = descCaptor.getValue();
        assertThat(descripcion).isNotBlank();
        assertThat(descripcion).doesNotContain("PasswordSecreto123!");
    }

    @Test
    @DisplayName("Logout registra evento LOGOUT con usuario e IP")
    void logoutRegistraAuditoria() throws Exception {
        CustomLogoutSuccessHandler handler = new CustomLogoutSuccessHandler(auditoriaService);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("director1", "password", List.of());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.80");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onLogoutSuccess(request, response, authentication);

        ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);
        verify(auditoriaService).registrarEventoConContexto(
                eq("director1"),
                eq(AccionAuditoria.LOGOUT),
                eq(ModuloAuditoria.SEGURIDAD),
                eq(ResultadoAuditoria.EXITO),
                descCaptor.capture(),
                eq("192.168.1.80"),
                eq("USUARIO"),
                isNull(),
                isNull()
        );

        assertThat(descCaptor.getValue()).contains("Cierre de sesión");
    }
}
