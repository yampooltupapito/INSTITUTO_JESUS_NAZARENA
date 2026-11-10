package com.jesusnazareno.asistencia.modules.persona.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla 'persona' en la base de datos.
 */
@Entity
@Table(name = "persona")
public class Persona extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 100)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, length = 10)
    private Sexo sexo;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Persona() {
    }

    public Persona(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                   TipoDocumento tipoDocumento, String numeroDocumento, String nombres, String apellidoPaterno,
                   String apellidoMaterno, LocalDate fechaNacimiento, Sexo sexo, String telefono, String correo,
                   String direccion, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
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
    }

    public static PersonaBuilder builder() {
        return new PersonaBuilder();
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

    public static class PersonaBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
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
        private Boolean activo = true;

        PersonaBuilder() {
        }

        public PersonaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PersonaBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PersonaBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PersonaBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public PersonaBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public PersonaBuilder tipoDocumento(TipoDocumento tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
            return this;
        }

        public PersonaBuilder numeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
            return this;
        }

        public PersonaBuilder nombres(String nombres) {
            this.nombres = nombres;
            return this;
        }

        public PersonaBuilder apellidoPaterno(String apellidoPaterno) {
            this.apellidoPaterno = apellidoPaterno;
            return this;
        }

        public PersonaBuilder apellidoMaterno(String apellidoMaterno) {
            this.apellidoMaterno = apellidoMaterno;
            return this;
        }

        public PersonaBuilder fechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
            return this;
        }

        public PersonaBuilder sexo(Sexo sexo) {
            this.sexo = sexo;
            return this;
        }

        public PersonaBuilder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public PersonaBuilder correo(String correo) {
            this.correo = correo;
            return this;
        }

        public PersonaBuilder direccion(String direccion) {
            this.direccion = direccion;
            return this;
        }

        public PersonaBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Persona build() {
            return new Persona(id, createdAt, updatedAt, createdBy, updatedBy, tipoDocumento, numeroDocumento,
                    nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, sexo, telefono, correo, direccion, activo);
        }
    }
}
