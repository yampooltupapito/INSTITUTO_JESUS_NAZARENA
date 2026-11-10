package com.jesusnazareno.asistencia.modules.asistencia.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.RevisionJustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.service.AsistenciaService;
import com.jesusnazareno.asistencia.modules.asistencia.service.JustificacionService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Controlador Web Thymeleaf para el módulo de Gestión de Justificaciones de Asistencia.
 */
@Controller
@RequestMapping("/justificaciones")
public class JustificacionController {

    private final JustificacionService justificacionService;
    private final AsistenciaService asistenciaService;
    private final AnioEscolarService anioEscolarService;
    private final GradoService gradoService;
    private final SeccionService seccionService;
    private final UsuarioRepository usuarioRepository;
    private final MessageSource messageSource;

    public JustificacionController(JustificacionService justificacionService,
                                   AsistenciaService asistenciaService,
                                   AnioEscolarService anioEscolarService,
                                   GradoService gradoService,
                                   SeccionService seccionService,
                                   UsuarioRepository usuarioRepository,
                                   MessageSource messageSource) {
        this.justificacionService = justificacionService;
        this.asistenciaService = asistenciaService;
        this.anioEscolarService = anioEscolarService;
        this.gradoService = gradoService;
        this.seccionService = seccionService;
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
    }

    /**
     * Consulta y listado paginado de justificaciones con filtros avanzados.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('JUSTIFICACION_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "estado", required = false) EstadoJustificacion estado,
            @RequestParam(name = "fechaSolicitud", required = false) LocalDate fechaSolicitud,
            @RequestParam(name = "fechaAsistencia", required = false) LocalDate fechaAsistencia,
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId,
            @RequestParam(name = "gradoId", required = false) Long gradoId,
            @RequestParam(name = "seccionId", required = false) Long seccionId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pagina, tamano,
                Sort.by(Sort.Direction.DESC, "fechaSolicitud").and(Sort.by(Sort.Direction.DESC, "id")));

        Page<JustificacionResponse> justificaciones = justificacionService.buscarJustificaciones(
                q, estado, fechaSolicitud, fechaAsistencia, anioEscolarId, gradoId, seccionId, pageable);

        model.addAttribute("justificaciones", justificaciones);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("estado", estado);
        model.addAttribute("fechaSolicitud", fechaSolicitud);
        model.addAttribute("fechaAsistencia", fechaAsistencia);
        model.addAttribute("anioEscolarId", anioEscolarId);
        model.addAttribute("gradoId", gradoId);
        model.addAttribute("seccionId", seccionId);
        model.addAttribute("page", pagina);
        model.addAttribute("size", tamano);

        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("grados", gradoService.listarTodos());
        if (gradoId != null) {
            model.addAttribute("secciones", seccionService.listarPorGrado(gradoId));
        } else {
            model.addAttribute("secciones", Collections.emptyList());
        }
        model.addAttribute("estados", EstadoJustificacion.values());
        model.addAttribute("currentMenu", "justificaciones");
        model.addAttribute("pageTitle", "Gestión de Justificaciones");

        return "justificaciones/list";
    }

    /**
     * Muestra el formulario para registrar una nueva justificación vinculada a una asistencia válida.
     */
    @GetMapping("/nueva")
    @PreAuthorize("hasAuthority('JUSTIFICACION_CREAR')")
    public String nueva(
            @RequestParam(name = "asistenciaId", required = false) Long asistenciaId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Locale locale = LocaleContextHolder.getLocale();
        if (asistenciaId == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("justificacion.asistencia-requerida", null,
                            "Debe seleccionar una asistencia para justificar.", locale));
            return "redirect:/asistencias";
        }

