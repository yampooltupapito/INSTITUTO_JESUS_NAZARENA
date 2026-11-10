package com.jesusnazareno.asistencia.modules.apoderado.dto;

import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EstudianteApoderadoResponse {

    private Long id;
    private Long estudianteId;
    private String estudianteDocumento;
    private String estudianteNombreCompleto;
    private Long apoderadoId;
    private String apoderadoDocumento;
    private String apoderadoNombreCompleto;
    private String apoderadoTelefono;
    private String apoderadoCorreo;
    private String apoderadoDireccion;
    private Parentesco parentesco;
    private String parentescoDescripcion;
    private Boolean esPrincipal;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EstudianteApoderadoResponse() {
    }

    public EstudianteApoderadoResponse(Long id, Long estudianteId, String estudianteDocumento, String estudianteNombreCompleto,
                                       Long apoderadoId, String apoderadoDocumento, String apoderadoNombreCompleto,
                                       String apoderadoTelefono, String apoderadoCorreo, String apoderadoDireccion,
                                       Parentesco parentesco, String parentescoDescripcion, Boolean esPrincipal,
                                       LocalDate fechaInicio, LocalDate fechaFin, Boolean activo,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.estudianteDocumento = estudianteDocumento;
        this.estudianteNombreCompleto = estudianteNombreCompleto;
        this.apoderadoId = apoderadoId;
        this.apoderadoDocumento = apoderadoDocumento;
        this.apoderadoNombreCompleto = apoderadoNombreCompleto;
        this.apoderadoTelefono = apoderadoTelefono;
        this.apoderadoCorreo = apoderadoCorreo;
        this.apoderadoDireccion = apoderadoDireccion;
        this.parentesco = parentesco;
        this.parentescoDescripcion = parentescoDescripcion;
        this.esPrincipal = esPrincipal;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public Long getApoderadoId() {
        return apoderadoId;
    }

    public void setApoderadoId(Long apoderadoId) {
        this.apoderadoId = apoderadoId;
    }

    public String getApoderadoDocumento() {
        return apoderadoDocumento;
    }

    public void setApoderadoDocumento(String apoderadoDocumento) {
        this.apoderadoDocumento = apoderadoDocumento;
    }

    public String getApoderadoNombreCompleto() {
        return apoderadoNombreCompleto;
    }

    public void setApoderadoNombreCompleto(String apoderadoNombreCompleto) {
        this.apoderadoNombreCompleto = apoderadoNombreCompleto;
    }

    public String getApoderadoTelefono() {
        return apoderadoTelefono;
    }

    public void setApoderadoTelefono(String apoderadoTelefono) {
        this.apoderadoTelefono = apoderadoTelefono;
    }

    public String getApoderadoCorreo() {
        return apoderadoCorreo;
    }

    public void setApoderadoCorreo(String apoderadoCorreo) {
        this.apoderadoCorreo = apoderadoCorreo;
    }

    public String getApoderadoDireccion() {
        return apoderadoDireccion;
    }

    public void setApoderadoDireccion(String apoderadoDireccion) {
        this.apoderadoDireccion = apoderadoDireccion;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public String getParentescoDescripcion() {
        return parentescoDescripcion;
    }

    public void setParentescoDescripcion(String parentescoDescripcion) {
        this.parentescoDescripcion = parentescoDescripcion;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
