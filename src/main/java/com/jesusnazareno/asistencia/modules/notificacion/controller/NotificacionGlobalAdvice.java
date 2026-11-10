package com.jesusnazareno.asistencia.modules.notificacion.controller;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.notificacion.dto.NotificacionResponse;
import com.jesusnazareno.asistencia.modules.notificacion.service.NotificacionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Advice global para inyectar el contador de notificaciones no leídas
 * y las últimas notificaciones en todas las vistas de usuarios autenticados.
 */
@ControllerAdvice
public class NotificacionGlobalAdvice {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public NotificacionGlobalAdvice(
            NotificacionService notificacionService,
            UsuarioRepository usuarioRepository) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("contadorNotificacionesNoLeidas")
    public long contadorNotificacionesNoLeidas() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return 0L;
        }

        return usuarioRepository.findByUsername(authentication.getName())
                .map(Usuario::getId)
                .map(notificacionService::contarNoLeidas)
                .orElse(0L);
    }

    @ModelAttribute("ultimasNotificacionesNoLeidas")
    public List<NotificacionResponse> ultimasNotificacionesNoLeidas() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return List.of();
        }

        return usuarioRepository.findByUsername(authentication.getName())
                .map(Usuario::getId)
                .map(id -> notificacionService.obtenerUltimasNoLeidas(id, 5))
                .orElse(List.of());
    }
}
