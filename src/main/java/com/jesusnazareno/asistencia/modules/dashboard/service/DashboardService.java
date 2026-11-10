package com.jesusnazareno.asistencia.modules.dashboard.service;

import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardFiltro;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumen;

/**
 * Servicio para la obtención de indicadores, resúmenes agrupados y tendencias del Dashboard.
 */
public interface DashboardService {

    DashboardResumen obtenerResumen();

    DashboardResumen obtenerResumen(DashboardFiltro filtro);

    void validarFiltros(DashboardFiltro filtro);
}
