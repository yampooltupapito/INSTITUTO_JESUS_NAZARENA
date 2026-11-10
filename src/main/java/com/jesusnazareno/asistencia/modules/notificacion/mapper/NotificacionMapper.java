package com.jesusnazareno.asistencia.modules.notificacion.mapper;

import com.jesusnazareno.asistencia.modules.notificacion.domain.Notificacion;
import com.jesusnazareno.asistencia.modules.notificacion.dto.NotificacionResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Mapper para transformar Notificacion a NotificacionResponse.
 */
@Component
public class NotificacionMapper {

    private static final ZoneId ZONE_LIMA = ZoneId.of("America/Lima");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotificacionResponse toResponse(Notificacion entity) {
        if (entity == null) {
            return null;
        }

        String fechaLecturaFormateada = entity.getFechaLectura() != null
                ? entity.getFechaLectura().format(DATE_TIME_FORMATTER)
                : null;

        String fechaCreacionFormateada = entity.getCreatedAt() != null
                ? entity.getCreatedAt().format(DATE_TIME_FORMATTER)
                : null;

        String tiempoRelativo = calcularTiempoRelativo(entity.getCreatedAt());

        return NotificacionResponse.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .tipo(entity.getTipo())
                .tipoDescripcion(entity.getTipo() != null ? entity.getTipo().getDescripcion() : null)
                .titulo(entity.getTitulo())
                .mensaje(entity.getMensaje())
                .referenciaTipo(entity.getReferenciaTipo())
                .referenciaId(entity.getReferenciaId())
                .urlDestino(entity.getUrlDestino())
                .leida(entity.getLeida())
                .fechaLectura(entity.getFechaLectura())
                .fechaLecturaFormateada(fechaLecturaFormateada)
                .createdAt(entity.getCreatedAt())
                .fechaCreacionFormateada(fechaCreacionFormateada)
                .tiempoRelativo(tiempoRelativo)
                .build();
    }

    private String calcularTiempoRelativo(LocalDateTime fecha) {
        if (fecha == null) {
            return "";
        }
        LocalDateTime ahora = LocalDateTime.now(ZONE_LIMA);
        Duration duracion = Duration.between(fecha, ahora);
        long segundos = Math.max(0, duracion.getSeconds());

        if (segundos < 60) {
            return "Hace un momento";
        }
        long minutos = segundos / 60;
        if (minutos < 60) {
            return "Hace " + minutos + " min";
        }
        long horas = minutos / 60;
        if (horas < 24) {
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        }
        long dias = horas / 24;
        if (dias < 30) {
            return "Hace " + dias + (dias == 1 ? " día" : " días");
        }
        return fecha.format(DATE_TIME_FORMATTER);
    }
}
