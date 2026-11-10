package com.jesusnazareno.asistencia.modules.auditoria.service;

import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaEventoDTO;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaFiltroDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz del servicio central de auditoría funcional y trazabilidad.
 */
public interface AuditoriaService {

    /**
     * Registra un evento de auditoría detectando automáticamente el usuario autenticado y la IP.
     */
    void registrarEventoConContexto(AccionAuditoria accion,
                                    ModuloAuditoria modulo,
                                    String entidadTipo,
                                    Long entidadId,
                                    ResultadoAuditoria resultado,
                                    String descripcion,
                                    String detalles);

    /**
     * Registra un evento de auditoría donde se especifica explícitamente el username y/o IP (por ejemplo en login/logout).
     */
    void registrarEvento(String username,
                         AccionAuditoria accion,
                         ModuloAuditoria modulo,
                         String entidadTipo,
                         Long entidadId,
                         ResultadoAuditoria resultado,
                         String descripcion,
                         String direccionIp,
                         String detalles);

    /**
     * Sobrecarga alternativa para registro con contexto automático.
     */
    void registrarEvento(AccionAuditoria accion,
                         ModuloAuditoria modulo,
                         ResultadoAuditoria resultado,
                         String descripcion,
                         String entidadTipo,
                         Long entidadId,
                         String detalles);

    /**
     * Sobrecarga alternativa con todos los parámetros explícitos.
     */
    void registrarEventoConContexto(String username,
                                    AccionAuditoria accion,
                                    ModuloAuditoria modulo,
                                    ResultadoAuditoria resultado,
                                    String descripcion,
                                    String direccionIp,
                                    String entidadTipo,
                                    Long entidadId,
                                    String detalles);

    /**
     * Lista y filtra eventos de auditoría desde PostgreSQL con paginación.
     */
    Page<AuditoriaEventoDTO> buscarEventos(AuditoriaFiltroDTO filtro, Pageable pageable);

    /**
     * Alias para compatibilidad de interfaz.
     */
    Page<AuditoriaEventoDTO> listarEventos(AuditoriaFiltroDTO filtro, Pageable pageable);

    /**
     * Obtiene el detalle inmutable de un evento específico por su ID.
     */
    AuditoriaEventoDTO buscarPorId(Long id);

    /**
     * Alias para compatibilidad de interfaz.
     */
    AuditoriaEventoDTO obtenerPorId(Long id);
}
