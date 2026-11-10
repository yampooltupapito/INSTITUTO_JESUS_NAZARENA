package com.jesusnazareno.asistencia.modules.academico.mapper;

import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.CalendarioEscolar;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarResponse;
import org.springframework.stereotype.Component;

import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Mapper para convertir entre la entidad CalendarioEscolar y sus DTOs.
 */
@Component
public class CalendarioEscolarMapper {

    private static final Locale LOCALE_PE = new Locale("es", "PE");

    public CalendarioEscolar toEntity(CalendarioEscolarRequest request, AnioEscolar anioEscolar) {
        if (request == null) {
            return null;
        }
        return CalendarioEscolar.builder()
                .anioEscolar(anioEscolar)
                .fecha(request.getFecha())
                .tipoDia(request.getTipoDia())
                .descripcion(request.getDescripcion())
                .esLectivo(request.getEsLectivo() != null ? request.getEsLectivo() : false)
                .activo(true)
                .build();
    }

    public void updateEntityFromRequest(CalendarioEscolar entity, CalendarioEscolarRequest request, AnioEscolar anioEscolar) {
        if (entity == null || request == null) {
            return;
        }
        entity.setAnioEscolar(anioEscolar);
        entity.setFecha(request.getFecha());
        entity.setTipoDia(request.getTipoDia());
        entity.setDescripcion(request.getDescripcion());
        entity.setEsLectivo(request.getEsLectivo() != null ? request.getEsLectivo() : false);
    }

    public CalendarioEscolarResponse toResponse(CalendarioEscolar entity) {
        if (entity == null) {
            return null;
        }

        String diaSemana = null;
        if (entity.getFecha() != null) {
            String dia = entity.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, LOCALE_PE);
            diaSemana = dia.substring(0, 1).toUpperCase(LOCALE_PE) + dia.substring(1);
        }

        return CalendarioEscolarResponse.builder()
                .id(entity.getId())
                .anioEscolarId(entity.getAnioEscolar() != null ? entity.getAnioEscolar().getId() : null)
                .anioEscolarAnio(entity.getAnioEscolar() != null ? entity.getAnioEscolar().getAnio() : null)
                .anioEscolarNombre(entity.getAnioEscolar() != null ? entity.getAnioEscolar().getNombre() : null)
                .fecha(entity.getFecha())
                .diaSemana(diaSemana)
                .tipoDia(entity.getTipoDia())
                .tipoDiaDescripcion(entity.getTipoDia() != null ? entity.getTipoDia().getDescripcion() : null)
                .descripcion(entity.getDescripcion())
                .esLectivo(entity.getEsLectivo())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
