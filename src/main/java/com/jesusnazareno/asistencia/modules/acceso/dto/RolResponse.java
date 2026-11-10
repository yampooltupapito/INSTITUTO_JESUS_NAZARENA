package com.jesusnazareno.asistencia.modules.acceso.dto;

import java.util.List;

public class RolResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private List<PermisoResponse> permisos;

    public RolResponse() {
    }

    public RolResponse(Long id, String codigo, String nombre, String descripcion, Boolean activo, List<PermisoResponse> permisos) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.permisos = permisos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<PermisoResponse> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<PermisoResponse> permisos) {
        this.permisos = permisos;
    }
}
