package com.jesusnazareno.asistencia.modules.academico.mapper;

import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.HorarioAsistencia;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre la entidad HorarioAsistencia y sus DTOs.
 */
@Component
public class HorarioAsistenciaMapper {

    public HorarioAsistencia toEntity(HorarioAsistenciaRequest request, AnioEscolar anioEscolar, Grado grado, Seccion seccion) {
        if (request == null) {
            return null;
        }
        return HorarioAsistencia.builder()
                .anioEscolar(anioEscolar)
                .turno(request.getTurno())
                .grado(grado)
                .seccion(seccion)
                .horaEntrada(request.getHoraEntrada())
                .minutosTolerancia(request.getMinutosTolerancia() != null ? request.getMinutosTolerancia() : 0)
                .horaSalida(request.getHoraSalida())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activo(true)
                .build();
    }

    public HorarioAsistenciaResponse toResponse(HorarioAsistencia entity) {
        if (entity == null) {
            return null;
        }

        String alcance;
        if (entity.getSeccion() != null) {
            alcance = "Sección: " + entity.getSeccion().getNombre() +
                    (entity.getGrado() != null ? " (" + entity.getGrado().getNombre() + ")" : "");
        } else if (entity.getGrado() != null) {
            alcance = "Grado: " + entity.getGrado().getNombre() + " (" + entity.getGrado().getNivel() + ")";
        } else {
            alcance = "General";
        }

        return HorarioAsistenciaResponse.builder()
                .id(entity.getId())
                .anioEscolarId(entity.getAnioEscolar() != null ? entity.getAnioEscolar().getId() : null)
                .anio(entity.getAnioEscolar() != null ? entity.getAnioEscolar().getAnio() : null)
                .anioNombre(entity.getAnioEscolar() != null ? entity.getAnioEscolar().getNombre() : null)
                .turno(entity.getTurno())
                .gradoId(entity.getGrado() != null ? entity.getGrado().getId() : null)
                .gradoNombre(entity.getGrado() != null ? entity.getGrado().getNombre() : null)
                .seccionId(entity.getSeccion() != null ? entity.getSeccion().getId() : null)
                .seccionNombre(entity.getSeccion() != null ? entity.getSeccion().getNombre() : null)
                .horaEntrada(entity.getHoraEntrada())
                .minutosTolerancia(entity.getMinutosTolerancia())
                .horaLimite(entity.getHoraLimite())
                .horaSalida(entity.getHoraSalida())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .activo(entity.getActivo())
                .alcance(alcance)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntity(HorarioAsistenciaRequest request, HorarioAsistencia entity,
                             AnioEscolar anioEscolar, Grado grado, Seccion seccion) {
        if (request == null || entity == null) {
            return;
        }
        entity.setAnioEscolar(anioEscolar);
        entity.setTurno(request.getTurno());
        entity.setGrado(grado);
        entity.setSeccion(seccion);
        entity.setHoraEntrada(request.getHoraEntrada());
        entity.setMinutosTolerancia(request.getMinutosTolerancia() != null ? request.getMinutosTolerancia() : 0);
        entity.setHoraSalida(request.getHoraSalida());
        entity.setFechaInicio(request.getFechaInicio());
        entity.setFechaFin(request.getFechaFin());
    }
}
