package com.jesusnazareno.asistencia.modules.reporte.dto;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

/**
 * Contenedor del resultado de consulta de reporte de asistencia,
 * incluyendo datos paginados, totales calculados y filtros aplicados.
 */
public class ReporteAsistenciaResultado {

    private final Page<ReporteAsistenciaItem> paginaItems;
    private final List<ReporteAsistenciaItem> todosItems;
    private final ReporteAsistenciaResumen resumen;
    private final ReporteAsistenciaFiltro filtro;

    public ReporteAsistenciaResultado(Page<ReporteAsistenciaItem> paginaItems,
                                      List<ReporteAsistenciaItem> todosItems,
                                      ReporteAsistenciaResumen resumen,
                                      ReporteAsistenciaFiltro filtro) {
        this.paginaItems = paginaItems;
        this.todosItems = todosItems != null ? todosItems : Collections.emptyList();
        this.resumen = resumen;
        this.filtro = filtro;
    }

    public Page<ReporteAsistenciaItem> getPaginaItems() {
        return paginaItems;
    }

    public List<ReporteAsistenciaItem> getTodosItems() {
        return todosItems;
    }

    public ReporteAsistenciaResumen getResumen() {
        return resumen;
    }

    public ReporteAsistenciaFiltro getFiltro() {
        return filtro;
    }
}
