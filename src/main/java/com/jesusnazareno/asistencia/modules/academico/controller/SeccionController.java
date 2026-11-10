package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Controller
@RequestMapping("/academico/secciones")
public class SeccionController {

    private final SeccionService seccionService;
    private final GradoService gradoService;
    private final SeccionRepository seccionRepository;
    private final MessageSource messageSource;

    public SeccionController(SeccionService seccionService,
                             GradoService gradoService,
                             SeccionRepository seccionRepository,
                             MessageSource messageSource) {
        this.seccionService = seccionService;
        this.gradoService = gradoService;
        this.seccionRepository = seccionRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ACADEMICO_VER')")
    public String listar(
            @RequestParam(name = "nivel", required = false) NivelEducativo nivel,
            @RequestParam(name = "gradoId", required = false) Long gradoId,
            @RequestParam(name = "turno", required = false) Turno turno,
            @RequestParam(name = "estado", defaultValue = "TODOS") String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pagina, tamano);

        Page<SeccionResponse> secciones = seccionService.buscarPaginado(nivel, gradoId, turno, estado, pageable);

        model.addAttribute("secciones", secciones.getContent());
        model.addAttribute("nivelSeleccionado", nivel);
        model.addAttribute("gradoIdSeleccionado", gradoId);
        model.addAttribute("turnoSeleccionado", turno);
        model.addAttribute("estado", estado);
        model.addAttribute("niveles", NivelEducativo.values());
        model.addAttribute("grados", gradoService.listarActivos());
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("paginaActual", secciones.getNumber());
        model.addAttribute("totalPaginas", secciones.getTotalPages());
        model.addAttribute("totalElementos", secciones.getTotalElements());
        model.addAttribute("currentMenu", "secciones");

        return "academico/secciones/list";
    }

    @GetMapping("/nueva")
    @PreAuthorize("hasAuthority('ACADEMICO_CREAR')")
    public String nueva(Model model) {
        if (!model.containsAttribute("seccionRequest")) {
            model.addAttribute("seccionRequest", new SeccionRequest());
        }
        model.addAttribute("grados", gradoService.listarActivos());
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Nueva Sección");
        model.addAttribute("currentMenu", "secciones");
        return "academico/secciones/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMICO_CREAR')")
    public String crear(
            @Valid @ModelAttribute("seccionRequest") SeccionRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (request.getGradoId() != null && request.getNombre() != null
                && seccionRepository.existsByGradoIdAndNombre(request.getGradoId(), request.getNombre().trim())) {
            bindingResult.rejectValue("nombre", "academico.seccion.duplicada",
                    "Ya existe una sección con ese nombre para el grado seleccionado.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("grados", gradoService.listarActivos());
            model.addAttribute("turnos", Turno.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Sección");
            model.addAttribute("currentMenu", "secciones");
            return "academico/secciones/form";
        }

        try {
            seccionService.crearSeccion(request);
            String mensaje = messageSource.getMessage("academico.seccion.creada", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/secciones";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("grados", gradoService.listarActivos());
            model.addAttribute("turnos", Turno.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nueva Sección");
            model.addAttribute("currentMenu", "secciones");
            return "academico/secciones/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        SeccionResponse seccion = seccionService.buscarPorId(id);
        model.addAttribute("seccion", seccion);
        model.addAttribute("currentMenu", "secciones");
        return "academico/secciones/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        SeccionResponse seccion = seccionService.buscarPorId(id);

        if (!model.containsAttribute("seccionRequest")) {
            SeccionRequest request = new SeccionRequest(
                    seccion.getGradoId(),
                    seccion.getNombre(),
                    seccion.getTurno(),
                    seccion.getCapacidad()
            );
            model.addAttribute("seccionRequest", request);
        }
        model.addAttribute("grados", gradoService.listarTodos());
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("seccionId", id);
        model.addAttribute("pageTitle", "Editar Sección");
        model.addAttribute("currentMenu", "secciones");
        return "academico/secciones/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("seccionRequest") SeccionRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (request.getGradoId() != null && request.getNombre() != null) {
            seccionRepository.findByGradoIdAndNombre(request.getGradoId(), request.getNombre().trim()).ifPresent(existente -> {
                if (!existente.getId().equals(id)) {
                    bindingResult.rejectValue("nombre", "academico.seccion.duplicada",
                            "Ya existe una sección con ese nombre para el grado seleccionado.");
                }
            });
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("grados", gradoService.listarTodos());
            model.addAttribute("turnos", Turno.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("seccionId", id);
            model.addAttribute("pageTitle", "Editar Sección");
            model.addAttribute("currentMenu", "secciones");
            return "academico/secciones/form";
        }

        try {
            seccionService.actualizarSeccion(id, request);
            String mensaje = messageSource.getMessage("academico.seccion.actualizada", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/secciones";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("grados", gradoService.listarTodos());
            model.addAttribute("turnos", Turno.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("seccionId", id);
            model.addAttribute("pageTitle", "Editar Sección");
            model.addAttribute("currentMenu", "secciones");
            return "academico/secciones/form";
        }
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        seccionService.activarSeccion(id);
        String mensaje = messageSource.getMessage("academico.seccion.activada", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", mensaje);
        return "redirect:/academico/secciones";
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        seccionService.desactivarSeccion(id);
        String mensaje = messageSource.getMessage("academico.seccion.desactivada", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", mensaje);
        return "redirect:/academico/secciones";
    }
}
