package com.jesusnazareno.asistencia.modules.auditoria.domain.enums;

/**
 * Catálogo controlado de acciones auditables en el sistema.
 */
public enum AccionAuditoria {
    // Seguridad y Sesión
    LOGIN_EXITOSO,
    LOGIN_FALLIDO,
    LOGOUT,

    // Usuarios
    USUARIO_CREAR,
    USUARIO_EDITAR,
    USUARIO_ACTIVAR,
    USUARIO_DESACTIVAR,
    USUARIO_CAMBIAR_PASSWORD,

    // Matrículas
    MATRICULA_CREAR,
    MATRICULA_EDITAR,
    MATRICULA_CAMBIAR_ESTADO,

    // Asistencia
    ASISTENCIA_REGISTRAR,
    ASISTENCIA_EDITAR,
    ASISTENCIA_REGISTRO_MASIVO,

    // Justificaciones
    JUSTIFICACION_CREAR,
    JUSTIFICACION_APROBAR,
    JUSTIFICACION_RECHAZAR,

    // Horarios y Calendario
    HORARIO_CREAR,
    HORARIO_EDITAR,
    CALENDARIO_CREAR,
    CALENDARIO_EDITAR,

    // Alertas
    ALERTA_REVISAR,
    ALERTA_RESOLVER,
    ALERTA_CONFIGURAR,

    // Importaciones
    IMPORTACION_ESTUDIANTES,
    IMPORTACION_MATRICULAS,

    // Exportación de Reportes
    REPORTE_PDF,
    REPORTE_EXCEL,
    REPORTE_CSV
}
