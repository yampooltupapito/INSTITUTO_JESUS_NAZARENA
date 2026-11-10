package com.jesusnazareno.asistencia.config.security;

import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler de autenticación fallida que audita intentos erróneos sin almacenar contraseñas ni datos sensibles.
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuditoriaService auditoriaService;

    public CustomAuthenticationFailureHandler(AuditoriaService auditoriaService) {
        super("/login?error");
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String ip = request.getRemoteAddr();

        auditoriaService.registrarEventoConContexto(
                username != null ? username.trim() : null,
                AccionAuditoria.LOGIN_FALLIDO,
                ModuloAuditoria.SEGURIDAD,
                ResultadoAuditoria.FALLO,
                "Intento fallido de inicio de sesión (credenciales incorrectas o usuario inexistente).",
                ip != null ? ip : "127.0.0.1",
                "USUARIO",
                null,
                null
        );

        super.onAuthenticationFailure(request, response, exception);
    }
}
