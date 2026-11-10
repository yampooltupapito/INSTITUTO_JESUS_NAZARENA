package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
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
@RequestMapping("/academico/anios")
public class AnioEscolarController {

    private final AnioEscolarService anioEscolarService;
    private final AnioEscolarRepository anioEscolarRepository;
    private final MessageSource messageSource;

    public AnioEscolarController(AnioEscolarService anioEscolarService,
                                 AnioEscolarRepository anioEscolarRepository,
                                 MessageSource messageSource) {
        this.anioEscolarService = anioEscolarService;
        this.anioEscolarRepository = anioEscolarRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ACADEMICO_VER')")
    public String listar(
            @RequestParam(name = "estado", defaultValue = "TODOS") String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pagina, tamano);

        Page<AnioEscolarResponse> anios = anioEscolarService.listarPaginado(estado, pageable);

        model.addAttribute("anios", anios.getContent());
        model.addAttribute("estado", estado);
        model.addAttribute("paginaActual", anios.getNumber());
        model.addAttribute("totalPaginas", anios.getTotalPages());
        model.addAttribute("totalElementos", anios.getTotalElements());
        model.addAttribute("currentMenu", "anios");

        return "academico/anios/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAuthority('ACADEMICO_CREAR')")
    public String nuevo(Model model) {
        if (!model.containsAttribute("anioRequest")) {
            model.addAttribute("anioRequest", new AnioEscolarRequest());
        }
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Nuevo Año Escolar");
        model.addAttribute("currentMenu", "anios");
        return "academico/anios/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMICO_CREAR')")
    public String crear(
            @Valid @ModelAttribute("anioRequest") AnioEscolarRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (request.getFechaFin() != null && request.getFechaInicio() != null
                && request.getFechaFin().isBefore(request.getFechaInicio())) {
            bindingResult.rejectValue("fechaFin", "academico.anio.fechas-invalidas",
                    "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        if (request.getAnio() != null && anioEscolarRepository.existsByAnio(request.getAnio())) {
            bindingResult.rejectValue("anio", "academico.anio.duplicado",
                    "El año escolar ya existe.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Año Escolar");
            model.addAttribute("currentMenu", "anios");
            return "academico/anios/form";
        }

        try {
            anioEscolarService.crearAnioEscolar(request);
            String mensaje = messageSource.getMessage("academico.anio.creado", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/anios";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Año Escolar");
            model.addAttribute("currentMenu", "anios");
            return "academico/anios/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        AnioEscolarResponse anio = anioEscolarService.buscarPorId(id);
        model.addAttribute("anio", anio);
        model.addAttribute("currentMenu", "anios");
        return "academico/anios/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        AnioEscolarResponse anio = anioEscolarService.buscarPorId(id);

        if (!model.containsAttribute("anioRequest")) {
            AnioEscolarRequest request = new AnioEscolarRequest(
                    anio.getAnio(),
                    anio.getFechaInicio(),
                    anio.getFechaFin(),
                    anio.getNombre()
            );
            model.addAttribute("anioRequest", request);
        }
        model.addAttribute("isEdit", true);
        model.addAttribute("anioId", id);
        model.addAttribute("pageTitle", "Editar Año Escolar");
        model.addAttribute("currentMenu", "anios");
        return "academico/anios/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("anioRequest") AnioEscolarRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (request.getFechaFin() != null && request.getFechaInicio() != null
                && request.getFechaFin().isBefore(request.getFechaInicio())) {
            bindingResult.rejectValue("fechaFin", "academico.anio.fechas-invalidas",
                    "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        anioEscolarRepository.findByAnio(request.getAnio()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                bindingResult.rejectValue("anio", "academico.anio.duplicado",
                        "El año escolar ya existe.");
            }
        });

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("anioId", id);
            model.addAttribute("pageTitle", "Editar Año Escolar");
            model.addAttribute("currentMenu", "anios");
            return "academico/anios/form";
        }

        try {
            anioEscolarService.actualizarAnioEscolar(id, request);
            String mensaje = messageSource.getMessage("academico.anio.actualizado", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/anios";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("anioId", id);
            model.addAttribute("pageTitle", "Editar Año Escolar");
            model.addAttribute("currentMenu", "anios");
            return "academico/anios/form";
        }
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        anioEscolarService.activarAnioEscolar(id);
        String mensaje = messageSource.getMessage("academico.anio.activado", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", mensaje);
        return "redirect:/academico/anios";
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        anioEscolarService.desactivarAnioEscolar(id);
        String mensaje = messageSource.getMessage("academico.anio.desactivado", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", mensaje);
        return "redirect:/academico/anios";
    }
}
