package com.jesusnazareno.asistencia.modules.auditoria.controller;

import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaEventoDTO;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaFiltroDTO;
import com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auditoria")
@PreAuthorize("hasAuthority('AUDITORIA_VER')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping({"", "/"})
    public String listar(@ModelAttribute("filtro") AuditoriaFiltroDTO filtro,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "15") int size,
                         Model model) {
        int pageSize = Math.min(Math.max(size, 1), 100);
        int pageNumber = Math.max(page, 0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "fecha"));

        Page<AuditoriaEventoDTO> eventos = auditoriaService.buscarEventos(filtro, pageable);

        model.addAttribute("eventos", eventos);
        model.addAttribute("acciones", AccionAuditoria.values());
        model.addAttribute("modulos", ModuloAuditoria.values());
        model.addAttribute("resultados", ResultadoAuditoria.values());

        return "auditoria/list";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable("id") Long id, Model model) {
        AuditoriaEventoDTO evento = auditoriaService.buscarPorId(id);
        model.addAttribute("evento", evento);
        return "auditoria/detail";
    }
}
