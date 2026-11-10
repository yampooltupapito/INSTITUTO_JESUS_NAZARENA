package com.jesusnazareno.asistencia.modules.estudiante.controller;

import com.jesusnazareno.asistencia.common.validation.DniPeruanoValidator;
import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.apoderado.service.EstudianteApoderadoService;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final EstudianteApoderadoService estudianteApoderadoService;
    private final MessageSource messageSource;
    private final DniPeruanoValidator dniValidator;

    public EstudianteController(
            EstudianteService estudianteService,
            EstudianteApoderadoService estudianteApoderadoService,
            MessageSource messageSource) {
        this.estudianteService = estudianteService;
        this.estudianteApoderadoService = estudianteApoderadoService;
        this.messageSource = messageSource;
        this.dniValidator = new DniPeruanoValidator();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ESTUDIANTE_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "estado", defaultValue = "TODOS") String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(
                pagina,
                tamano,
                Sort.by(Sort.Direction.ASC, "persona.apellidoPaterno", "persona.apellidoMaterno", "persona.nombres")
        );

        Page<PersonaResponse> estudiantes = estudianteService.buscarEstudiantes(q, estado, pageable);

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("estado", estado);
        model.addAttribute("page", pagina);
        model.addAttribute("size", tamano);
        model.addAttribute("currentMenu", "estudiantes");
        model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.listado", null, "Gestión de Estudiantes", LocaleContextHolder.getLocale()));

        return "estudiantes/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAuthority('ESTUDIANTE_CREAR')")
    public String nuevo(Model model) {
        if (!model.containsAttribute("estudiante")) {
            model.addAttribute("estudiante", new PersonaRequest());
        }
        cargarCatalogos(model, false, null);
        model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.nuevo", null, "Registrar Estudiante", LocaleContextHolder.getLocale()));
        return "estudiantes/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ESTUDIANTE_CREAR')")
    public String crear(
            @Valid @ModelAttribute("estudiante") PersonaRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        validarDocumentoDni(request, bindingResult);

        if (bindingResult.hasErrors()) {
            cargarCatalogos(model, false, null);
            model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.nuevo", null, "Registrar Estudiante", LocaleContextHolder.getLocale()));
            return "estudiantes/form";
        }

        try {
            estudianteService.registrarEstudiante(request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("estudiante.creado", null, "Estudiante registrado correctamente.", LocaleContextHolder.getLocale())
            );
            return "redirect:/estudiantes";
        } catch (BusinessException | ValidationException ex) {
            bindingResult.rejectValue("numeroDocumento", "error.business", ex.getMessage());
            cargarCatalogos(model, false, null);
            model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.nuevo", null, "Registrar Estudiante", LocaleContextHolder.getLocale()));
            return "estudiantes/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTUDIANTE_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(id);
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("apoderados", estudianteApoderadoService.obtenerApoderadosPorEstudiante(id));
        model.addAttribute("currentMenu", "estudiantes");
        model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.detalle", null, "Ficha del Estudiante", LocaleContextHolder.getLocale()));
        return "estudiantes/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('ESTUDIANTE_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(id);

        PersonaRequest request = new PersonaRequest(
                estudiante.getTipoDocumento(),
                estudiante.getNumeroDocumento(),
                estudiante.getNombres(),
                estudiante.getApellidoPaterno(),
                estudiante.getApellidoMaterno(),
                estudiante.getFechaNacimiento(),
                estudiante.getSexo(),
                estudiante.getTelefono(),
                estudiante.getCorreo(),
                estudiante.getDireccion()
        );

        model.addAttribute("estudiante", request);
        cargarCatalogos(model, true, id);
        model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.editar", null, "Editar Estudiante", LocaleContextHolder.getLocale()));
        return "estudiantes/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTUDIANTE_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("estudiante") PersonaRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        validarDocumentoDni(request, bindingResult);

        if (bindingResult.hasErrors()) {
            cargarCatalogos(model, true, id);
            model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.editar", null, "Editar Estudiante", LocaleContextHolder.getLocale()));
            return "estudiantes/form";
        }

        try {
            estudianteService.actualizarEstudiante(id, request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("estudiante.actualizado", null, "Estudiante actualizado correctamente.", LocaleContextHolder.getLocale())
            );
            return "redirect:/estudiantes/" + id;
        } catch (BusinessException | ValidationException ex) {
            bindingResult.rejectValue("numeroDocumento", "error.business", ex.getMessage());
            cargarCatalogos(model, true, id);
            model.addAttribute("pageTitle", messageSource.getMessage("estudiante.titulo.editar", null, "Editar Estudiante", LocaleContextHolder.getLocale()));
            return "estudiantes/form";
        }
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('ESTUDIANTE_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        estudianteService.desactivarEstudiante(id);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                messageSource.getMessage("estudiante.desactivado", null, "Estudiante desactivado correctamente.", LocaleContextHolder.getLocale())
        );
        return "redirect:/estudiantes";
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ESTUDIANTE_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        estudianteService.activarEstudiante(id);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                messageSource.getMessage("estudiante.activado", null, "Estudiante activado correctamente.", LocaleContextHolder.getLocale())
        );
        return "redirect:/estudiantes";
    }

    private void validarDocumentoDni(PersonaRequest request, BindingResult bindingResult) {
        if (request.getTipoDocumento() == TipoDocumento.DNI) {
            if (!dniValidator.isValid(request.getNumeroDocumento(), null)) {
                bindingResult.rejectValue("numeroDocumento", "validation.dni", "El DNI debe contener exactamente 8 dígitos numéricos.");
            }
        }
    }

    private void cargarCatalogos(Model model, boolean modoEdicion, Long estudianteId) {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("modoEdicion", modoEdicion);
        model.addAttribute("estudianteId", estudianteId);
        model.addAttribute("currentMenu", "estudiantes");
    }
}
