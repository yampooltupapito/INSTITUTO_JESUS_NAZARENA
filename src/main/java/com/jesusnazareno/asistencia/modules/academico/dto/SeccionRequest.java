package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SeccionRequest {

    @NotNull(message = "{validation.required}")
    private Long gradoId;

    @NotBlank(message = "{validation.required}")
    @Size(max = 50, message = "El nombre de la sección no puede exceder 50 caracteres.")
    private String nombre;

    @NotNull(message = "{validation.required}")
    private Turno turno;

    @NotNull(message = "{validation.required}")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0.")
    private Integer capacidad;

    public SeccionRequest() {
    }

    public SeccionRequest(Long gradoId, String nombre, Turno turno, Integer capacidad) {
        this.gradoId = gradoId;
        this.nombre = nombre;
        this.turno = turno;
        this.capacidad = capacidad;
    }

    public static SeccionRequestBuilder builder() {
        return new SeccionRequestBuilder();
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public static class SeccionRequestBuilder {
        private Long gradoId;
        private String nombre;
        private Turno turno;
        private Integer capacidad;

        SeccionRequestBuilder() {
        }

        public SeccionRequestBuilder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public SeccionRequestBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public SeccionRequestBuilder turno(Turno turno) {
            this.turno = turno;
            return this;
        }

        public SeccionRequestBuilder capacidad(Integer capacidad) {
            this.capacidad = capacidad;
            return this;
        }

        public SeccionRequest build() {
            return new SeccionRequest(gradoId, nombre, turno, capacidad);
        }
    }
}
