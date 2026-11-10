package com.jesusnazareno.asistencia.modules.apoderado.controller;

import com.jesusnazareno.asistencia.common.validation.DniPeruanoValidator;
import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoCreateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoUpdateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.service.EstudianteApoderadoService;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ApoderadoController {

    private final EstudianteApoderadoService estudianteApoderadoService;
    private final EstudianteService estudianteService;
    private final PersonaActorRepository personaActorRepository;
    private final MessageSource messageSource;
    private final DniPeruanoValidator dniValidator;

    public ApoderadoController(
            EstudianteApoderadoService estudianteApoderadoService,
            EstudianteService estudianteService,
            PersonaActorRepository personaActorRepository,
            MessageSource messageSource) {
        this.estudianteApoderadoService = estudianteApoderadoService;
        this.estudianteService = estudianteService;
        this.personaActorRepository = personaActorRepository;
        this.messageSource = messageSource;
        this.dniValidator = new DniPeruanoValidator();
    }

    @GetMapping("/apoderados")
    @PreAuthorize("hasAuthority('APODERADO_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "estado", defaultValue = "TODOS") String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);

        Boolean activoFilter = switch (estado != null ? estado.toUpperCase() : "TODOS") {
            case "ACTIVOS" -> true;
            case "INACTIVOS" -> false;
            default -> null;
        };

        Pageable pageable = PageRequest.of(
                pagina,
                tamano,
                Sort.by(Sort.Direction.ASC, "apellidoPaterno", "apellidoMaterno", "nombres")
        );

        Page<ApoderadoResponse> apoderados = estudianteApoderadoService.buscarApoderados(q, activoFilter, pageable);

        model.addAttribute("apoderados", apoderados);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("estado", estado);
        model.addAttribute("page", pagina);
        model.addAttribute("size", tamano);
        model.addAttribute("currentMenu", "apoderados");
        model.addAttribute("pageTitle", "Gestión de Padres y Apoderados");

        return "apoderados/list";
    }

    @GetMapping("/apoderados/nuevo")
    @PreAuthorize("hasAuthority('APODERADO_CREAR')")
    public String nuevo(
            @RequestParam(name = "estudianteId", required = false) Long estudianteId,
            Model model
    ) {
        ApoderadoCreateRequest request = new ApoderadoCreateRequest();
        if (estudianteId != null) {
            request.setEstudianteId(estudianteId);
            try {
                PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(estudianteId);
                model.addAttribute("estudianteAsignado", estudiante);
            } catch (Exception ignored) {
            }
        }

        model.addAttribute("apoderado", request);
        model.addAttribute("isEdit", false);
        cargarCatalogos(model);
        model.addAttribute("currentMenu", "apoderados");
        model.addAttribute("pageTitle", "Registrar Nuevo Apoderado");

        return "apoderados/form";
    }

    @PostMapping("/apoderados")
    @PreAuthorize("hasAuthority('APODERADO_CREAR')")
    public String crear(
            @Valid @ModelAttribute("apoderado") ApoderadoCreateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        validarDni(request.getTipoDocumento(), request.getNumeroDocumento(), bindingResult);

        if (bindingResult.hasErrors()) {
            cargarCatalogos(model);
            model.addAttribute("isEdit", false);
            if (request.getEstudianteId() != null) {
                try {
                    PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(request.getEstudianteId());
                    model.addAttribute("estudianteAsignado", estudiante);
                } catch (Exception ignored) {
                }
            }
            model.addAttribute("currentMenu", "apoderados");
            model.addAttribute("pageTitle", "Registrar Nuevo Apoderado");
            return "apoderados/form";
        }

        try {
            ApoderadoResponse creado = estudianteApoderadoService.registrarApoderado(request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("apoderado.creado", null, "Apoderado registrado correctamente.", LocaleContextHolder.getLocale())
            );

            if (request.getEstudianteId() != null) {
                return "redirect:/estudiantes/" + request.getEstudianteId();
            }
            return "redirect:/apoderados/" + creado.getId();
        } catch (BusinessException | ValidationException ex) {
            bindingResult.reject("error.business", ex.getMessage());
            cargarCatalogos(model);
            model.addAttribute("isEdit", false);
            if (request.getEstudianteId() != null) {
                try {
                    PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(request.getEstudianteId());
                    model.addAttribute("estudianteAsignado", estudiante);
                } catch (Exception ignored) {
                }
            }
            model.addAttribute("currentMenu", "apoderados");
            model.addAttribute("pageTitle", "Registrar Nuevo Apoderado");
            return "apoderados/form";
        }
    }

    @GetMapping("/apoderados/{id}")
    @PreAuthorize("hasAuthority('APODERADO_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        ApoderadoResponse apoderado = estudianteApoderadoService.obtenerApoderadoPorId(id);
        List<EstudianteApoderadoResponse> relaciones = estudianteApoderadoService.obtenerRelacionesPorApoderado(id);

        model.addAttribute("apoderado", apoderado);
        model.addAttribute("relaciones", relaciones);
        model.addAttribute("currentMenu", "apoderados");
        model.addAttribute("pageTitle", "Ficha del Apoderado");

        return "apoderados/detail";
    }

    @GetMapping("/apoderados/{id}/editar")
    @PreAuthorize("hasAuthority('APODERADO_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        ApoderadoResponse apoderado = estudianteApoderadoService.obtenerApoderadoPorId(id);

        ApoderadoUpdateRequest request = new ApoderadoUpdateRequest();
        request.setTipoDocumento(TipoDocumento.valueOf(apoderado.getTipoDocumento()));
        request.setNumeroDocumento(apoderado.getNumeroDocumento());
        request.setNombres(apoderado.getNombres());
        request.setApellidoPaterno(apoderado.getApellidoPaterno());
        request.setApellidoMaterno(apoderado.getApellidoMaterno());
        request.setFechaNacimiento(apoderado.getFechaNacimiento());
        if (apoderado.getSexo() != null && !apoderado.getSexo().isBlank()) {
            request.setSexo(Sexo.valueOf(apoderado.getSexo()));
        }
        request.setTelefono(apoderado.getTelefono());
        request.setCorreo(apoderado.getCorreo());
        request.setDireccion(apoderado.getDireccion());

        model.addAttribute("apoderado", request);
        model.addAttribute("apoderadoId", id);
        cargarCatalogos(model);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "apoderados");
        model.addAttribute("pageTitle", "Editar Ficha de Apoderado");

        return "apoderados/form";
    }

    @PostMapping("/apoderados/{id}")
    @PreAuthorize("hasAuthority('APODERADO_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("apoderado") ApoderadoUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        validarDni(request.getTipoDocumento(), request.getNumeroDocumento(), bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("apoderadoId", id);
            model.addAttribute("isEdit", true);
            cargarCatalogos(model);
            model.addAttribute("currentMenu", "apoderados");
            model.addAttribute("pageTitle", "Editar Ficha de Apoderado");
            return "apoderados/form";
        }

        try {
            estudianteApoderadoService.actualizarApoderado(id, request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("apoderado.actualizado", null, "Apoderado actualizado correctamente.", LocaleContextHolder.getLocale())
            );
            return "redirect:/apoderados/" + id;
        } catch (BusinessException | ValidationException ex) {
            bindingResult.reject("error.business", ex.getMessage());
            model.addAttribute("apoderadoId", id);
            model.addAttribute("isEdit", true);
            cargarCatalogos(model);
            model.addAttribute("currentMenu", "apoderados");
            model.addAttribute("pageTitle", "Editar Ficha de Apoderado");
            return "apoderados/form";
        }
    }

    @PostMapping("/apoderados/{id}/activar")
    @PreAuthorize("hasAuthority('APODERADO_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        estudianteApoderadoService.activarApoderado(id);
        redirectAttributes.addFlashAttribute("successMessage", "Función de apoderado activada correctamente.");
        return "redirect:/apoderados/" + id;
    }

    @PostMapping("/apoderados/{id}/desactivar")
    @PreAuthorize("hasAuthority('APODERADO_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        estudianteApoderadoService.desactivarApoderado(id);
        redirectAttributes.addFlashAttribute("successMessage", "Función de apoderado desactivada correctamente.");
        return "redirect:/apoderados/" + id;
    }

    // =========================================================================
    // RUTAS DE RELACIÓN ESTUDIANTE - APODERADO
    // =========================================================================

    @GetMapping("/estudiantes/{id}/apoderados")
    @PreAuthorize("hasAuthority('APODERADO_VER')")
    public String listarPorEstudiante(@PathVariable("id") Long id) {
        return "redirect:/estudiantes/" + id;
    }

    @GetMapping("/estudiantes/{id}/apoderados/nuevo")
    @PreAuthorize("hasAuthority('APODERADO_CREAR')")
    public String nuevoParaEstudiante(@PathVariable("id") Long id, Model model) {
        PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(id);

        EstudianteApoderadoRequest request = new EstudianteApoderadoRequest();
        request.setEstudianteId(id);

        // Obtener apoderados disponibles ordenados
        List<Persona> apoderadosDisponibles = personaActorRepository
                .findPersonasActivasByTipoActorCodigoOrderByApellidos("PADRE_APODERADO");

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("relacion", request);
        model.addAttribute("apoderados", apoderadosDisponibles);
        model.addAttribute("parentescos", Parentesco.values());
        model.addAttribute("currentMenu", "estudiantes");
        model.addAttribute("pageTitle", "Asignar Apoderado al Estudiante");

        return "apoderados/asignar";
    }

    @PostMapping("/estudiantes/{id}/apoderados")
    @PreAuthorize("hasAuthority('APODERADO_CREAR')")
    public String asignar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("relacion") EstudianteApoderadoRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        request.setEstudianteId(id);

        if (bindingResult.hasErrors()) {
            PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(id);
            List<Persona> apoderadosDisponibles = personaActorRepository
                    .findPersonasActivasByTipoActorCodigoOrderByApellidos("PADRE_APODERADO");
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("apoderados", apoderadosDisponibles);
            model.addAttribute("parentescos", Parentesco.values());
            model.addAttribute("currentMenu", "estudiantes");
            model.addAttribute("pageTitle", "Asignar Apoderado al Estudiante");
            return "apoderados/asignar";
        }

        try {
            estudianteApoderadoService.asignarApoderadoAEstudiante(request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("apoderado.relacion.creada", null, "Apoderado asignado al estudiante correctamente.", LocaleContextHolder.getLocale())
            );
            return "redirect:/estudiantes/" + id;
        } catch (BusinessException | ValidationException ex) {
            bindingResult.reject("error.business", ex.getMessage());
            PersonaResponse estudiante = estudianteService.obtenerEstudiantePorId(id);
            List<Persona> apoderadosDisponibles = personaActorRepository
                    .findPersonasActivasByTipoActorCodigoOrderByApellidos("PADRE_APODERADO");
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("apoderados", apoderadosDisponibles);
            model.addAttribute("parentescos", Parentesco.values());
            model.addAttribute("currentMenu", "estudiantes");
            model.addAttribute("pageTitle", "Asignar Apoderado al Estudiante");
            return "apoderados/asignar";
        }
    }

    @PostMapping("/estudiantes/{estudianteId}/apoderados/{relacionId}/principal")
    @PreAuthorize("hasAuthority('APODERADO_EDITAR')")
    public String marcarPrincipal(
            @PathVariable("estudianteId") Long estudianteId,
            @PathVariable("relacionId") Long relacionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            estudianteApoderadoService.marcarComoPrincipal(estudianteId, relacionId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("apoderado.principal.actualizado", null, "Apoderado principal actualizado correctamente.", LocaleContextHolder.getLocale())
            );
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/estudiantes/" + estudianteId;
    }

    @PostMapping("/estudiantes/{estudianteId}/apoderados/{relacionId}/desactivar")
    @PreAuthorize("hasAuthority('APODERADO_EDITAR')")
    public String desactivarRelacion(
            @PathVariable("estudianteId") Long estudianteId,
            @PathVariable("relacionId") Long relacionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            estudianteApoderadoService.desactivarRelacion(estudianteId, relacionId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    messageSource.getMessage("apoderado.relacion.desactivada", null, "Relación con el apoderado desactivada correctamente.", LocaleContextHolder.getLocale())
            );
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/estudiantes/" + estudianteId;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("parentescos", Parentesco.values());
    }

    private void validarDni(TipoDocumento tipoDocumento, String numeroDocumento, BindingResult bindingResult) {
        if (tipoDocumento == TipoDocumento.DNI && numeroDocumento != null && !numeroDocumento.isBlank()) {
            if (!dniValidator.isValid(numeroDocumento, null)) {
                bindingResult.rejectValue("numeroDocumento", "validation.dni", "El DNI debe contener exactamente 8 dígitos numéricos.");
            }
        }
    }
}
