package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoResponse;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
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

import java.util.List;

@Controller
@RequestMapping("/academico/grados")
public class GradoController {

    private final GradoService gradoService;
    private final GradoRepository gradoRepository;
    private final MessageSource messageSource;

    public GradoController(GradoService gradoService,
                           GradoRepository gradoRepository,
                           MessageSource messageSource) {
        this.gradoService = gradoService;
        this.gradoRepository = gradoRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ACADEMICO_VER')")
    public String listar(
            @RequestParam(name = "nivel", required = false) NivelEducativo nivel,
            @RequestParam(name = "estado", defaultValue = "TODOS") String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pagina, tamano);

        Page<GradoResponse> grados = gradoService.listarPaginado(nivel, estado, pageable);

        model.addAttribute("grados", grados.getContent());
        model.addAttribute("nivelSeleccionado", nivel);
        model.addAttribute("estado", estado);
        model.addAttribute("niveles", NivelEducativo.values());
        model.addAttribute("paginaActual", grados.getNumber());
        model.addAttribute("totalPaginas", grados.getTotalPages());
        model.addAttribute("totalElementos", grados.getTotalElements());
        model.addAttribute("currentMenu", "grados");

        return "academico/grados/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAuthority('ACADEMICO_CREAR')")
    public String nuevo(Model model) {
        if (!model.containsAttribute("gradoRequest")) {
            model.addAttribute("gradoRequest", new GradoRequest());
        }
        model.addAttribute("niveles", NivelEducativo.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Nuevo Grado");
        model.addAttribute("currentMenu", "grados");
        return "academico/grados/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMICO_CREAR')")
    public String crear(
            @Valid @ModelAttribute("gradoRequest") GradoRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (request.getNivel() != null && request.getNumero() != null
                && gradoRepository.existsByNivelAndNumero(request.getNivel(), request.getNumero())) {
            bindingResult.rejectValue("numero", "academico.grado.duplicado",
                    "El grado ya se encuentra registrado para el nivel seleccionado.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("niveles", NivelEducativo.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Grado");
            model.addAttribute("currentMenu", "grados");
            return "academico/grados/form";
        }

        try {
            gradoService.crearGrado(request);
            String mensaje = messageSource.getMessage("academico.grado.creado", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/grados";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("niveles", NivelEducativo.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Grado");
            model.addAttribute("currentMenu", "grados");
            return "academico/grados/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        GradoResponse grado = gradoService.buscarPorId(id);
        List<SeccionResponse> secciones = gradoService.obtenerSeccionesPorGrado(id);

        model.addAttribute("grado", grado);
        model.addAttribute("secciones", secciones);
        model.addAttribute("currentMenu", "grados");
        return "academico/grados/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        GradoResponse grado = gradoService.buscarPorId(id);

        if (!model.containsAttribute("gradoRequest")) {
            GradoRequest request = new GradoRequest(
                    grado.getNivel(),
                    grado.getNumero(),
                    grado.getNombre(),
                    grado.getDescripcion()
            );
            model.addAttribute("gradoRequest", request);
        }
        model.addAttribute("niveles", NivelEducativo.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("gradoId", id);
        model.addAttribute("pageTitle", "Editar Grado");
        model.addAttribute("currentMenu", "grados");
        return "academico/grados/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("gradoRequest") GradoRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (request.getNivel() != null && request.getNumero() != null) {
            gradoRepository.findByNivelAndNumero(request.getNivel(), request.getNumero()).ifPresent(existente -> {
                if (!existente.getId().equals(id)) {
                    bindingResult.rejectValue("numero", "academico.grado.duplicado",
                            "El grado ya se encuentra registrado para el nivel seleccionado.");
                }
            });
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("niveles", NivelEducativo.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("gradoId", id);
            model.addAttribute("pageTitle", "Editar Grado");
            model.addAttribute("currentMenu", "grados");
            return "academico/grados/form";
        }

        try {
            gradoService.actualizarGrado(id, request);
            String mensaje = messageSource.getMessage("academico.grado.actualizado", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/grados";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("niveles", NivelEducativo.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("gradoId", id);
            model.addAttribute("pageTitle", "Editar Grado");
            model.addAttribute("currentMenu", "grados");
            return "academico/grados/form";
        }
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        gradoService.activarGrado(id);
        String mensaje = messageSource.getMessage("academico.grado.activado", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", mensaje);
        return "redirect:/academico/grados";
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('ACADEMICO_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        gradoService.desactivarGrado(id);
        String mensaje = messageSource.getMessage("academico.grado.desactivado", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", mensaje);
        return "redirect:/academico/grados";
    }
}
