package com.jesusnazareno.asistencia.modules.importacion.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa una fila leída desde la plantilla Excel de Matrículas.
 * NO es entidad JPA y contiene el estado de validación y lista de errores por fila.
 */
public class MatriculaImportRow {

    private int numeroFila;
    private String numeroDocumentoEstudiante;
    private Integer anio;
    private String anioRaw;
    private NivelEducativo nivel;
    private String nivelRaw;
    private String grado;
    private String seccion;
    private String codigoMatricula;
    private LocalDate fechaMatricula;
    private String fechaMatriculaRaw;
    private EstadoMatricula estado = EstadoMatricula.MATRICULADO;
    private String estadoRaw;
    private String observaciones;

    // Campos resueltos durante validación
    private Long personaEstudianteId;
    private String nombreEstudiante;
    private Long anioEscolarId;
    private Long gradoId;
    private Long seccionId;

    private EstadoFilaImportacion estadoFila = EstadoFilaImportacion.VALIDA;
    private List<String> errores = new ArrayList<>();
    private List<String> advertencias = new ArrayList<>();

    public MatriculaImportRow() {
    }

    public MatriculaImportRow(int numeroFila) {
        this.numeroFila = numeroFila;
    }

    public void agregarError(String error) {
        this.errores.add(error);
        this.estadoFila = EstadoFilaImportacion.ERROR;
    }

    public void agregarAdvertencia(String advertencia) {
        this.advertencias.add(advertencia);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public int getNumeroFila() {
        return numeroFila;
    }

    public void setNumeroFila(int numeroFila) {
        this.numeroFila = numeroFila;
    }

    public String getNumeroDocumentoEstudiante() {
        return numeroDocumentoEstudiante;
    }

    public void setNumeroDocumentoEstudiante(String numeroDocumentoEstudiante) {
        this.numeroDocumentoEstudiante = numeroDocumentoEstudiante;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getAnioRaw() {
        return anioRaw;
    }

    public void setAnioRaw(String anioRaw) {
        this.anioRaw = anioRaw;
    }

    public NivelEducativo getNivel() {
        return nivel;
    }

    public void setNivel(NivelEducativo nivel) {
        this.nivel = nivel;
    }

    public String getNivelRaw() {
        return nivelRaw;
    }

    public void setNivelRaw(String nivelRaw) {
        this.nivelRaw = nivelRaw;
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

    public String getCodigoMatricula() {
        return codigoMatricula;
    }

    public void setCodigoMatricula(String codigoMatricula) {
        this.codigoMatricula = codigoMatricula;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public String getFechaMatriculaRaw() {
        return fechaMatriculaRaw;
    }

    public void setFechaMatriculaRaw(String fechaMatriculaRaw) {
        this.fechaMatriculaRaw = fechaMatriculaRaw;
    }

    public EstadoMatricula getEstado() {
        return estado;
    }

    public void setEstado(EstadoMatricula estado) {
        this.estado = estado;
    }

    public String getEstadoRaw() {
        return estadoRaw;
    }

    public void setEstadoRaw(String estadoRaw) {
        this.estadoRaw = estadoRaw;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getPersonaEstudianteId() {
        return personaEstudianteId;
    }

    public void setPersonaEstudianteId(Long personaEstudianteId) {
        this.personaEstudianteId = personaEstudianteId;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public EstadoFilaImportacion getEstadoFila() {
        return estadoFila;
    }

    public void setEstadoFila(EstadoFilaImportacion estadoFila) {
        this.estadoFila = estadoFila;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }

    public List<String> getAdvertencias() {
        return advertencias;
    }

    public void setAdvertencias(List<String> advertencias) {
        this.advertencias = advertencias;
    }
}
