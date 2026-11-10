package com.jesusnazareno.asistencia.modules.notificacion.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;
import com.jesusnazareno.asistencia.modules.notificacion.dto.NotificacionResponse;
import com.jesusnazareno.asistencia.modules.notificacion.service.NotificacionService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controlador web para la gestión de notificaciones internas del usuario autenticado.
 */
@Controller
@RequestMapping("/notificaciones")
@PreAuthorize("isAuthenticated()")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;
    private final MessageSource messageSource;

    public NotificacionController(
            NotificacionService notificacionService,
            UsuarioRepository usuarioRepository,
            MessageSource messageSource) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
    }

    /**
     * Listado paginado de notificaciones del usuario autenticado con filtros.
     */
    @GetMapping
    public String listar(
            @RequestParam(name = "estadoLectura", required = false, defaultValue = "TODAS") String estadoLectura,
            @RequestParam(name = "tipo", required = false) TipoNotificacion tipo,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication authentication,
            Model model) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        Boolean filtroLeida = null;
        if ("NO_LEIDAS".equalsIgnoreCase(estadoLectura)) {
            filtroLeida = false;
        } else if ("LEIDAS".equalsIgnoreCase(estadoLectura)) {
            filtroLeida = true;
        }

        int pageSize = Math.min(Math.max(size, 1), 100);
        int pageNumber = Math.max(page, 0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<NotificacionResponse> notificaciones = notificacionService.listarDelUsuario(
                usuario.getId(), filtroLeida, tipo, pageable);

        long noLeidas = notificacionService.contarNoLeidas(usuario.getId());

        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("tiposNotificacion", TipoNotificacion.values());
        model.addAttribute("estadoLectura", estadoLectura.toUpperCase());
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("totalNoLeidas", noLeidas);

        return "notificaciones/list";
    }

    /**
     * Detalle de una notificación y marcado como leída.
     */
    @GetMapping("/{id}")
    public String verDetalle(
            @PathVariable("id") Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        try {
            // Marca como leída de forma idempotente al abrir el detalle
            NotificacionResponse notificacion = notificacionService.marcarComoLeida(id, usuario.getId());
            model.addAttribute("notificacion", notificacion);
            return "notificaciones/detail";
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/notificaciones";
        }
    }

    /**
     * Marcar una notificación individual como leída.
     */
    @PostMapping("/{id}/leer")
    public String marcarComoLeida(
            @PathVariable("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Locale locale = LocaleContextHolder.getLocale();
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        try {
            notificacionService.marcarComoLeida(id, usuario.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("notificacion.leida", null, "Notificación marcada como leída.", locale));
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/notificaciones";
    }

    /**
     * Marcar todas las notificaciones del usuario autenticado como leídas.
     */
    @PostMapping("/leer-todas")
    public String marcarTodasComoLeidas(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Locale locale = LocaleContextHolder.getLocale();
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        int actualizadas = notificacionService.marcarTodasComoLeidas(usuario.getId());
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("notificacion.todas-leidas", null,
                        "Todas las notificaciones fueron marcadas como leídas.", locale));

        return "redirect:/notificaciones";
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException("Usuario no autenticado.");
        }
        String username = authentication.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario autenticado no encontrado: " + username));
    }
}
