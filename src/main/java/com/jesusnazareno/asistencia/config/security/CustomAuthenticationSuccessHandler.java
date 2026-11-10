package com.jesusnazareno.asistencia.config.security;

import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handler de autenticación exitosa que actualiza la fecha y hora del último acceso
 * del usuario en la base de datos y genera el evento de auditoría correspondiente.
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public CustomAuthenticationSuccessHandler(UsuarioRepository usuarioRepository,
                                              AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        setDefaultTargetUrl("/dashboard");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();
            String ip = request.getRemoteAddr();

            usuarioRepository.findByUsername(username).ifPresent(usuario -> {
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioRepository.save(usuario);

                auditoriaService.registrarEventoConContexto(
                        username,
                        AccionAuditoria.LOGIN_EXITOSO,
                        ModuloAuditoria.SEGURIDAD,
                        ResultadoAuditoria.EXITO,
                        "Inicio de sesión exitoso.",
                        ip != null ? ip : "127.0.0.1",
                        "USUARIO",
                        usuario.getId(),
                        null
                );
            });
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
