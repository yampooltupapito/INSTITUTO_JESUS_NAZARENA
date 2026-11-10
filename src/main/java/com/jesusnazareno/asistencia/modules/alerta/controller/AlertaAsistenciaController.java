package com.jesusnazareno.asistencia.modules.alerta.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.dto.AlertaAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ResolucionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.RevisionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.service.AlertaAsistenciaService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Controlador Web Thymeleaf para el módulo de Gestión de Alertas Internas de Asistencia.
 */
@Controller
@RequestMapping("/alertas")
public class AlertaAsistenciaController {

    private final AlertaAsistenciaService alertaService;
    private final AnioEscolarService anioEscolarService;
    private final GradoService gradoService;
    private final SeccionService seccionService;
    private final UsuarioRepository usuarioRepository;
    private final MessageSource messageSource;

    public AlertaAsistenciaController(
            AlertaAsistenciaService alertaService,
            AnioEscolarService anioEscolarService,
            GradoService gradoService,
            SeccionService seccionService,
            UsuarioRepository usuarioRepository,
            MessageSource messageSource) {
        this.alertaService = alertaService;
        this.anioEscolarService = anioEscolarService;
        this.gradoService = gradoService;
        this.seccionService = seccionService;
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
    }

    /**
     * Listado paginado de alertas con filtros.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ALERTA_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "tipo", required = false) TipoAlertaAsistencia tipo,
            @RequestParam(name = "estado", required = false) EstadoAlerta estado,
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId,
            @RequestParam(name = "gradoId", required = false) Long gradoId,
            @RequestParam(name = "seccionId", required = false) Long seccionId,
            @RequestParam(name = "fechaInicio", required = false) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) LocalDate fechaFin,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AlertaAsistenciaResponse> alertasPage = alertaService.buscarAlertas(
                q, tipo, estado, anioEscolarId, gradoId, seccionId, fechaInicio, fechaFin, pageable);

        model.addAttribute("alertas", alertasPage.getContent());
        model.addAttribute("page", alertasPage);
        model.addAttribute("q", q);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("anioEscolarId", anioEscolarId);
        model.addAttribute("gradoId", gradoId);
        model.addAttribute("seccionId", seccionId);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        model.addAttribute("tiposAlerta", TipoAlertaAsistencia.values());
        model.addAttribute("estadosAlerta", EstadoAlerta.values());
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("grados", gradoService.listarTodos());
        if (gradoId != null) {
            model.addAttribute("secciones", seccionService.listarPorGrado(gradoId));
        }

        return "alertas/list";
    }

    /**
     * Detalle de una alerta.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ALERTA_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        AlertaAsistenciaResponse alerta = alertaService.buscarPorId(id);
        model.addAttribute("alerta", alerta);
        return "alertas/detail";
    }

    /**
     * Transición PENDIENTE -> EN_REVISION.
     */
    @PostMapping("/{id}/revisar")
    @PreAuthorize("hasAuthority('ALERTA_REVISAR')")
    public String marcarEnRevision(
            @PathVariable("id") Long id,
            @RequestParam(name = "observacion", required = false) String observacion,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Locale locale = LocaleContextHolder.getLocale();
        Usuario revisor = obtenerUsuarioAutenticado(authentication);

        RevisionAlertaRequest request = new RevisionAlertaRequest(observacion);
        try {
            alertaService.marcarEnRevision(id, request, revisor.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("alerta.en-revision", null, "Alerta marcada en revisión.", locale));
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/alertas/" + id;
    }

    /**
     * Transición PENDIENTE/EN_REVISION -> RESUELTA.
     */
    @PostMapping("/{id}/resolver")
    @PreAuthorize("hasAuthority('ALERTA_REVISAR')")
    public String resolver(
            @PathVariable("id") Long id,
            @RequestParam(name = "observacion") String observacion,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Locale locale = LocaleContextHolder.getLocale();
        Usuario revisor = obtenerUsuarioAutenticado(authentication);

        ResolucionAlertaRequest request = new ResolucionAlertaRequest(observacion);
        try {
            alertaService.resolver(id, request, revisor.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("alerta.resuelta", null, "Alerta resuelta correctamente.", locale));
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/alertas/" + id;
    }

    /**
     * Operación administrativa para recalcular todas las alertas.
     */
    @PostMapping("/recalcular")
    @PreAuthorize("hasAuthority('ALERTA_CONFIGURAR') or hasAuthority('ALERTA_REVISAR')")
    public String recalcular(RedirectAttributes redirectAttributes) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            alertaService.recalcularTodasLasAlertas();
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("alerta.generada", null, "Alertas recalculadas exitosamente.", locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al recalcular alertas: " + ex.getMessage());
        }
        return "redirect:/alertas";
    }

    /**
     * Pantalla de configuración de umbrales.
     */
    @GetMapping("/configuracion")
    @PreAuthorize("hasAuthority('ALERTA_CONFIGURAR')")
    public String verConfiguracion(Model model) {
        List<ConfiguracionAlertaResponse> configuraciones = alertaService.listarConfiguraciones();
        model.addAttribute("configuraciones", configuraciones);
        model.addAttribute("tiposAlerta", TipoAlertaAsistencia.values());
        if (!model.containsAttribute("configuracionRequest")) {
            model.addAttribute("configuracionRequest", new ConfiguracionAlertaRequest());
        }
        return "alertas/configuracion";
    }

    /**
     * Actualización de umbral de alerta.
     */
    @PostMapping("/configuracion")
    @PreAuthorize("hasAuthority('ALERTA_CONFIGURAR')")
    public String actualizarConfiguracion(
            @Valid @ModelAttribute("configuracionRequest") ConfiguracionAlertaRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Locale locale = LocaleContextHolder.getLocale();
        if (bindingResult.hasErrors()) {
            model.addAttribute("configuraciones", alertaService.listarConfiguraciones());
            model.addAttribute("tiposAlerta", TipoAlertaAsistencia.values());
            return "alertas/configuracion";
        }

        try {
            alertaService.actualizarConfiguracion(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("alerta.configuracion.actualizada", null,
                            "Configuración de alertas actualizada correctamente.", locale));
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/alertas/configuracion";
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
