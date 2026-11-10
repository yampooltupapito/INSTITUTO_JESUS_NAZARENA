package com.jesusnazareno.asistencia.modules.alerta.mapper;

import com.jesusnazareno.asistencia.modules.alerta.domain.AlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.dto.AlertaAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre AlertaAsistencia y AlertaAsistenciaResponse.
 */
@Component
public class AlertaAsistenciaMapper {

    public AlertaAsistenciaResponse toResponse(AlertaAsistencia entity) {
        if (entity == null) {
            return null;
        }

        Matricula m = entity.getMatricula();
        Persona e = (m != null) ? m.getEstudiante() : null;

        String nombreCompleto = null;
        String documento = null;
        if (e != null) {
            documento = e.getNumeroDocumento();
            nombreCompleto = String.format("%s %s, %s",
                    e.getApellidoPaterno() != null ? e.getApellidoPaterno() : "",
                    e.getApellidoMaterno() != null ? e.getApellidoMaterno() : "",
                    e.getNombres() != null ? e.getNombres() : "").trim();
        }

        String revisorNombre = null;
        if (entity.getRevisadoPor() != null) {
            if (entity.getRevisadoPor().getPersona() != null) {
                Persona pr = entity.getRevisadoPor().getPersona();
                revisorNombre = String.format("%s %s", pr.getNombres(), pr.getApellidoPaterno());
            } else {
                revisorNombre = entity.getRevisadoPor().getUsername();
            }
        }

        return AlertaAsistenciaResponse.builder()
                .id(entity.getId())
                .matriculaId(m != null ? m.getId() : null)
                .codigoMatricula(m != null ? m.getCodigoMatricula() : null)
                .estudianteId(e != null ? e.getId() : null)
                .estudianteDocumento(documento)
                .estudianteNombreCompleto(nombreCompleto)
                .anioEscolarId(m != null && m.getAnioEscolar() != null ? m.getAnioEscolar().getId() : null)
                .anioEscolarAnio(m != null && m.getAnioEscolar() != null ? m.getAnioEscolar().getAnio() : null)
                .anioEscolarNombre(m != null && m.getAnioEscolar() != null ? m.getAnioEscolar().getNombre() : null)
                .gradoId(m != null && m.getGrado() != null ? m.getGrado().getId() : null)
                .gradoNombre(m != null && m.getGrado() != null ? m.getGrado().getNombre() : null)
                .seccionId(m != null && m.getSeccion() != null ? m.getSeccion().getId() : null)
                .seccionNombre(m != null && m.getSeccion() != null ? m.getSeccion().getNombre() : null)
                .tipo(entity.getTipo())
                .tipoDescripcion(entity.getTipo() != null ? entity.getTipo().getDescripcion() : null)
                .fechaDeteccion(entity.getFechaDeteccion())
                .fechaInicioPeriodo(entity.getFechaInicioPeriodo())
                .fechaFinPeriodo(entity.getFechaFinPeriodo())
                .cantidad(entity.getCantidad())
                .estado(entity.getEstado())
                .estadoDescripcion(entity.getEstado() != null ? entity.getEstado().getDescripcion() : null)
                .descripcion(entity.getDescripcion())
                .revisadoPorId(entity.getRevisadoPor() != null ? entity.getRevisadoPor().getId() : null)
                .revisadoPorNombre(revisorNombre)
                .fechaRevision(entity.getFechaRevision())
                .observacionRevision(entity.getObservacionRevision())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
