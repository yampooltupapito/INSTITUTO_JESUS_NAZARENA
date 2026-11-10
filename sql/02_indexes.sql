-- =============================================================================
-- SISTEMA WEB DE CONTROL DE ASISTENCIA - INSTITUCIÓN EDUCATIVA JESÚS NAZARENO
-- Script 02: Creación de Índices Secundarios para Optimización de Consultas
-- Motor: PostgreSQL 14+
-- =============================================================================

-- 1. Búsqueda de personas por correo electrónico
CREATE INDEX idx_persona_correo ON persona(correo);

-- 2. Búsqueda de usuario por persona asociada
CREATE INDEX idx_usuario_persona_id ON usuario(persona_id);

-- 3. Búsqueda de relaciones persona-actor (persona_id está cubierto como columna líder por uk_persona_tipo_actor)

-- 4. Búsqueda de personas por tipo de actor
CREATE INDEX idx_persona_actor_tipo_actor_id ON persona_actor(tipo_actor_id);

-- 5. Búsqueda de roles que poseen un permiso específico
CREATE INDEX idx_rol_permiso_permiso_id ON rol_permiso(permiso_id);

-- 6. Filtro de matrículas por año escolar
CREATE INDEX idx_matricula_anio_escolar_id ON matricula(anio_escolar_id);

-- 7. Filtro de matrículas por grado
CREATE INDEX idx_matricula_grado_id ON matricula(grado_id);

-- 8. Filtro de matrículas por sección
CREATE INDEX idx_matricula_seccion_id ON matricula(seccion_id);

-- 9. Consultas y reportes de asistencia filtrados por fecha
CREATE INDEX idx_asistencia_fecha ON asistencia(fecha);

-- 10. Auditoría e informes de asistencia registrados por usuario
CREATE INDEX idx_asistencia_registrado_por ON asistencia(registrado_por);

-- 11. Filtro de justificaciones por asistencia
CREATE INDEX idx_justificacion_asistencia_id ON justificacion(asistencia_id);

-- 12. Filtro de justificaciones por estado
CREATE INDEX idx_justificacion_estado ON justificacion(estado);

-- 13. Filtro de justificaciones por fecha de solicitud
CREATE INDEX idx_justificacion_fecha_solicitud ON justificacion(fecha_solicitud);

-- 14. Unicidad de justificación activa por asistencia
CREATE UNIQUE INDEX uk_justificacion_asistencia_activa ON justificacion(asistencia_id) WHERE activo = TRUE;

-- 15. Búsqueda de relaciones estudiante-apoderado por estudiante
CREATE INDEX idx_estudiante_apoderado_estudiante_id ON estudiante_apoderado(estudiante_id);

-- 16. Búsqueda de relaciones estudiante-apoderado por apoderado
CREATE INDEX idx_estudiante_apoderado_apoderado_id ON estudiante_apoderado(apoderado_id);

-- 17. Unicidad de apoderado principal activo por estudiante
CREATE UNIQUE INDEX uk_estudiante_apoderado_principal ON estudiante_apoderado(estudiante_id) WHERE es_principal = TRUE AND activo = TRUE;

-- 18. Unicidad de relación activa estudiante-apoderado
CREATE UNIQUE INDEX uk_estudiante_apoderado_activo ON estudiante_apoderado(estudiante_id, apoderado_id) WHERE activo = TRUE;

-- 19. Filtro de horarios por año escolar
CREATE INDEX idx_horario_asistencia_anio_escolar ON horario_asistencia(anio_escolar_id);

-- 20. Filtro de horarios por grado
CREATE INDEX idx_horario_asistencia_grado ON horario_asistencia(grado_id);

-- 21. Filtro de horarios por sección
CREATE INDEX idx_horario_asistencia_seccion ON horario_asistencia(seccion_id);

-- 22. Filtro de horarios por estado activo
CREATE INDEX idx_horario_asistencia_activo ON horario_asistencia(activo);

-- 23. Búsqueda de vigencia de horarios por fecha inicio y fin
CREATE INDEX idx_horario_asistencia_fechas ON horario_asistencia(fecha_inicio, fecha_fin);

-- 24. Filtro de calendario escolar por año escolar
CREATE INDEX idx_calendario_escolar_anio_escolar ON calendario_escolar(anio_escolar_id);

-- 25. Filtro de calendario escolar por fecha
CREATE INDEX idx_calendario_escolar_fecha ON calendario_escolar(fecha);

-- 26. Unicidad de configuración activa por año escolar y fecha
CREATE UNIQUE INDEX uk_calendario_escolar_anio_fecha_activo ON calendario_escolar(anio_escolar_id, fecha) WHERE activo = TRUE;

-- 27. Filtro de alertas de asistencia por matrícula
CREATE INDEX idx_alerta_asistencia_matricula_id ON alerta_asistencia(matricula_id);

-- 28. Filtro de alertas de asistencia por estado
CREATE INDEX idx_alerta_asistencia_estado ON alerta_asistencia(estado);

-- 29. Filtro de alertas de asistencia por tipo
CREATE INDEX idx_alerta_asistencia_tipo ON alerta_asistencia(tipo);

-- 30. Filtro de alertas de asistencia por fecha de detección
CREATE INDEX idx_alerta_asistencia_fecha_deteccion ON alerta_asistencia(fecha_deteccion);

-- 31. Filtro de configuración de alertas por tipo
CREATE INDEX idx_config_alerta_tipo ON configuracion_alerta_asistencia(tipo);

-- 32. Unicidad de alerta activa por matrícula, tipo y periodo
CREATE UNIQUE INDEX uk_alerta_asistencia_activa_periodo ON alerta_asistencia(matricula_id, tipo, fecha_inicio_periodo) WHERE estado IN ('PENDIENTE', 'EN_REVISION') AND activo = TRUE;

-- 33. Filtro de notificaciones por usuario y estado de lectura (cubre también usuario_id solo)
CREATE INDEX idx_notificacion_usuario_leida ON notificacion(usuario_id, leida);

-- 35. Filtro de notificaciones por tipo
CREATE INDEX idx_notificacion_tipo ON notificacion(tipo);

-- 36. Ordenamiento de notificaciones por fecha de creación
CREATE INDEX idx_notificacion_created_at ON notificacion(created_at);

-- 37. Búsqueda de notificaciones por referencia
CREATE INDEX idx_notificacion_referencia ON notificacion(referencia_tipo, referencia_id);

-- 38. Auditoría: búsqueda y ordenamiento por fecha
CREATE INDEX idx_auditoria_evento_fecha ON auditoria_evento(fecha);

-- 39. Auditoría: filtro por usuario
CREATE INDEX idx_auditoria_evento_usuario_id ON auditoria_evento(usuario_id);

-- 40. Auditoría: filtro por username histórico
CREATE INDEX idx_auditoria_evento_username ON auditoria_evento(username);

-- 41. Auditoría: filtro por acción
CREATE INDEX idx_auditoria_evento_accion ON auditoria_evento(accion);

-- 42. Auditoría: filtro por módulo
CREATE INDEX idx_auditoria_evento_modulo ON auditoria_evento(modulo);

-- 43. Auditoría: filtro por resultado
CREATE INDEX idx_auditoria_evento_resultado ON auditoria_evento(resultado);

-- 44. Auditoría: filtro por entidad y ID
CREATE INDEX idx_auditoria_evento_entidad ON auditoria_evento(entidad_tipo, entidad_id);

