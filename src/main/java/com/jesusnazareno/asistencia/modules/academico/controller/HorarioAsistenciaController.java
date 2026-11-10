package com.jesusnazareno.asistencia.modules.academico.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService;
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
@RequestMapping("/academico/horarios")
public class HorarioAsistenciaController {

    private final HorarioAsistenciaService horarioAsistenciaService;
    private final AnioEscolarService anioEscolarService;
    private final GradoService gradoService;
    private final SeccionService seccionService;
    private final MessageSource messageSource;

    public HorarioAsistenciaController(HorarioAsistenciaService horarioAsistenciaService,
                                      AnioEscolarService anioEscolarService,
                                      GradoService gradoService,
                                      SeccionService seccionService,
                                      MessageSource messageSource) {
        this.horarioAsistenciaService = horarioAsistenciaService;
        this.anioEscolarService = anioEscolarService;
        this.gradoService = gradoService;
        this.seccionService = seccionService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('HORARIO_VER')")
    public String listar(
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId,
            @RequestParam(name = "turno", required = false) Turno turno,
            @RequestParam(name = "estado", defaultValue = "TODOS") String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pagina, tamano);

        Page<HorarioAsistenciaResponse> horarios = horarioAsistenciaService.listarPaginado(
                anioEscolarId, turno, estado, pageable
        );

        model.addAttribute("horarios", horarios.getContent());
        model.addAttribute("anioEscolarId", anioEscolarId);
        model.addAttribute("turnoSeleccionado", turno);
        model.addAttribute("estado", estado);
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("paginaActual", horarios.getNumber());
        model.addAttribute("totalPaginas", horarios.getTotalPages());
        model.addAttribute("totalElementos", horarios.getTotalElements());
        model.addAttribute("currentMenu", "horarios");

        return "academico/horarios/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAuthority('HORARIO_CREAR')")
    public String nuevo(Model model) {
        if (!model.containsAttribute("horarioRequest")) {
            HorarioAsistenciaRequest req = new HorarioAsistenciaRequest();
            req.setMinutosTolerancia(10);
            model.addAttribute("horarioRequest", req);
        }
        cargarCatalogos(model);
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Nuevo Horario de Asistencia");
        model.addAttribute("currentMenu", "horarios");
        return "academico/horarios/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('HORARIO_CREAR')")
    public String crear(
            @Valid @ModelAttribute("horarioRequest") HorarioAsistenciaRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        validarFormulario(request, bindingResult);

        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Horario de Asistencia");
            model.addAttribute("currentMenu", "horarios");
            return "academico/horarios/form";
        }

        try {
            horarioAsistenciaService.crearHorario(request);
            String mensaje = messageSource.getMessage("horario.creado", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/horarios";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            cargarCatalogos(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Nuevo Horario de Asistencia");
            model.addAttribute("currentMenu", "horarios");
            return "academico/horarios/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HORARIO_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        HorarioAsistenciaResponse horario = horarioAsistenciaService.buscarPorId(id);
        model.addAttribute("horario", horario);
        model.addAttribute("currentMenu", "horarios");
        return "academico/horarios/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('HORARIO_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        HorarioAsistenciaResponse horario = horarioAsistenciaService.buscarPorId(id);

        if (!model.containsAttribute("horarioRequest")) {
            HorarioAsistenciaRequest request = new HorarioAsistenciaRequest(
                    horario.getAnioEscolarId(),
                    horario.getTurno(),
                    horario.getGradoId(),
                    horario.getSeccionId(),
                    horario.getHoraEntrada(),
                    horario.getMinutosTolerancia(),
                    horario.getHoraSalida(),
                    horario.getFechaInicio(),
                    horario.getFechaFin()
            );
            model.addAttribute("horarioRequest", request);
        }
        cargarCatalogos(model);
        model.addAttribute("isEdit", true);
        model.addAttribute("horarioId", id);
        model.addAttribute("pageTitle", "Editar Horario de Asistencia");
        model.addAttribute("currentMenu", "horarios");
        return "academico/horarios/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('HORARIO_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("horarioRequest") HorarioAsistenciaRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        validarFormulario(request, bindingResult);

        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("horarioId", id);
            model.addAttribute("pageTitle", "Editar Horario de Asistencia");
            model.addAttribute("currentMenu", "horarios");
            return "academico/horarios/form";
        }

        try {
            horarioAsistenciaService.actualizarHorario(id, request);
            String mensaje = messageSource.getMessage("horario.actualizado", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
            return "redirect:/academico/horarios";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            cargarCatalogos(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("horarioId", id);
            model.addAttribute("pageTitle", "Editar Horario de Asistencia");
            model.addAttribute("currentMenu", "horarios");
            return "academico/horarios/form";
        }
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('HORARIO_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            horarioAsistenciaService.activarHorario(id);
            String mensaje = messageSource.getMessage("horario.activado", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", mensaje);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/academico/horarios";
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('HORARIO_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        horarioAsistenciaService.desactivarHorario(id);
        String mensaje = messageSource.getMessage("horario.desactivado", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", mensaje);
        return "redirect:/academico/horarios";
    }

    private void validarFormulario(HorarioAsistenciaRequest request, BindingResult bindingResult) {
        if (request.getFechaFin() != null && request.getFechaInicio() != null
                && request.getFechaFin().isBefore(request.getFechaInicio())) {
            bindingResult.rejectValue("fechaFin", "horario.fechas-invalidas",
                    "El periodo de vigencia no es válido: la fecha de fin no puede ser anterior a la de inicio.");
        }

        if (request.getHoraSalida() != null && request.getHoraEntrada() != null
                && !request.getHoraSalida().isAfter(request.getHoraEntrada())) {
            bindingResult.rejectValue("horaSalida", "horario.horas-invalidas",
                    "La hora de salida debe ser posterior a la hora de entrada.");
        }
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("grados", gradoService.listarTodos());
        model.addAttribute("secciones", seccionService.listarTodas());
    }
}