        AsistenciaResponse asistencia;
        try {
            asistencia = asistenciaService.buscarPorId(asistenciaId);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("asistencia.not-found", null, "Asistencia no encontrada.", locale));
            return "redirect:/asistencias";
        }

        // Validar que el estado admita justificación (solo AUSENTE o TARDANZA)
        if (asistencia.getEstado() != EstadoAsistencia.AUSENTE && asistencia.getEstado() != EstadoAsistencia.TARDANZA) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("justificacion.asistencia-invalida", null,
                            "Esta asistencia no admite justificación.", locale));
            return "redirect:/asistencias/" + asistenciaId;
        }

        // Validar si ya existe justificación activa
        if (justificacionService.buscarActivaPorAsistencia(asistenciaId).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("justificacion.duplicada", null,
                            "La asistencia ya tiene una justificación activa.", locale));
            return "redirect:/asistencias/" + asistenciaId;
        }

        JustificacionRequest form = new JustificacionRequest();
        form.setAsistenciaId(asistenciaId);

        model.addAttribute("form", form);
        model.addAttribute("asistencia", asistencia);
        model.addAttribute("currentMenu", "justificaciones");
        model.addAttribute("pageTitle", "Nueva Justificación");

        return "justificaciones/form";
    }

    /**
     * Procesa el formulario de registro de justificación.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('JUSTIFICACION_CREAR')")
    public String crear(
            @Valid @ModelAttribute("form") JustificacionRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Locale locale = LocaleContextHolder.getLocale();

        if (bindingResult.hasErrors()) {
            if (form.getAsistenciaId() != null) {
                try {
                    model.addAttribute("asistencia", asistenciaService.buscarPorId(form.getAsistenciaId()));
                } catch (Exception ignored) {
                }
            }
            model.addAttribute("currentMenu", "justificaciones");
            model.addAttribute("pageTitle", "Nueva Justificación");
            return "justificaciones/form";
        }

        try {
            JustificacionResponse response = justificacionService.crear(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("justificacion.creada", null,
                            "Justificación registrada correctamente.", locale));
            return "redirect:/justificaciones/" + response.getId();
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            if (form.getAsistenciaId() != null) {
                return "redirect:/asistencias/" + form.getAsistenciaId();
            }
            return "redirect:/asistencias";
        }
    }

    /**
     * Muestra el detalle completo de una justificación y su contexto.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('JUSTIFICACION_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        JustificacionResponse justificacion = justificacionService.buscarPorId(id);
        model.addAttribute("justificacion", justificacion);

        try {
            AsistenciaResponse asistencia = asistenciaService.buscarPorId(justificacion.getAsistenciaId());
            model.addAttribute("asistencia", asistencia);
        } catch (Exception ignored) {
        }

        model.addAttribute("currentMenu", "justificaciones");
        model.addAttribute("pageTitle", "Detalle de Justificación");
        return "justificaciones/detail";
    }

    /**
     * Muestra la pantalla de revisión de una justificación pendiente.
     */
    @GetMapping("/{id}/revisar")
    @PreAuthorize("hasAuthority('JUSTIFICACION_REVISAR')")
    public String revisar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Locale locale = LocaleContextHolder.getLocale();
        JustificacionResponse justificacion = justificacionService.buscarPorId(id);

        if (justificacion.getEstado() != EstadoJustificacion.PENDIENTE) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("justificacion.estado-invalido", null,
                            "La justificación no se encuentra en un estado válido para esta operación.", locale));
            return "redirect:/justificaciones/" + id;
        }

        try {
            AsistenciaResponse asistencia = asistenciaService.buscarPorId(justificacion.getAsistenciaId());
            model.addAttribute("asistencia", asistencia);
        } catch (Exception ignored) {
        }

        RevisionJustificacionRequest form = new RevisionJustificacionRequest();
        model.addAttribute("justificacion", justificacion);
        model.addAttribute("form", form);
        model.addAttribute("currentMenu", "justificaciones");
        model.addAttribute("pageTitle", "Revisar Justificación");
        return "justificaciones/review";
    }

    /**
     * Procesa la aprobación de una justificación:
     * Transición PENDIENTE -> APROBADA y Asistencia -> JUSTIFICADO dentro de la misma transacción.
     */
    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasAuthority('JUSTIFICACION_REVISAR')")
    public String aprobar(
            @PathVariable("id") Long id,
            @RequestParam(name = "observacionRevision", required = false) String observacionRevision,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Locale locale = LocaleContextHolder.getLocale();
        String username = authentication.getName();
        Usuario revisor = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario autenticado no encontrado."));

        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(revisor.getId())
                .observacionRevision(observacionRevision)
                .build();

        try {
            justificacionService.aprobar(id, revision);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("justificacion.aprobada", null,
                            "Justificación aprobada correctamente.", locale));
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/justificaciones/" + id;
    }

    /**
     * Procesa el rechazo de una justificación:
     * Transición PENDIENTE -> RECHAZADA. La Asistencia conserva su estado original (AUSENTE/TARDANZA).
     */
    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasAuthority('JUSTIFICACION_REVISAR')")
    public String rechazar(
            @PathVariable("id") Long id,
            @RequestParam(name = "observacionRevision", required = false) String observacionRevision,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Locale locale = LocaleContextHolder.getLocale();
        String username = authentication.getName();
        Usuario revisor = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario autenticado no encontrado."));

        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(revisor.getId())
                .observacionRevision(observacionRevision)
                .build();

        try {
            justificacionService.rechazar(id, revision);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("justificacion.rechazada", null,
                            "Justificación rechazada correctamente.", locale));
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/justificaciones/" + id;
    }

    @GetMapping("/secciones")
    @PreAuthorize("hasAuthority('JUSTIFICACION_VER')")
    @ResponseBody
    public List<?> obtenerSeccionesPorGrado(@RequestParam("gradoId") Long gradoId) {
        return seccionService.listarPorGrado(gradoId);
    }
}
