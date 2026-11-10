package com.jesusnazareno.asistencia.modules.acceso.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioCreateRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioPasswordForm;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioUpdateRequest;
import com.jesusnazareno.asistencia.modules.acceso.service.RolService;
import com.jesusnazareno.asistencia.modules.acceso.service.UsuarioService;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
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
 * Controlador web administrativo para la gestión integral de Usuarios:
 * listado con búsqueda y filtros, creación, detalle con permisos efectivos,
 * edición básica, activación/desactivación lógica y cambio administrativo de contraseña.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PersonaRepository personaRepository;

    public UsuarioController(UsuarioService usuarioService,
                             RolService rolService,
                             PersonaRepository personaRepository) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.personaRepository = personaRepository;
    }

    /**
     * Listado general de usuarios con filtros por texto (q), rol y estado (activo/inactivo),
     * manteniendo paginación segura y eficiente.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USUARIO_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "rolId", required = false) Long rolId,
            @RequestParam(name = "activo", required = false) Boolean activo,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        int pageSize = Math.min(Math.max(size, 1), 100);
        int pageNum = Math.max(page, 0);
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<UsuarioResponse> usuariosPage = usuarioService.listarPaginado(q, rolId, activo, pageable);

        model.addAttribute("usuariosPage", usuariosPage);
        model.addAttribute("roles", rolService.listarActivos());
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("rolId", rolId);
        model.addAttribute("activo", activo);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentMenu", "usuarios");

        return "usuarios/list";
    }

    /**
     * Muestra el formulario para registrar un nuevo usuario en el sistema.
     */
    @GetMapping("/nuevo")
    @PreAuthorize("hasAuthority('USUARIO_CREAR')")
    public String nuevo(Model model) {
        if (!model.containsAttribute("usuarioForm")) {
            model.addAttribute("usuarioForm", new UsuarioCreateRequest());
        }
        cargarDatosFormulario(model);
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "usuarios");
        return "usuarios/form";
    }

    /**
     * Procesa la creación de un nuevo usuario con validación estricta en backend.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USUARIO_CREAR')")
    public String guardar(
            @Valid @ModelAttribute("usuarioForm") UsuarioCreateRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("currentMenu", "usuarios");
            return "usuarios/form";
        }

        try {
            usuarioService.crear(form);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado correctamente.");
            return "redirect:/usuarios";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            cargarDatosFormulario(model);
            model.addAttribute("isEdit", false);
            model.addAttribute("currentMenu", "usuarios");
            return "usuarios/form";
        }
    }

    /**
     * Muestra la ficha de detalle de un usuario, incluyendo sus permisos efectivos.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        UsuarioResponse usuario = usuarioService.buscarPorIdConPermisos(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("currentMenu", "usuarios");
        return "usuarios/detail";
    }

    /**
     * Muestra el formulario de edición de datos permitidos (username y rol).
     */
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public String editar(@PathVariable("id") Long id, Model model) {
        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        if (!model.containsAttribute("usuarioForm")) {
            UsuarioUpdateRequest form = new UsuarioUpdateRequest(usuario.getUsername(), usuario.getRolId(), usuario.getActivo());
            model.addAttribute("usuarioForm", form);
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listarActivos());
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "usuarios");
        return "usuarios/form";
    }

    /**
     * Procesa la actualización de los datos de un usuario.
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("usuarioForm") UsuarioUpdateRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        UsuarioResponse usuario = usuarioService.buscarPorId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", true);
            model.addAttribute("currentMenu", "usuarios");
            return "usuarios/form";
        }

        try {
            usuarioService.actualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente.");
            return "redirect:/usuarios/" + id;
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", rolService.listarActivos());
            model.addAttribute("isEdit", true);
            model.addAttribute("currentMenu", "usuarios");
            return "usuarios/form";
        }
    }

    /**
     * Activa un usuario previamente desactivado.
     */
    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.activar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario activado correctamente.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    /**
     * Desactiva un usuario sin eliminar el registro físico de la base de datos.
     * Previene la auto-desactivación y la desactivación del último administrador.
     */
    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desactivar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario desactivado correctamente.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    /**
     * Muestra el formulario para cambio/reset de contraseña administrativa.
     */
    @GetMapping("/{id}/password")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public String mostrarFormPassword(@PathVariable("id") Long id, Model model) {
        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new UsuarioPasswordForm());
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("currentMenu", "usuarios");
        return "usuarios/password";
    }

    /**
     * Procesa el cambio seguro de contraseña mediante BCrypt.
     */
    @PostMapping("/{id}/password")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public String cambiarPassword(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("passwordForm") UsuarioPasswordForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        UsuarioResponse usuario = usuarioService.buscarPorId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("currentMenu", "usuarios");
            return "usuarios/password";
        }

        if (!form.getNuevaPassword().equals(form.getConfirmarPassword())) {
            bindingResult.rejectValue("confirmarPassword", "usuario.password.no-coincide", "Las contraseñas no coinciden.");
            model.addAttribute("usuario", usuario);
            model.addAttribute("currentMenu", "usuarios");
            return "usuarios/password";
        }

        try {
            usuarioService.resetearPassword(id, form.getNuevaPassword(), form.getConfirmarPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Contraseña actualizada correctamente.");
            return "redirect:/usuarios/" + id;
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("currentMenu", "usuarios");
            return "usuarios/password";
        }
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("personasDisponibles", personaRepository.findPersonasDisponiblesParaUsuario());
        model.addAttribute("roles", rolService.listarActivos());
    }
}
