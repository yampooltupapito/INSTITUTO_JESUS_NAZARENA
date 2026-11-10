-- =============================================================================
-- SISTEMA WEB DE CONTROL DE ASISTENCIA - INSTITUCIÓN EDUCATIVA JESÚS NAZARENO
-- Script 03: Carga Inicial de Datos Maestros (Seed)
-- Motor: PostgreSQL 14+
-- =============================================================================

-- 1. TIPOS DE ACTOR DE LA INSTITUCIÓN
INSERT INTO tipo_actor (codigo, nombre, descripcion) VALUES
('ADMINISTRADOR', 'Administrador del Sistema', 'Personal responsable del soporte técnico y gestión global'),
('DIRECTOR', 'Director / Equipo Directivo', 'Personal directivo de la institución educativa'),
('AUXILIAR', 'Auxiliar de Educación', 'Personal encargado de la disciplina y control diario de asistencia'),
('DOCENTE', 'Docente / Profesor', 'Personal pedagógico a cargo de asignaturas y secciones'),
('ESTUDIANTE', 'Estudiante / Alumno', 'Estudiante matriculado en la institución'),
('PADRE_APODERADO', 'Padre de Familia / Apoderado', 'Tutor legal o apoderado del estudiante')
ON CONFLICT (codigo) DO NOTHING;

-- 2. ROLES DEL SISTEMA DE SEGURIDAD
INSERT INTO rol (codigo, nombre, descripcion) VALUES
('ADMIN', 'Administrador', 'Acceso total a la configuración y gestión del sistema'),
('DIRECTOR', 'Director', 'Acceso a reportes globales, métricas y supervisión general'),
('AUXILIAR', 'Auxiliar', 'Registro y control diario de asistencia de estudiantes'),
('DOCENTE', 'Docente', 'Visualización de asistencia de sus secciones y registro en aula'),
('ESTUDIANTE', 'Estudiante', 'Acceso al portal para consultar su récord personal de asistencia'),
('PADRE', 'Padre / Apoderado', 'Acceso al portal para consultar la asistencia de sus hijos')
ON CONFLICT (codigo) DO NOTHING;

