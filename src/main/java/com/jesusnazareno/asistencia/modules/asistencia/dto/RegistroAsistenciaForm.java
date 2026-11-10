package com.jesusnazareno.asistencia.modules.asistencia.dto;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de formulario para el registro masivo de asistencia de una seccion.
 * NO es una entidad JPA. Se usa unicamente en la capa web.
 */
public class RegistroAsistenciaForm {

    @NotNull(message = "{validation.required}")
    private LocalDate fecha;

    @NotNull(message = "{validation.required}")
    private Long anioEscolarId;

    @NotNull(message = "{validation.required}")
    private Long gradoId;

    @NotNull(message = "{validation.required}")
    private Long seccionId;

    private List<RegistroItem> registros = new ArrayList<>();

    public RegistroAsistenciaForm() {}

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getAnioEscolarId() { return anioEscolarId; }
    public void setAnioEscolarId(Long anioEscolarId) { this.anioEscolarId = anioEscolarId; }
    public Long getGradoId() { return gradoId; }
    public void setGradoId(Long gradoId) { this.gradoId = gradoId; }
    public Long getSeccionId() { return seccionId; }
    public void setSeccionId(Long seccionId) { this.seccionId = seccionId; }
    public List<RegistroItem> getRegistros() { return registros; }
    public void setRegistros(List<RegistroItem> registros) { this.registros = registros != null ? registros : new ArrayList<>(); }

    public static class RegistroItem {
        private Long matriculaId;
        private String estudianteNombre;
        private String estudianteDocumento;
        private EstadoAsistencia estado;
        private LocalTime horaIngreso;
        private String observacion;
        private boolean registradoPreviamente;
        private EstadoAsistencia estadoPrevio;
        private Long idRegistroPrevio;

        public RegistroItem() {}
        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }
        public String getEstudianteNombre() { return estudianteNombre; }
        public void setEstudianteNombre(String estudianteNombre) { this.estudianteNombre = estudianteNombre; }
        public String getEstudianteDocumento() { return estudianteDocumento; }
        public void setEstudianteDocumento(String estudianteDocumento) { this.estudianteDocumento = estudianteDocumento; }
        public EstadoAsistencia getEstado() { return estado; }
        public void setEstado(EstadoAsistencia estado) { this.estado = estado; }
        public LocalTime getHoraIngreso() { return horaIngreso; }
        public void setHoraIngreso(LocalTime horaIngreso) { this.horaIngreso = horaIngreso; }
        public String getObservacion() { return observacion; }
        public void setObservacion(String observacion) { this.observacion = observacion; }
        public boolean isRegistradoPreviamente() { return registradoPreviamente; }
        public void setRegistradoPreviamente(boolean registradoPreviamente) { this.registradoPreviamente = registradoPreviamente; }
        public EstadoAsistencia getEstadoPrevio() { return estadoPrevio; }
        public void setEstadoPrevio(EstadoAsistencia estadoPrevio) { this.estadoPrevio = estadoPrevio; }
        public Long getIdRegistroPrevio() { return idRegistroPrevio; }
        public void setIdRegistroPrevio(Long idRegistroPrevio) { this.idRegistroPrevio = idRegistroPrevio; }
    }
}
