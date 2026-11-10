package com.jesusnazareno.asistencia.modules.matricula.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;

/**
 * DTO ligero para transferir la capacidad y disponibilidad de vacantes de una sección.
 */
public class SeccionCapacidadDTO {

    private Long id;
    private String nombre;
    private Turno turno;
    private Integer capacidad;
    private Long matriculados;
    private Long vacantes;

    public SeccionCapacidadDTO() {
    }

    public SeccionCapacidadDTO(Long id, String nombre, Turno turno, Integer capacidad, Long matriculados, Long vacantes) {
        this.id = id;
        this.nombre = nombre;
        this.turno = turno;
        this.capacidad = capacidad;
        this.matriculados = matriculados;
        this.vacantes = vacantes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Long matriculados) {
        this.matriculados = matriculados;
    }

    public Long getVacantes() {
        return vacantes;
    }

    public void setVacantes(Long vacantes) {
        this.vacantes = vacantes;
    }
}
