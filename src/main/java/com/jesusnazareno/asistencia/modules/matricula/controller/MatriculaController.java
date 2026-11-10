package com.jesusnazareno.asistencia.modules.matricula.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaResponse;
import com.jesusnazareno.asistencia.modules.matricula.dto.SeccionCapacidadDTO;
import com.jesusnazareno.asistencia.modules.matricula.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final EstudianteService estudianteService;
    private final AnioEscolarService anioEscolarService;
    private final GradoService gradoService;
    private final SeccionService seccionService;
    private final MessageSource messageSource;

    public MatriculaController(MatriculaService matriculaService,
                               EstudianteService estudianteService,
                               AnioEscolarService anioEscolarService,
                               GradoService gradoService,
                               SeccionService seccionService,
                               MessageSource messageSource) {
        this.matriculaService = matriculaService;
        this.estudianteService = estudianteService;
        this.anioEscolarService = anioEscolarService;
        this.gradoService = gradoService;
        this.seccionService = seccionService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId,
            @RequestParam(name = "gradoId", required = false) Long gradoId,
            @RequestParam(name = "seccionId", required = false) Long seccionId,
            @RequestParam(name = "estado", required = false) EstadoMatricula estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(
                pagina,
                tamano,
                Sort.by(Sort.Direction.DESC, "anioEscolar.anio")
                        .and(Sort.by(Sort.Direction.ASC, "grado.numero"))
                        .and(Sort.by(Sort.Direction.ASC, "seccion.nombre"))
                        .and(Sort.by(Sort.Direction.ASC, "estudiante.apellidoPaterno"))
        );

        Page<MatriculaResponse> matriculas = matriculaService.buscarMatriculas(
                q, anioEscolarId, gradoId, seccionId, estado, pageable
        );

        model.addAttribute("matriculas", matriculas);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("anioEscolarId", anioEscolarId);
        model.addAttribute("gradoId", gradoId);
        model.addAttribute("seccionId", seccionId);
        model.addAttribute("estado", estado);
        model.addAttribute("page", pagina);
        model.addAttribute("size", tamano);

        // Catálogos para filtros
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("grados", gradoService.listarTodos());
        if (gradoId != null) {
            model.addAttribute("secciones", seccionService.listarPorGrado(gradoId));
        } else {
            model.addAttribute("secciones", Collections.emptyList());
        }
        model.addAttribute("estados", EstadoMatricula.values());
        model.addAttribute("currentMenu", "matriculas");
        model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.listado", null, "Padrón de Matrículas", LocaleContextHolder.getLocale()));

        return "matriculas/list";
    }

    @GetMapping("/nueva")
    @PreAuthorize("hasAuthority('MATRICULA_CREAR')")
    public String nueva(Model model) {
        if (!model.containsAttribute("matricula")) {
            MatriculaRequest defaultRequest = new MatriculaRequest();
            defaultRequest.setFechaMatricula(LocalDate.now(ZoneId.of("America/Lima")));
            defaultRequest.setEstado(EstadoMatricula.MATRICULADO);

            // Obtener año escolar activo por defecto
            List<AnioEscolarResponse> anios = anioEscolarService.listarTodos();
            anios.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getActivo()))
                    .findFirst()
                    .ifPresent(activo -> defaultRequest.setAnioEscolarId(activo.getId()));

            model.addAttribute("matricula", defaultRequest);
        }

        cargarCatalogosFormulario(model, false, null, null);
        model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.nueva", null, "Nueva Matrícula", LocaleContextHolder.getLocale()));
        return "matriculas/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MATRICULA_CREAR')")
    public String crear(
            @Valid @ModelAttribute("matricula") MatriculaRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            cargarCatalogosFormulario(model, false, null, request);
            model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.nueva", null, "Nueva Matrícula", LocaleContextHolder.getLocale()));
            return "matriculas/form";
        }

        try {
            matriculaService.crearMatricula(request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("matricula.creada", null, "Matrícula registrada correctamente.", LocaleContextHolder.getLocale())
            );
            return "redirect:/matriculas";
        } catch (BusinessException | ValidationException | ResourceNotFoundException ex) {
            bindingResult.reject("error.business", ex.getMessage());
            cargarCatalogosFormulario(model, false, null, request);
            model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.nueva", null, "Nueva Matrícula", LocaleContextHolder.getLocale()));
            return "matriculas/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        MatriculaResponse matricula = matriculaService.buscarPorId(id);
        model.addAttribute("matricula", matricula);
        model.addAttribute("estados", EstadoMatricula.values());
        model.addAttribute("currentMenu", "matriculas");
        model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.detalle", null, "Detalle de Matrícula", LocaleContextHolder.getLocale()));
        return "matriculas/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        MatriculaResponse matriculaExistente = matriculaService.buscarPorId(id);

        MatriculaRequest request = new MatriculaRequest(
                matriculaExistente.getPersonaEstudianteId(),
                matriculaExistente.getAnioEscolarId(),
                matriculaExistente.getGradoId(),
                matriculaExistente.getSeccionId(),
                matriculaExistente.getCodigoMatricula(),
                matriculaExistente.getFechaMatricula(),
                matriculaExistente.getEstado(),
                matriculaExistente.getObservaciones()
        );

        model.addAttribute("matricula", request);
        cargarCatalogosFormulario(model, true, id, request);
        model.addAttribute("matriculaOriginal", matriculaExistente);
        model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.editar", null, "Editar Matrícula", LocaleContextHolder.getLocale()));
        return "matriculas/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("matricula") MatriculaRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            cargarCatalogosFormulario(model, true, id, request);
            model.addAttribute("matriculaOriginal", matriculaService.buscarPorId(id));
            model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.editar", null, "Editar Matrícula", LocaleContextHolder.getLocale()));
            return "matriculas/form";
        }

        try {
            matriculaService.actualizarMatricula(id, request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("matricula.actualizada", null, "Matrícula actualizada correctamente.", LocaleContextHolder.getLocale())
            );
            return "redirect:/matriculas/" + id;
        } catch (BusinessException | ValidationException | ResourceNotFoundException ex) {
            bindingResult.reject("error.business", ex.getMessage());
            cargarCatalogosFormulario(model, true, id, request);
            model.addAttribute("matriculaOriginal", matriculaService.buscarPorId(id));
            model.addAttribute("pageTitle", messageSource.getMessage("matricula.titulo.editar", null, "Editar Matrícula", LocaleContextHolder.getLocale()));
            return "matriculas/form";
        }
    }

    @PostMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    public String cambiarEstado(
            @PathVariable("id") Long id,
            @RequestParam("estado") EstadoMatricula estado,
            RedirectAttributes redirectAttributes
    ) {
        matriculaService.cambiarEstado(id, estado);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                messageSource.getMessage("matricula.estado.actualizado", null, "Estado de matrícula actualizado correctamente.", LocaleContextHolder.getLocale())
        );
        return "redirect:/matriculas/" + id;
    }

    @GetMapping("/secciones")
    @PreAuthorize("hasAuthority('MATRICULA_VER') or hasAuthority('MATRICULA_CREAR') or hasAuthority('MATRICULA_EDITAR')")
    @ResponseBody
    public List<SeccionCapacidadDTO> obtenerSeccionesPorGrado(
            @RequestParam("gradoId") Long gradoId,
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId
    ) {
        return matriculaService.listarSeccionesConCapacidad(gradoId, anioEscolarId);
    }

    private void cargarCatalogosFormulario(Model model, boolean modoEdicion, Long matriculaId, MatriculaRequest currentRequest) {
        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantesActivos());
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("grados", gradoService.listarTodos());
        model.addAttribute("estados", EstadoMatricula.values());
        model.addAttribute("modoEdicion", modoEdicion);
        model.addAttribute("matriculaId", matriculaId);
        model.addAttribute("currentMenu", "matriculas");

        if (currentRequest != null && currentRequest.getGradoId() != null) {
            model.addAttribute("seccionesDisponibles",
                    matriculaService.listarSeccionesConCapacidad(currentRequest.getGradoId(), currentRequest.getAnioEscolarId())
            );
        } else {
            model.addAttribute("seccionesDisponibles", Collections.emptyList());
        }
    }
}
