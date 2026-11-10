package com.jesusnazareno.asistencia.modules.persona.dto;

import com.jesusnazareno.asistencia.common.validation.TelefonoPeruano;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para la creación y actualización de personas.
 */
public class PersonaRequest {

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

    public PersonaRequest() {
    }

    public PersonaRequest(TipoDocumento tipoDocumento, String numeroDocumento, String nombres, String apellidoPaterno,
                          String apellidoMaterno, LocalDate fechaNacimiento, Sexo sexo, String telefono, String correo, String direccion) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public static PersonaRequestBuilder builder() {
        return new PersonaRequestBuilder();
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

    public static class PersonaRequestBuilder {
        private TipoDocumento tipoDocumento;
        private String numeroDocumento;
        private String nombres;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private LocalDate fechaNacimiento;
        private Sexo sexo;
        private String telefono;
        private String correo;
        private String direccion;

        PersonaRequestBuilder() {
        }

        public PersonaRequestBuilder tipoDocumento(TipoDocumento tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
            return this;
        }

        public PersonaRequestBuilder numeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
            return this;
        }

        public PersonaRequestBuilder nombres(String nombres) {
            this.nombres = nombres;
            return this;
        }

        public PersonaRequestBuilder apellidoPaterno(String apellidoPaterno) {
            this.apellidoPaterno = apellidoPaterno;
            return this;
        }

        public PersonaRequestBuilder apellidoMaterno(String apellidoMaterno) {
            this.apellidoMaterno = apellidoMaterno;
            return this;
        }

        public PersonaRequestBuilder fechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
            return this;
        }

        public PersonaRequestBuilder sexo(Sexo sexo) {
            this.sexo = sexo;
            return this;
        }

        public PersonaRequestBuilder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public PersonaRequestBuilder correo(String correo) {
            this.correo = correo;
            return this;
        }

        public PersonaRequestBuilder direccion(String direccion) {
            this.direccion = direccion;
            return this;
        }

        public PersonaRequest build() {
            return new PersonaRequest(tipoDocumento, numeroDocumento, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, sexo, telefono, correo, direccion);
        }
    }
}
