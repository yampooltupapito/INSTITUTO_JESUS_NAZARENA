package com.jesusnazareno.asistencia.modules.alerta.dto;

import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;

/**
 * DTO para la visualización de la configuración de alertas.
 */
public class ConfiguracionAlertaResponse {

    private Long id;
    private TipoAlertaAsistencia tipo;
    private String tipoDescripcion;
    private Integer umbral;
    private Boolean activo;

    public ConfiguracionAlertaResponse() {
    }

    public ConfiguracionAlertaResponse(Long id, TipoAlertaAsistencia tipo, String tipoDescripcion, Integer umbral, Boolean activo) {
        this.id = id;
        this.tipo = tipo;
        this.tipoDescripcion = tipoDescripcion;
        this.umbral = umbral;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAlertaAsistencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlertaAsistencia tipo) {
        this.tipo = tipo;
    }

    public String getTipoDescripcion() {
        return tipoDescripcion;
    }

    public void setTipoDescripcion(String tipoDescripcion) {
        this.tipoDescripcion = tipoDescripcion;
    }

    public Integer getUmbral() {
        return umbral;
    }

    public void setUmbral(Integer umbral) {
        this.umbral = umbral;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
