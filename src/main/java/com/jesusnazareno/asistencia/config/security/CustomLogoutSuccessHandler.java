package com.jesusnazareno.asistencia.config.security;

import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler de logout exitoso que audita el cierre de sesión del usuario.
 */
@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final AuditoriaService auditoriaService;

    public CustomLogoutSuccessHandler(AuditoriaService auditoriaService) {
        setDefaultTargetUrl("/login?logout");
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();
            String ip = request.getRemoteAddr();

            auditoriaService.registrarEventoConContexto(
                    username,
                    AccionAuditoria.LOGOUT,
                    ModuloAuditoria.SEGURIDAD,
                    ResultadoAuditoria.EXITO,
                    "Cierre de sesión de usuario.",
                    ip != null ? ip : "127.0.0.1",
                    "USUARIO",
                    null,
                    null
            );
        }

        super.onLogoutSuccess(request, response, authentication);
    }
}
