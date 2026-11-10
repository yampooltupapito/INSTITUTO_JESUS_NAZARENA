package com.jesusnazareno.asistencia.modules.personal.dto;

import com.jesusnazareno.asistencia.common.validation.TelefonoPeruano;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para el registro inicial de un miembro del personal institucional
 * o asignación de una función institucional a una persona existente.
 */
public class PersonalCreateRequest {

    @NotNull(message = "{validation.required}")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "{validation.required}")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres.")
    private String numeroDocumento;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres.")
    private String nombres;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "El apellido paterno no puede exceder 100 caracteres.")
    private String apellidoPaterno;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "El apellido materno no puede exceder 100 caracteres.")
    private String apellidoMaterno;

    @NotNull(message = "{validation.required}")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual.")
    private LocalDate fechaNacimiento;

    @NotNull(message = "{validation.required}")
    private Sexo sexo;

    @TelefonoPeruano
    private String telefono;

    @Email(message = "{validation.invalid}")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres.")
    private String correo;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres.")
    private String direccion;

    @NotBlank(message = "{validation.required}")
    private String tipoActor;

    private LocalDate fechaInicio;

    public PersonalCreateRequest() {
        this.fechaInicio = LocalDate.now();
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

    public String getTipoActor() {
        return tipoActor;
    }

    public void setTipoActor(String tipoActor) {
        this.tipoActor = tipoActor;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
}
