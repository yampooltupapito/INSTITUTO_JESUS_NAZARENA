package com.jesusnazareno.asistencia.modules.apoderado.dto;

import com.jesusnazareno.asistencia.common.validation.TelefonoPeruano;
import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ApoderadoCreateRequest {

    @NotNull(message = "El tipo de documento es obligatorio.")
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;

    @NotBlank(message = "El número de documento es obligatorio.")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres.")
    private String numeroDocumento;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres.")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio.")
    @Size(max = 100, message = "El apellido paterno no puede exceder 100 caracteres.")
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio.")
    @Size(max = 100, message = "El apellido materno no puede exceder 100 caracteres.")
    private String apellidoMaterno;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotNull(message = "El sexo es obligatorio.")
    private Sexo sexo;

    @TelefonoPeruano
    private String telefono;

    @Email(message = "El correo electrónico no tiene un formato válido.")
    @Size(max = 100, message = "El correo electrónico no puede exceder 100 caracteres.")
    private String correo;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres.")
    private String direccion;

    // Campos opcionales para asociar directamente a un estudiante
    private Long estudianteId;
    private Parentesco parentesco;
    private Boolean esPrincipal = Boolean.FALSE;

    public ApoderadoCreateRequest() {
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
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

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}
