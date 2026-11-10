package com.jesusnazareno.asistencia.modules.dashboard.controller;

import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardFiltro;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumen;
import com.jesusnazareno.asistencia.modules.dashboard.service.DashboardService;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;

/**
 * Controlador web para el Dashboard principal de control de asistencia.
 * Maneja indicadores clave, resúmenes agrupados y filtros contextuales.
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final AnioEscolarRepository anioEscolarRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;

    public DashboardController(DashboardService dashboardService,
                               AnioEscolarRepository anioEscolarRepository,
                               GradoRepository gradoRepository,
                               SeccionRepository seccionRepository) {
        this.dashboardService = dashboardService;
        this.anioEscolarRepository = anioEscolarRepository;
        this.gradoRepository = gradoRepository;
        this.seccionRepository = seccionRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('DASHBOARD_VER')")
    public String dashboard(@ModelAttribute("filtro") DashboardFiltro filtro, Model model) {
        DashboardResumen resumen;
        if (filtro != null && filtro.tieneFiltros()) {
            resumen = dashboardService.obtenerResumen(filtro);
        } else {
            resumen = dashboardService.obtenerResumen();
        }

        model.addAttribute("resumen", resumen);
        model.addAttribute("filtro", filtro != null ? filtro : new DashboardFiltro());
        model.addAttribute("aniosEscolares", anioEscolarRepository.findAll(Sort.by(Sort.Direction.DESC, "anio")));
        model.addAttribute("grados", gradoRepository.findAll(Sort.by(Sort.Direction.ASC, "numero")));

        if (filtro != null && filtro.getGradoId() != null) {
            model.addAttribute("secciones", seccionRepository.findByGradoIdAndActivoTrue(filtro.getGradoId()));
        } else {
            model.addAttribute("secciones", Collections.emptyList());
        }

        return "dashboard/index";
    }
}
