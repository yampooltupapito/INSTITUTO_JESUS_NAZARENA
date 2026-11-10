package com.jesusnazareno.asistencia.modules.apoderado.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApoderadoResponse {

    private Long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String telefono;
    private String correo;
    private String direccion;
    private Boolean activo;
    private Boolean tieneUsuario;
    private List<EstudianteResumenDTO> estudiantesAsociados = new ArrayList<>();
    private int cantidadEstudiantes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ApoderadoResponse() {
    }

    public ApoderadoResponse(Long id, String tipoDocumento, String numeroDocumento, String nombres,
                             String apellidoPaterno, String apellidoMaterno, LocalDate fechaNacimiento,
                             String sexo, String telefono, String correo, String direccion, Boolean activo,
                             Boolean tieneUsuario, List<EstudianteResumenDTO> estudiantesAsociados,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.nombreCompleto = nombres + " " + apellidoPaterno + " " + apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.activo = activo;
        this.tieneUsuario = tieneUsuario != null ? tieneUsuario : Boolean.FALSE;
        this.estudiantesAsociados = estudiantesAsociados != null ? estudiantesAsociados : new ArrayList<>();
        this.cantidadEstudiantes = this.estudiantesAsociados.size();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getTieneUsuario() {
        return tieneUsuario;
    }

    public void setTieneUsuario(Boolean tieneUsuario) {
        this.tieneUsuario = tieneUsuario;
    }

    public List<EstudianteResumenDTO> getEstudiantesAsociados() {
        return estudiantesAsociados;
    }

    public void setEstudiantesAsociados(List<EstudianteResumenDTO> estudiantesAsociados) {
        this.estudiantesAsociados = estudiantesAsociados;
        this.cantidadEstudiantes = estudiantesAsociados != null ? estudiantesAsociados.size() : 0;
    }

    public int getCantidadEstudiantes() {
        return cantidadEstudiantes;
    }

    public void setCantidadEstudiantes(int cantidadEstudiantes) {
        this.cantidadEstudiantes = cantidadEstudiantes;
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