-- 3. PERMISOS DEL SISTEMA
INSERT INTO permiso (codigo, nombre, descripcion, modulo) VALUES
('DASHBOARD_VER', 'Ver Dashboard', 'Permite visualizar el panel principal de indicadores', 'DASHBOARD'),
('USUARIO_VER', 'Ver Usuarios', 'Permite consultar el listado de usuarios del sistema', 'ACCESO'),
('USUARIO_CREAR', 'Crear Usuario', 'Permite registrar nuevos usuarios', 'ACCESO'),
('USUARIO_EDITAR', 'Editar Usuario', 'Permite modificar datos de usuarios existentes', 'ACCESO'),
('ESTUDIANTE_VER', 'Ver Estudiantes', 'Permite listar y consultar datos de estudiantes', 'ESTUDIANTE'),
('ESTUDIANTE_CREAR', 'Crear Estudiante', 'Permite registrar la ficha inicial de estudiantes', 'ESTUDIANTE'),
('ESTUDIANTE_EDITAR', 'Editar Estudiante', 'Permite actualizar datos de estudiantes', 'ESTUDIANTE'),
('ASISTENCIA_VER', 'Ver Asistencia', 'Permite consultar registros de asistencia', 'ASISTENCIA'),
('ASISTENCIA_REGISTRAR', 'Registrar Asistencia', 'Permite tomar la asistencia diaria', 'ASISTENCIA'),
('ASISTENCIA_EDITAR', 'Editar Asistencia', 'Permite corregir o justificar asistencias', 'ASISTENCIA'),
('ASISTENCIA_REPORTAR', 'Reportar Asistencia', 'Permite generar consolidados de asistencia', 'ASISTENCIA'),
('REPORTE_ASISTENCIA', 'Generar Reportes PDF/Excel', 'Permite exportar reportes institucionales', 'REPORTES'),
('MATRICULA_VER', 'Ver Matrículas', 'Permite consultar el padrón de matrículas', 'MATRICULA'),
('MATRICULA_CREAR', 'Crear Matrícula', 'Permite registrar la matrícula de un estudiante', 'MATRICULA'),
('MATRICULA_EDITAR', 'Editar Matrícula', 'Permite modificar y cambiar estado de matrículas', 'MATRICULA'),
('JUSTIFICACION_VER', 'Ver Justificaciones', 'Permite consultar y listar justificaciones de asistencia', 'JUSTIFICACION'),
('JUSTIFICACION_CREAR', 'Crear Justificación', 'Permite registrar solicitudes de justificación', 'JUSTIFICACION'),
('JUSTIFICACION_REVISAR', 'Revisar Justificación', 'Permite aprobar o rechazar solicitudes de justificación', 'JUSTIFICACION'),
('PERSONAL_VER', 'Ver Personal', 'Permite consultar y listar el personal de la institución', 'PERSONAL'),
('PERSONAL_CREAR', 'Crear Personal', 'Permite registrar nuevo personal y asignarle funciones', 'PERSONAL'),
('PERSONAL_EDITAR', 'Editar Personal', 'Permite actualizar datos y funciones del personal', 'PERSONAL'),
('APODERADO_VER', 'Ver Apoderados', 'Permite consultar y listar los apoderados y sus relaciones', 'APODERADO'),
('APODERADO_CREAR', 'Crear Apoderado', 'Permite registrar apoderados y asignarlos a estudiantes', 'APODERADO'),
('APODERADO_EDITAR', 'Editar Apoderado', 'Permite actualizar datos, parentesco y apoderado principal', 'APODERADO'),
('ACADEMICO_VER', 'Ver Gestión Académica', 'Permite consultar años escolares, grados y secciones', 'ACADEMICO'),
('ACADEMICO_CREAR', 'Crear Elementos Académicos', 'Permite registrar años escolares, grados y secciones', 'ACADEMICO'),
('ACADEMICO_EDITAR', 'Editar Elementos Académicos', 'Permite actualizar, activar y desactivar años escolares, grados y secciones', 'ACADEMICO'),
('HORARIO_VER', 'Ver Horarios de Asistencia', 'Permite consultar y listar los horarios de asistencia', 'ACADEMICO'),
('HORARIO_CREAR', 'Crear Horario de Asistencia', 'Permite registrar nuevos horarios de asistencia', 'ACADEMICO'),
('HORARIO_EDITAR', 'Editar Horario de Asistencia', 'Permite actualizar, activar y desactivar horarios de asistencia', 'ACADEMICO'),
('CALENDARIO_VER', 'Ver Calendario Escolar', 'Permite consultar y listar fechas y excepciones del calendario escolar', 'ACADEMICO'),
('CALENDARIO_CREAR', 'Crear Fecha de Calendario', 'Permite registrar nuevas fechas y excepciones en el calendario', 'ACADEMICO'),
('CALENDARIO_EDITAR', 'Editar Fecha de Calendario', 'Permite actualizar, activar y desactivar fechas del calendario', 'ACADEMICO'),
('ALERTA_VER', 'Ver Alertas de Asistencia', 'Permite consultar y listar alertas internas de asistencia', 'ASISTENCIA'),
('ALERTA_REVISAR', 'Revisar Alertas de Asistencia', 'Permite poner en revisión y resolver alertas de asistencia', 'ASISTENCIA'),
('ALERTA_CONFIGURAR', 'Configurar Alertas de Asistencia', 'Permite configurar umbrales de detección de alertas', 'ASISTENCIA'),
('IMPORTACION_ESTUDIANTE', 'Importar Estudiantes', 'Permite importar masivamente estudiantes desde archivos Excel', 'ESTUDIANTE'),
('IMPORTACION_MATRICULA', 'Importar Matrículas', 'Permite importar masivamente matrículas desde archivos Excel', 'MATRICULA'),
('AUDITORIA_VER', 'Ver Auditoría del Sistema', 'Permite consultar y visualizar el registro histórico de eventos y trazabilidad del sistema', 'SEGURIDAD')
ON CONFLICT (codigo) DO NOTHING;

-- 4. ASIGNACIÓN INICIAL DE PERMISOS A ROLES (ROL_PERMISO)
-- Asignar todos los permisos al ROL ADMIN
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p WHERE r.codigo = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Permisos para ROL DIRECTOR
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r JOIN permiso p ON p.codigo IN ('DASHBOARD_VER', 'USUARIO_VER', 'ESTUDIANTE_VER', 'MATRICULA_VER', 'MATRICULA_CREAR', 'MATRICULA_EDITAR', 'ASISTENCIA_VER', 'ASISTENCIA_REPORTAR', 'REPORTE_ASISTENCIA', 'JUSTIFICACION_VER', 'JUSTIFICACION_CREAR', 'JUSTIFICACION_REVISAR', 'PERSONAL_VER', 'PERSONAL_CREAR', 'PERSONAL_EDITAR', 'APODERADO_VER', 'APODERADO_CREAR', 'APODERADO_EDITAR', 'ACADEMICO_VER', 'ACADEMICO_CREAR', 'ACADEMICO_EDITAR', 'HORARIO_VER', 'HORARIO_CREAR', 'HORARIO_EDITAR', 'CALENDARIO_VER', 'CALENDARIO_CREAR', 'CALENDARIO_EDITAR', 'ALERTA_VER', 'ALERTA_REVISAR', 'ALERTA_CONFIGURAR', 'IMPORTACION_ESTUDIANTE', 'IMPORTACION_MATRICULA', 'AUDITORIA_VER') WHERE r.codigo = 'DIRECTOR'
ON CONFLICT DO NOTHING;

