package com.jesusnazareno.asistencia.modules.apoderado.dto;

import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class EstudianteApoderadoRequest {

    @NotNull(message = "El estudiante es obligatorio.")
    private Long estudianteId;

    @NotNull(message = "El apoderado es obligatorio.")
    private Long apoderadoId;

    @NotNull(message = "El parentesco es obligatorio.")
    private Parentesco parentesco;

    private Boolean esPrincipal = Boolean.FALSE;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    public EstudianteApoderadoRequest() {
        this.fechaInicio = LocalDate.now();
    }

    public EstudianteApoderadoRequest(Long estudianteId, Long apoderadoId, Parentesco parentesco, Boolean esPrincipal, LocalDate fechaInicio) {
        this.estudianteId = estudianteId;
        this.apoderadoId = apoderadoId;
        this.parentesco = parentesco;
        this.esPrincipal = esPrincipal != null ? esPrincipal : Boolean.FALSE;
        this.fechaInicio = fechaInicio != null ? fechaInicio : LocalDate.now();
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Long getApoderadoId() {
        return apoderadoId;
    }

    public void setApoderadoId(Long apoderadoId) {
        this.apoderadoId = apoderadoId;
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

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
}
