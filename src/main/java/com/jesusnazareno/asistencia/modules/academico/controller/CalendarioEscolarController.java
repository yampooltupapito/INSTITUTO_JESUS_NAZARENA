package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

/**
 * Controlador web para la gestión de excepciones y fechas especiales del calendario escolar.
 */
@Controller
@RequestMapping("/academico/calendario")
public class CalendarioEscolarController {

    private final CalendarioEscolarService calendarioEscolarService;
    private final AnioEscolarService anioEscolarService;
    private final MessageSource messageSource;

    public CalendarioEscolarController(CalendarioEscolarService calendarioEscolarService,
                                      AnioEscolarService anioEscolarService,
                                      MessageSource messageSource) {
        this.calendarioEscolarService = calendarioEscolarService;
        this.anioEscolarService = anioEscolarService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CALENDARIO_VER')")
    public String listar(
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId,
            @RequestParam(name = "tipoDia", required = false) TipoDiaCalendario tipoDia,
            @RequestParam(name = "esLectivo", required = false) Boolean esLectivo,
            @RequestParam(name = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pagina, tamano);

        Page<CalendarioEscolarResponse> fechas = calendarioEscolarService.listar(
                anioEscolarId, tipoDia, esLectivo, fechaInicio, fechaFin, pageable
        );

        model.addAttribute("fechas", fechas.getContent());
        model.addAttribute("anioEscolarId", anioEscolarId);
        model.addAttribute("tipoDiaSeleccionado", tipoDia);
        model.addAttribute("esLectivo", esLectivo);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("tiposDia", TipoDiaCalendario.values());
        model.addAttribute("paginaActual", fechas.getNumber());
        model.addAttribute("totalPaginas", fechas.getTotalPages());
        model.addAttribute("totalElementos", fechas.getTotalElements());
        model.addAttribute("currentMenu", "calendario");

        return "academico/calendario/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAuthority('CALENDARIO_CREAR')")
    public String nuevo(Model model) {
        if (!model.containsAttribute("calendarioRequest")) {
            CalendarioEscolarRequest req = new CalendarioEscolarRequest();
            req.setEsLectivo(false);
            model.addAttribute("calendarioRequest", req);
        }
        cargarCatalogos(model);
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Registrar Fecha Especial");
        model.addAttribute("currentMenu", "calendario");
        return "academico/calendario/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CALENDARIO_CREAR')")
    public String crear(
            @Valid @ModelAttribute("calendarioRequest") CalendarioEscolarRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Registrar Fecha Especial");
            model.addAttribute("currentMenu", "calendario");
            return "academico/calendario/form";
        }

        try {
            calendarioEscolarService.crear(request);
            String mensaje = messageSource.getMessage("calendario.creado", null, "Fecha especial registrada correctamente.", LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/calendario";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            cargarCatalogos(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Registrar Fecha Especial");
            model.addAttribute("currentMenu", "calendario");
            return "academico/calendario/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CALENDARIO_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        CalendarioEscolarResponse fecha = calendarioEscolarService.buscarPorId(id);
        model.addAttribute("calendario", fecha);
        model.addAttribute("currentMenu", "calendario");
        return "academico/calendario/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('CALENDARIO_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        CalendarioEscolarResponse fecha = calendarioEscolarService.buscarPorId(id);

        if (!model.containsAttribute("calendarioRequest")) {
            CalendarioEscolarRequest request = new CalendarioEscolarRequest(
                    fecha.getAnioEscolarId(),
                    fecha.getFecha(),
                    fecha.getTipoDia(),
                    fecha.getDescripcion(),
                    fecha.getEsLectivo()
            );
            model.addAttribute("calendarioRequest", request);
        }
        cargarCatalogos(model);
        model.addAttribute("isEdit", true);
        model.addAttribute("calendarioId", id);
        model.addAttribute("pageTitle", "Editar Fecha del Calendario");
        model.addAttribute("currentMenu", "calendario");
        return "academico/calendario/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('CALENDARIO_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("calendarioRequest") CalendarioEscolarRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("calendarioId", id);
            model.addAttribute("pageTitle", "Editar Fecha del Calendario");
            model.addAttribute("currentMenu", "calendario");
            return "academico/calendario/form";
        }

        try {
            calendarioEscolarService.actualizar(id, request);
            String mensaje = messageSource.getMessage("calendario.actualizado", null, "Calendario escolar actualizado correctamente.", LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/calendario";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            cargarCatalogos(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("calendarioId", id);
            model.addAttribute("pageTitle", "Editar Fecha del Calendario");
            model.addAttribute("currentMenu", "calendario");
            return "academico/calendario/form";
        }
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('CALENDARIO_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            calendarioEscolarService.activar(id);
            String mensaje = messageSource.getMessage("calendario.activado", null, "Fecha de calendario activada correctamente.", LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/academico/calendario";
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('CALENDARIO_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            calendarioEscolarService.desactivar(id);
            String mensaje = messageSource.getMessage("calendario.desactivado", null, "Fecha de calendario desactivada correctamente.", LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/academico/calendario";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("tiposDia", TipoDiaCalendario.values());
    }
}