-- Permisos para ROL AUXILIAR
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r JOIN permiso p ON p.codigo IN ('DASHBOARD_VER', 'ESTUDIANTE_VER', 'MATRICULA_VER', 'MATRICULA_CREAR', 'ASISTENCIA_VER', 'ASISTENCIA_REGISTRAR', 'ASISTENCIA_EDITAR', 'ASISTENCIA_REPORTAR', 'REPORTE_ASISTENCIA', 'JUSTIFICACION_VER', 'JUSTIFICACION_CREAR', 'JUSTIFICACION_REVISAR', 'APODERADO_VER', 'APODERADO_CREAR', 'APODERADO_EDITAR', 'ACADEMICO_VER', 'HORARIO_VER', 'ALERTA_VER', 'ALERTA_REVISAR', 'IMPORTACION_ESTUDIANTE', 'IMPORTACION_MATRICULA') WHERE r.codigo = 'AUXILIAR'
ON CONFLICT DO NOTHING;

-- Permisos para ROL DOCENTE
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r JOIN permiso p ON p.codigo IN ('DASHBOARD_VER', 'ESTUDIANTE_VER', 'ASISTENCIA_VER', 'ASISTENCIA_REGISTRAR', 'JUSTIFICACION_VER', 'JUSTIFICACION_CREAR') WHERE r.codigo = 'DOCENTE'
ON CONFLICT DO NOTHING;

-- Permisos para ROL ESTUDIANTE
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r JOIN permiso p ON p.codigo IN ('DASHBOARD_VER', 'ASISTENCIA_VER') WHERE r.codigo = 'ESTUDIANTE'
ON CONFLICT DO NOTHING;

-- Permisos para ROL PADRE
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r JOIN permiso p ON p.codigo IN ('DASHBOARD_VER', 'ASISTENCIA_VER') WHERE r.codigo = 'PADRE'
ON CONFLICT DO NOTHING;

-- 5. CONFIGURACIÓN INICIAL DE UMBRALES DE ALERTAS DE ASISTENCIA
INSERT INTO configuracion_alerta_asistencia (tipo, umbral, activo)
VALUES 
    ('AUSENCIAS_CONSECUTIVAS', 3, TRUE),
    ('AUSENCIAS_ACUMULADAS', 5, TRUE),
    ('TARDANZAS_ACUMULADAS', 5, TRUE)
ON CONFLICT (tipo) DO NOTHING;

-- 6. USUARIO ADMINISTRADOR INICIAL POR DEFECTO
-- Credenciales: admin / Admin123!
INSERT INTO persona (tipo_documento, numero_documento, nombres, apellido_paterno, apellido_materno, fecha_nacimiento, sexo, correo, telefono, direccion, activo, created_by)
VALUES ('DNI', '00000001', 'Administrador', 'General', 'Sistema', '1990-01-01', 'MASCULINO', 'admin@jesusnazareno.edu.pe', '999888777', 'Av. Principal 123', TRUE, 'system')
ON CONFLICT (numero_documento) DO NOTHING;

INSERT INTO persona_actor (persona_id, tipo_actor_id, fecha_inicio, activo, created_by)
SELECT p.id, ta.id, CURRENT_DATE, TRUE, 'system'
FROM persona p CROSS JOIN tipo_actor ta
WHERE p.numero_documento = '00000001' AND ta.codigo = 'ADMINISTRADOR'
ON CONFLICT (persona_id, tipo_actor_id) DO NOTHING;

INSERT INTO usuario (persona_id, username, password, rol_id, activo, created_by)
SELECT p.id, 'admin', '$2a$10$pVgox6eEOLJjQLZdu9JNFu6THyVw44wcc2fnBOo9pj91lQn9iJEeC', r.id, TRUE, 'system'
FROM persona p CROSS JOIN rol r
WHERE p.numero_documento = '00000001' AND r.codigo = 'ADMIN'
ON CONFLICT (username) DO NOTHING;

