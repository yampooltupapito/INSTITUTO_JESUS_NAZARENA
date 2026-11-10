package com.jesusnazareno.asistencia.modules.asistencia.mapper;

import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionResponse;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Mapeador manual para transformar entre entidades Justificacion y sus DTOs.
 */
@Component
public class JustificacionMapper {

    public Justificacion toEntity(JustificacionRequest request, Asistencia asistencia) {
        if (request == null) {
            return null;
        }

        return Justificacion.builder()
                .asistencia(asistencia)
                .motivo(request.getMotivo() != null ? request.getMotivo().trim() : null)
                .descripcion(request.getDescripcion() != null ? request.getDescripcion().trim() : null)
                .fechaSolicitud(LocalDate.now())
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();
    }

    public JustificacionResponse toResponse(Justificacion justificacion) {
        if (justificacion == null) {
            return null;
        }

        Long asistenciaId = null;
        LocalDate asistenciaFecha = null;
        EstadoAsistencia asistenciaEstado = null;
        Long estudianteId = null;
        String estudianteNombre = null;
        String estudianteDocumento = null;
        String gradoNombre = null;
        String seccionNombre = null;
        String anioEscolarNombre = null;

        Asistencia asistencia = justificacion.getAsistencia();
        if (asistencia != null) {
            asistenciaId = asistencia.getId();
            asistenciaFecha = asistencia.getFecha();
            asistenciaEstado = asistencia.getEstado();

            Matricula matricula = asistencia.getMatricula();
            if (matricula != null) {
                if (matricula.getEstudiante() != null) {
                    Persona estudiante = matricula.getEstudiante();
                    estudianteId = estudiante.getId();
                    String pat = estudiante.getApellidoPaterno() != null ? estudiante.getApellidoPaterno() : "";
                    String mat = estudiante.getApellidoMaterno() != null ? estudiante.getApellidoMaterno() : "";
                    String nom = estudiante.getNombres() != null ? estudiante.getNombres() : "";
                    estudianteNombre = (pat + " " + mat).trim() + ", " + nom;
                    estudianteDocumento = estudiante.getNumeroDocumento();
                }
                if (matricula.getGrado() != null) {
                    gradoNombre = matricula.getGrado().getNombre();
                }
                if (matricula.getSeccion() != null) {
                    seccionNombre = matricula.getSeccion().getNombre();
                }
                if (matricula.getAnioEscolar() != null) {
                    anioEscolarNombre = matricula.getAnioEscolar().getNombre();
                }
            }
        }

        Long revisadoPorId = null;
        String revisadoPorNombre = null;
        String revisadoPorUsername = null;
        if (justificacion.getRevisadoPor() != null) {
            var revisor = justificacion.getRevisadoPor();
            revisadoPorId = revisor.getId();
            revisadoPorUsername = revisor.getUsername();
            if (revisor.getPersona() != null) {
                Persona rp = revisor.getPersona();
                String pat = rp.getApellidoPaterno() != null ? rp.getApellidoPaterno() : "";
                String mat = rp.getApellidoMaterno() != null ? rp.getApellidoMaterno() : "";
                String nom = rp.getNombres() != null ? rp.getNombres() : "";
                revisadoPorNombre = (pat + " " + mat).trim() + ", " + nom;
            } else {
                revisadoPorNombre = revisor.getUsername();
            }
        }

        return JustificacionResponse.builder()
                .id(justificacion.getId())
                .asistenciaId(asistenciaId)
                .estudianteId(estudianteId)
                .estudianteNombreCompleto(estudianteNombre)
                .estudianteNumeroDocumento(estudianteDocumento)
                .gradoNombre(gradoNombre)
                .seccionNombre(seccionNombre)
                .anioEscolarNombre(anioEscolarNombre)
                .asistenciaFecha(asistenciaFecha)
                .asistenciaEstado(asistenciaEstado)
                .motivo(justificacion.getMotivo())
                .descripcion(justificacion.getDescripcion())
                .fechaSolicitud(justificacion.getFechaSolicitud())
                .estado(justificacion.getEstado())
                .observacionRevision(justificacion.getObservacionRevision())
                .revisadoPor(revisadoPorId)
                .revisadoPorNombre(revisadoPorNombre)
                .revisadoPorUsername(revisadoPorUsername)
                .fechaRevision(justificacion.getFechaRevision())
                .activo(justificacion.getActivo())
                .createdAt(justificacion.getCreatedAt())
                .updatedAt(justificacion.getUpdatedAt())
                .createdBy(justificacion.getCreatedBy())
                .updatedBy(justificacion.getUpdatedBy())
                .build();
    }
}
