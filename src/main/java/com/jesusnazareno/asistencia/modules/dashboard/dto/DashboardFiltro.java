package com.jesusnazareno.asistencia.modules.dashboard.dto;

/**
 * DTO para captura y validación de filtros en el Dashboard principal.
 */
public class DashboardFiltro {

    private Long anioEscolarId;
    private Long gradoId;
    private Long seccionId;

    public DashboardFiltro() {
    }

    public DashboardFiltro(Long anioEscolarId, Long gradoId, Long seccionId) {
        this.anioEscolarId = anioEscolarId;
        this.gradoId = gradoId;
        this.seccionId = seccionId;
    }

    public boolean tieneFiltros() {
        return anioEscolarId != null || gradoId != null || seccionId != null;
    }

    public boolean estaVacio() {
        return !tieneFiltros();
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }
}
