package com.jesusnazareno.asistencia.modules.asistencia.mapper;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.stereotype.Component;

/**
 * Mapeador manual para transformar entre la entidad Asistencia y sus DTOs.
 */
@Component
public class AsistenciaMapper {

    public Asistencia toEntity(AsistenciaRequest request, Matricula matricula, Usuario registradoPor) {
        if (request == null) {
            return null;
        }

        return Asistencia.builder()
                .matricula(matricula)
                .fecha(request.getFecha())
                .estado(request.getEstado())
                .horaIngreso(request.getHoraIngreso())
                .horaSalida(request.getHoraSalida())
                .observacion(request.getObservacion() != null ? request.getObservacion().trim() : null)
                .registradoPor(registradoPor)
                .build();
    }

    public void updateEntity(AsistenciaRequest request, Asistencia asistencia) {
        if (request == null || asistencia == null) {
            return;
        }

        asistencia.setEstado(request.getEstado());
        asistencia.setHoraIngreso(request.getHoraIngreso());
        asistencia.setHoraSalida(request.getHoraSalida());
        asistencia.setObservacion(request.getObservacion() != null ? request.getObservacion().trim() : null);
    }

    public AsistenciaResponse toResponse(Asistencia asistencia) {
        if (asistencia == null) {
            return null;
        }

        Long matriculaId = null;
        Long estudianteId = null;
        String estudianteNombre = null;
        String estudianteDoc = null;
        String gradoNombre = null;
        String seccionNombre = null;

        Matricula matricula = asistencia.getMatricula();
        if (matricula != null) {
            matriculaId = matricula.getId();
            Persona estudiante = matricula.getEstudiante();
            if (estudiante != null) {
                estudianteId = estudiante.getId();
                estudianteDoc = estudiante.getNumeroDocumento();
                String pat = estudiante.getApellidoPaterno() != null ? estudiante.getApellidoPaterno() : "";
                String mat = estudiante.getApellidoMaterno() != null ? estudiante.getApellidoMaterno() : "";
                String nom = estudiante.getNombres() != null ? estudiante.getNombres() : "";
                estudianteNombre = (pat + " " + mat).trim() + ", " + nom;
            }

            if (matricula.getGrado() != null) {
                gradoNombre = matricula.getGrado().getNombre();
            }

            if (matricula.getSeccion() != null) {
                seccionNombre = matricula.getSeccion().getNombre();
            }
        }

        Long registradoPorId = asistencia.getRegistradoPor() != null ? asistencia.getRegistradoPor().getId() : null;

        return AsistenciaResponse.builder()
                .id(asistencia.getId())
                .matriculaId(matriculaId)
                .estudianteId(estudianteId)
                .estudianteNombreCompleto(estudianteNombre)
                .estudianteNumeroDocumento(estudianteDoc)
                .gradoNombre(gradoNombre)
                .seccionNombre(seccionNombre)
                .fecha(asistencia.getFecha())
                .estado(asistencia.getEstado())
                .horaIngreso(asistencia.getHoraIngreso())
                .horaSalida(asistencia.getHoraSalida())
                .observacion(asistencia.getObservacion())
                .registradoPor(registradoPorId)
                .createdAt(asistencia.getCreatedAt())
                .updatedAt(asistencia.getUpdatedAt())
                .build();
    }
}
