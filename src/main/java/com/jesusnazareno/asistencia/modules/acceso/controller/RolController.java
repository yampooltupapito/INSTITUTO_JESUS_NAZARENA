package com.jesusnazareno.asistencia.modules.acceso.controller;

import com.jesusnazareno.asistencia.modules.acceso.dto.RolResponse;
import com.jesusnazareno.asistencia.modules.acceso.service.RolService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controlador de consulta para los Roles del sistema y sus permisos asociados.
 */
@Controller
@RequestMapping("/roles")
@PreAuthorize("hasAuthority('USUARIO_VER')")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /**
     * Listado general de roles configurados en la base de datos con conteo de permisos.
     */
    @GetMapping
    public String listar(Model model) {
        List<RolResponse> roles = rolService.listarTodos();
        model.addAttribute("roles", roles);
        model.addAttribute("currentMenu", "roles");
        return "roles/list";
    }

    /**
     * Vista de detalle del rol con el listado completo de permisos asociados desde rol_permiso.
     */
    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        RolResponse rol = rolService.buscarPorId(id);
        model.addAttribute("rol", rol);
        model.addAttribute("currentMenu", "roles");
        return "roles/detail";
    }
}
