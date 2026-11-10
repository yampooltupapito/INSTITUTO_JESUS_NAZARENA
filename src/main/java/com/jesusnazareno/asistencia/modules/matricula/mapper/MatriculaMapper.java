package com.jesusnazareno.asistencia.modules.matricula.mapper;

import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaResponse;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.stereotype.Component;

/**
 * Mapeador manual para transformar entre entidades Matricula y DTOs correspondientes.
 */
@Component
public class MatriculaMapper {

    public Matricula toEntity(MatriculaRequest request, Persona estudiante, AnioEscolar anioEscolar, Grado grado, Seccion seccion) {
        if (request == null) {
            return null;
        }

        EstadoMatricula estado = request.getEstado() != null ? request.getEstado() : EstadoMatricula.MATRICULADO;

        return Matricula.builder()
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula(request.getCodigoMatricula() != null ? request.getCodigoMatricula().trim() : null)
                .fechaMatricula(request.getFechaMatricula())
                .estado(estado)
                .observaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null)
                .build();
    }

    public void updateEntity(MatriculaRequest request, Matricula matricula, Persona estudiante, AnioEscolar anioEscolar, Grado grado, Seccion seccion) {
        if (request == null || matricula == null) {
            return;
        }

        matricula.setEstudiante(estudiante);
        matricula.setAnioEscolar(anioEscolar);
        matricula.setGrado(grado);
        matricula.setSeccion(seccion);
        matricula.setCodigoMatricula(request.getCodigoMatricula() != null ? request.getCodigoMatricula().trim() : null);
        matricula.setFechaMatricula(request.getFechaMatricula());
        if (request.getEstado() != null) {
            matricula.setEstado(request.getEstado());
        }
        matricula.setObservaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null);
    }

    public MatriculaResponse toResponse(Matricula matricula) {
        if (matricula == null) {
            return null;
        }

        String nombreCompleto = null;
        String numeroDoc = null;
        Long estudianteId = null;
        if (matricula.getEstudiante() != null) {
            estudianteId = matricula.getEstudiante().getId();
            numeroDoc = matricula.getEstudiante().getNumeroDocumento();
            String apePaterno = matricula.getEstudiante().getApellidoPaterno() != null ? matricula.getEstudiante().getApellidoPaterno() : "";
            String apeMaterno = matricula.getEstudiante().getApellidoMaterno() != null ? matricula.getEstudiante().getApellidoMaterno() : "";
            String nombres = matricula.getEstudiante().getNombres() != null ? matricula.getEstudiante().getNombres() : "";
            nombreCompleto = (apePaterno + " " + apeMaterno).trim() + ", " + nombres;
        }

        Long anioId = matricula.getAnioEscolar() != null ? matricula.getAnioEscolar().getId() : null;
        Integer anioNum = matricula.getAnioEscolar() != null ? matricula.getAnioEscolar().getAnio() : null;

        Long gradoId = matricula.getGrado() != null ? matricula.getGrado().getId() : null;
        String gradoNom = matricula.getGrado() != null ? matricula.getGrado().getNombre() : null;

        Long seccionId = matricula.getSeccion() != null ? matricula.getSeccion().getId() : null;
        String seccionNom = matricula.getSeccion() != null ? matricula.getSeccion().getNombre() : null;
        String seccionTur = (matricula.getSeccion() != null && matricula.getSeccion().getTurno() != null)
                ? matricula.getSeccion().getTurno().name() : null;

        return MatriculaResponse.builder()
                .id(matricula.getId())
                .personaEstudianteId(estudianteId)
                .estudianteNombreCompleto(nombreCompleto)
                .estudianteNumeroDocumento(numeroDoc)
                .anioEscolarId(anioId)
                .anioEscolarAnio(anioNum)
                .gradoId(gradoId)
                .gradoNombre(gradoNom)
                .seccionId(seccionId)
                .seccionNombre(seccionNom)
                .seccionTurno(seccionTur)
                .codigoMatricula(matricula.getCodigoMatricula())
                .fechaMatricula(matricula.getFechaMatricula())
                .estado(matricula.getEstado())
                .observaciones(matricula.getObservaciones())
                .createdAt(matricula.getCreatedAt())
                .updatedAt(matricula.getUpdatedAt())
                .createdBy(matricula.getCreatedBy())
                .updatedBy(matricula.getUpdatedBy())
                .build();
    }
}
