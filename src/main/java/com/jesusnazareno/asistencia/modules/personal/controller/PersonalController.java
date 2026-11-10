package com.jesusnazareno.asistencia.modules.personal.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalCreateRequest;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalResponse;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalUpdateRequest;
import com.jesusnazareno.asistencia.modules.personal.service.PersonalService;
import jakarta.validation.Valid;
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

/**
 * Controlador web administrativo para la gestión del personal institucional
 * (Director, Docente, Auxiliar).
 */
@Controller
@RequestMapping("/personal")
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    /**
     * Listado general de personal con búsqueda por texto y filtros por rol y estado.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PERSONAL_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "tipoActor", required = false) String tipoActor,
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        int pageSize = Math.min(Math.max(size, 1), 100);
        int pageNum = Math.max(page, 0);
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<PersonalResponse> personalPage = personalService.listarPersonal(q, tipoActor, estado, pageable);

        model.addAttribute("personalPage", personalPage);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("tipoActor", tipoActor != null ? tipoActor : "");
        model.addAttribute("estado", estado != null ? estado : "");
        model.addAttribute("tiposPersonal", personalService.obtenerTiposPersonalPermitidos());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentMenu", "personal");

        return "personal/list";
    }

    /**
     * Muestra el formulario para registrar un nuevo personal o asignar una función a persona existente.
     */
    @GetMapping("/nuevo")
    @PreAuthorize("hasAuthority('PERSONAL_CREAR')")
    public String nuevo(Model model) {
        if (!model.containsAttribute("personalForm")) {
            model.addAttribute("personalForm", new PersonalCreateRequest());
        }
        cargarDatosMaestros(model);
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "personal");
        return "personal/form";
    }

    /**
     * Procesa el registro de personal con validaciones en backend.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('PERSONAL_CREAR')")
    public String guardar(
            @Valid @ModelAttribute("personalForm") PersonalCreateRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarDatosMaestros(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("currentMenu", "personal");
            return "personal/form";
        }

        try {
            personalService.registrarPersonal(form);
            redirectAttributes.addFlashAttribute("successMessage", "Personal registrado correctamente.");
            return "redirect:/personal";
        } catch (ValidationException vex) {
            vex.getErrors().forEach((field, msg) -> bindingResult.rejectValue(field, "validation.invalid", msg));
            model.addAttribute("errorMessage", vex.getMessage());
            cargarDatosMaestros(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("currentMenu", "personal");
            return "personal/form";
        } catch (BusinessException bex) {
            model.addAttribute("errorMessage", bex.getMessage());
            cargarDatosMaestros(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("currentMenu", "personal");
            return "personal/form";
        }
    }

    /**
     * Visualiza el expediente y detalle completo de un miembro del personal.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERSONAL_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        PersonalResponse personal = personalService.buscarPorId(id);
        model.addAttribute("personal", personal);
        model.addAttribute("tiposPersonal", personalService.obtenerTiposPersonalPermitidos());
        model.addAttribute("currentMenu", "personal");
        return "personal/detail";
    }

    /**
     * Muestra el formulario de edición de datos personales.
     */
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('PERSONAL_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        PersonalResponse personal = personalService.buscarPorId(id);
        if (!model.containsAttribute("personalForm")) {
            PersonalUpdateRequest form = new PersonalUpdateRequest();
            form.setTipoDocumento(personal.getTipoDocumento());
            form.setNumeroDocumento(personal.getNumeroDocumento());
            form.setNombres(personal.getNombres());
            form.setApellidoPaterno(personal.getApellidoPaterno());
            form.setApellidoMaterno(personal.getApellidoMaterno());
            form.setFechaNacimiento(personal.getFechaNacimiento());
            form.setSexo(personal.getSexo());
            form.setTelefono(personal.getTelefono());
            form.setCorreo(personal.getCorreo());
            form.setDireccion(personal.getDireccion());
            model.addAttribute("personalForm", form);
        }
        model.addAttribute("personal", personal);
        cargarDatosMaestros(model);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "personal");
        return "personal/form";
    }

    /**
     * Procesa la actualización de datos personales.
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('PERSONAL_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("personalForm") PersonalUpdateRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        PersonalResponse personal = personalService.buscarPorId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("personal", personal);
            cargarDatosMaestros(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("currentMenu", "personal");
            return "personal/form";
        }

        try {
            personalService.actualizarPersonal(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Datos del personal actualizados correctamente.");
            return "redirect:/personal/" + id;
        } catch (ValidationException vex) {
            vex.getErrors().forEach((field, msg) -> bindingResult.rejectValue(field, "validation.invalid", msg));
            model.addAttribute("errorMessage", vex.getMessage());
            model.addAttribute("personal", personal);
            cargarDatosMaestros(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("currentMenu", "personal");
            return "personal/form";
        } catch (BusinessException bex) {
            model.addAttribute("errorMessage", bex.getMessage());
            model.addAttribute("personal", personal);
            cargarDatosMaestros(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("currentMenu", "personal");
            return "personal/form";
        }
    }

    /**
     * Activa una función institucional específica o el personal completo.
     */
    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('PERSONAL_EDITAR')")
    public String activar(
            @PathVariable("id") Long id,
            @RequestParam(name = "tipoActor", required = false) String tipoActor,
            RedirectAttributes redirectAttributes) {
        try {
            personalService.activarPersonal(id, tipoActor);
            redirectAttributes.addFlashAttribute("successMessage", "Función institucional activada correctamente.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/personal/" + id;
    }

    /**
     * Desactiva una función institucional específica o el rol de personal completo.
     * Preserva otros actores activos como apoderado o estudiante.
     */
    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('PERSONAL_EDITAR')")
    public String desactivar(
            @PathVariable("id") Long id,
            @RequestParam(name = "tipoActor", required = false) String tipoActor,
            RedirectAttributes redirectAttributes) {
        try {
            personalService.desactivarPersonal(id, tipoActor);
            redirectAttributes.addFlashAttribute("successMessage", "Función institucional desactivada correctamente.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/personal/" + id;
    }

    /**
     * Asigna una función institucional adicional (DIRECTOR, DOCENTE, AUXILIAR) a la persona.
     */
    @PostMapping("/{id}/actor")
    @PreAuthorize("hasAuthority('PERSONAL_EDITAR')")
    public String asignarActor(
            @PathVariable("id") Long id,
            @RequestParam("tipoActor") String tipoActor,
            RedirectAttributes redirectAttributes) {
        try {
            personalService.asignarNuevoActor(id, tipoActor);
            redirectAttributes.addFlashAttribute("successMessage", "Función institucional asignada correctamente.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/personal/" + id;
    }

    private void cargarDatosMaestros(Model model) {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("tiposPersonal", personalService.obtenerTiposPersonalPermitidos());
    }
}
