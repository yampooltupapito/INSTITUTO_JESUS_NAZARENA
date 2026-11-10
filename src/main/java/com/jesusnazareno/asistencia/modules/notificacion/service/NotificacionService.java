package com.jesusnazareno.asistencia.modules.notificacion.service;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.notificacion.domain.Notificacion;
import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;
import com.jesusnazareno.asistencia.modules.notificacion.dto.NotificacionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrato de servicio para la gestión de notificaciones internas del sistema.
 */
public interface NotificacionService {

    NotificacionResponse crearNotificacion(
            Usuario usuario,
            TipoNotificacion tipo,
            String titulo,
            String mensaje,
            String referenciaTipo,
            Long referenciaId,
            String urlDestino
    );

    Page<NotificacionResponse> listarDelUsuario(
            Long usuarioId,
            Boolean leida,
            TipoNotificacion tipo,
            Pageable pageable
    );

    long contarNoLeidas(Long usuarioId);

    List<NotificacionResponse> obtenerUltimasNoLeidas(Long usuarioId, int limite);

    List<NotificacionResponse> obtenerUltimasDelUsuario(Long usuarioId, int limite);

    NotificacionResponse buscarDelUsuario(Long id, Long usuarioId);

    NotificacionResponse marcarComoLeida(Long id, Long usuarioId);

    int marcarTodasComoLeidas(Long usuarioId);

    void crearNotificacionAlerta(Long alertaId);

    void crearNotificacionJustificacion(Long justificacionId, TipoNotificacion tipo);
}
