package com.jesusnazareno.asistencia.modules.persona.dto;

import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta estructurada de datos de persona.
 */
public class PersonaResponse {

    private Long id;
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
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PersonaResponse() {
    }

    public PersonaResponse(Long id, TipoDocumento tipoDocumento, String numeroDocumento, String nombres,
                           String apellidoPaterno, String apellidoMaterno, LocalDate fechaNacimiento, Sexo sexo,
                           String telefono, String correo, String direccion, Boolean activo,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
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
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PersonaResponseBuilder builder() {
        return new PersonaResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public static class PersonaResponseBuilder {
        private Long id;
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
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        PersonaResponseBuilder() {
        }

        public PersonaResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PersonaResponseBuilder tipoDocumento(TipoDocumento tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
            return this;
        }

        public PersonaResponseBuilder numeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
            return this;
        }

        public PersonaResponseBuilder nombres(String nombres) {
            this.nombres = nombres;
            return this;
        }

        public PersonaResponseBuilder apellidoPaterno(String apellidoPaterno) {
            this.apellidoPaterno = apellidoPaterno;
            return this;
        }

        public PersonaResponseBuilder apellidoMaterno(String apellidoMaterno) {
            this.apellidoMaterno = apellidoMaterno;
            return this;
        }

        public PersonaResponseBuilder fechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
            return this;
        }

        public PersonaResponseBuilder sexo(Sexo sexo) {
            this.sexo = sexo;
            return this;
        }

        public PersonaResponseBuilder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public PersonaResponseBuilder correo(String correo) {
            this.correo = correo;
            return this;
        }

        public PersonaResponseBuilder direccion(String direccion) {
            this.direccion = direccion;
            return this;
        }

        public PersonaResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public PersonaResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PersonaResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PersonaResponse build() {
            return new PersonaResponse(id, tipoDocumento, numeroDocumento, nombres, apellidoPaterno, apellidoMaterno,
                    fechaNacimiento, sexo, telefono, correo, direccion, activo, createdAt, updatedAt);
        }
    }
}
