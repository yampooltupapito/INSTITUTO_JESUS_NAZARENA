package com.jesusnazareno.asistencia.modules.reporte.dto;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO que representa una fila de resultados en los reportes de asistencia.
 * Evita la exposición de entidades JPA y optimiza la visualización y exportación.
 */
public class ReporteAsistenciaItem {

    private Integer numero;
    private Long asistenciaId;
    private Long matriculaId;
    private Long estudianteId;
    private String codigoMatricula;
    private String numeroDocumento;
    private String apellidos;
    private String nombres;
    private String estudianteCompleto;
    private String anioEscolar;
    private String grado;
    private String seccion;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String estadoNombre;
    private LocalTime horaIngreso;
    private LocalTime horaSalida;
    private String observacion;

    public ReporteAsistenciaItem() {
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Long getAsistenciaId() {
        return asistenciaId;
    }

    public void setAsistenciaId(Long asistenciaId) {
        this.asistenciaId = asistenciaId;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getCodigoMatricula() {
        return codigoMatricula;
    }

    public void setCodigoMatricula(String codigoMatricula) {
        this.codigoMatricula = codigoMatricula;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEstudianteCompleto() {
        return estudianteCompleto;
    }

    public void setEstudianteCompleto(String estudianteCompleto) {
        this.estudianteCompleto = estudianteCompleto;
    }

    public String getAnioEscolar() {
        return anioEscolar;
    }

    public void setAnioEscolar(String anioEscolar) {
        this.anioEscolar = anioEscolar;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
        if (estado != null) {
            this.estadoNombre = estado.name();
        }
    }

    public String getEstadoNombre() {
        return estadoNombre != null ? estadoNombre : (estado != null ? estado.name() : "NO REGISTRADO");
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public LocalTime getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(LocalTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
