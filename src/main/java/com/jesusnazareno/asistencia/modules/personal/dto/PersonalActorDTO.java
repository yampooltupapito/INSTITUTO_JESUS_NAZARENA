package com.jesusnazareno.asistencia.modules.personal.dto;

import java.time.LocalDate;

/**
 * DTO que representa la asignación de una función institucional (PersonaActor)
 * de un miembro del personal.
 */
public class PersonalActorDTO {

    private Long id;
    private Long tipoActorId;
    private String tipoActorCodigo;
    private String tipoActorNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;

    public PersonalActorDTO() {
    }

    public PersonalActorDTO(Long id, Long tipoActorId, String tipoActorCodigo, String tipoActorNombre,
                            LocalDate fechaInicio, LocalDate fechaFin, Boolean activo) {
        this.id = id;
        this.tipoActorId = tipoActorId;
        this.tipoActorCodigo = tipoActorCodigo;
        this.tipoActorNombre = tipoActorNombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTipoActorId() {
        return tipoActorId;
    }

    public void setTipoActorId(Long tipoActorId) {
        this.tipoActorId = tipoActorId;
    }

    public String getTipoActorCodigo() {
        return tipoActorCodigo;
    }

    public void setTipoActorCodigo(String tipoActorCodigo) {
        this.tipoActorCodigo = tipoActorCodigo;
    }

    public String getTipoActorNombre() {
        return tipoActorNombre;
    }

    public void setTipoActorNombre(String tipoActorNombre) {
        this.tipoActorNombre = tipoActorNombre;
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
}
