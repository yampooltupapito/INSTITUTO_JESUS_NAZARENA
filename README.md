# Sistema Web de Control de Asistencia — Institución Educativa Jesús Nazareno

## 1. Descripción y Objetivo
El **Sistema Web de Control de Asistencia de la Institución Educativa Jesús Nazareno** es una solución monolítica empresarial desarrollada con **Java 17, Spring Boot 3, Spring Security 6, Spring Data JPA, Hibernate, PostgreSQL, Thymeleaf y Tailwind CSS**.

Proporciona una plataforma integral para el control de asistencia estudiantil, trazabilidad institucional, alertas tempranas de deserción o ausentismo, gestión académica, reportes analíticos multi-formato, importaciones masivas y auditoría funcional estricta.

---

## 2. Tecnologías y Librerías Utilizadas
- **Lenguaje:** Java 17 (LTS)
- **Framework Base:** Spring Boot 3.2.5
- **Seguridad:** Spring Security 6 (Form Login, CSRF activo, Session Fixation protection, RBAC con authorities específicos)
- **Criptografía:** BCrypt Password Encoder
- **Motor de Base de Datos:** PostgreSQL 14+
- **Persistencia:** Spring Data JPA / Hibernate 6 (`spring.jpa.hibernate.ddl-auto=validate`, `open-in-view=false`)
- **Vistas Web:** Thymeleaf 3 + Thymeleaf Extras Spring Security 6
- **Diseño & Estilos:** Tailwind CSS (diseño responsive, badges, modales, alertas y estados vacíos)
- **Validación:** Jakarta Bean Validation (validadores personalizados como `@DniPeruano` y `@TelefonoPeruano`)
- **Generación de Reportes PDF:** OpenPDF 1.3.40 (maquetación institucional en tablas, pie de página con paginación y sellos de seguridad)
- **Generación & Procesamiento Excel:** Apache POI 5.2.5 (XLSX, estilos nativos, fórmulas y streaming seguro con límites de memoria)
- **Exportación CSV:** RFC 4180 con UTF-8 BOM y mitigación contra CSV Formula Injection
- **Monitoreo & Diagnóstico:** Spring Boot Actuator (restringido a `/actuator/health` y `/actuator/info`)
- **Auditoría:** Auditoría JPA (`BaseEntity` + `AuditorAware`) complementada con Auditoría Funcional Inmutable (`auditoria_evento`)
- **Pruebas:** JUnit 5, Mockito, Spring Security Test, MockMvc (503 pruebas automatizadas)

---

## 3. Arquitectura y Módulos del Sistema

```text
src/main/java/com/jesusnazareno/asistencia/
├── SgeApplication.java
├── config/
│   ├── JpaConfig.java                        # AuditorAware y configuración JPA
│   └── security/
│       ├── SecurityConfig.java               # Spring Security 6, CSRF y filtros
│       ├── CustomUserDetailsService.java     # Carga de usuario y permisos
│       ├── CustomAuthenticationSuccessHandler.java
│       ├── CustomAuthenticationFailureHandler.java
│       └── CustomLogoutSuccessHandler.java
├── common/
│   ├── persistence/BaseEntity.java           # Auditoría JPA: createdAt, updatedAt, createdBy, updatedBy
│   ├── dto/ApiError.java                     # Estructura estandarizada de errores REST
│   ├── validation/                           # Validadores DNI y Teléfono Perú
│   └── PingController.java                   # Endpoint inocuo /ping
├── exception/
│   ├── GlobalExceptionHandler.java           # Manejo global de excepciones seguras (400, 403, 404, 405, 500)
│   ├── BusinessException.java
│   └── ResourceNotFoundException.java
└── modules/
    ├── acceso/                               # Usuarios, Roles, Permisos y cambio de contraseña
    ├── persona/                              # Personas, Tipos de Actor y asignación de roles
    ├── academico/                            # Años Escolares, Grados, Secciones, Horarios y Calendario Escolar
    ├── estudiante/                           # Padrón de estudiantes, estados y fichas
    ├── matricula/                            # Matrículas con validación de capacidad por sección y correlativos
    ├── asistencia/                           # Registro diario/masivo, justificaciones con aprobación/rechazo
    ├── personal/                             # Personal docente y administrativo
    ├── apoderado/                            # Relaciones estudiante-apoderado y designación de apoderado principal
    ├── alerta/                               # Detección automática y gestión de alertas por inasistencias/tardanzas
    ├── notificacion/                         # Notificaciones internas privadas para usuarios (aislamiento anti-IDOR)
    ├── reporte/                              # Reportes analíticos con exportación PDF, Excel (XLSX) y CSV
    ├── importacion/                          # Carga masiva de estudiantes y matrículas desde plantillas XLSX seguras
    ├── auditoria/                            # Registro inmutable de eventos funcionales y trazabilidad administrativa
    └── dashboard/                            # Indicadores en tiempo real optimizados con COUNT en PostgreSQL
```

---

## 4. Base de Datos y Scripts SQL

El esquema se administra de forma externa con `ddl-auto=validate` a través de los scripts ubicados en `/sql`:

1. **`sql/01_schema.sql`**: Definición DDL de las 20 tablas, claves foráneas, restricciones `CHECK` y restricciones `UNIQUE`.
2. **`sql/02_indexes.sql`**: Índices secundarios y compuestos para optimización de filtros y ordenamiento sin redundancias.
3. **`sql/03_seed_base.sql`**: Carga inicial idempotente de tipos de actor, roles (`ADMIN`, `DIRECTOR`, `AUXILIAR`, `DOCENTE`), permisos del sistema y usuario administrador inicial.

### Parámetros de Conexión en `application.properties`:
```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5433/colegio_jesus_nazareno}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:jkbnmt09}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.jdbc.time_zone=America/Lima
```

---

## 5. Medidas de Seguridad y Calidad Implementadas
- **Protección CSRF:** Habilitada globalmente en Spring Security 6 para todas las peticiones POST.
- **Autorización Robusta:** Controlada con `@PreAuthorize("hasAuthority('...')")` en todos los métodos de los controladores.
- **Protección contra IDOR:** Verificación estricta en backend del propietario del recurso (notificaciones, justificaciones, etc.).
- **Tratamiento de Contraseñas:** Encriptadas mediante BCrypt; nunca se registran, exportan ni muestran en texto plano ni hashes.
- **Protección contra XSS:** 100% de uso de `th:text` en Thymeleaf, neutralizando inyecciones HTML en plantillas.
- **Protección contra Formula Injection:** En exportaciones CSV, los caracteres `=, +, -, @, \t, \r` son neutralizados con `'`.
- **Carga de Archivos Segura:** Límite de 5 MB, máximo 1000 filas por archivo, formato exclusivo XLSX, validación previa en memoria y sin escritura en disco.
- **Páginas de Error Personalizadas:** Vistas amigables sin exposición de trazas técnicas para errores `403`, `404` y `500`.

---

## 6. Ejecución del Proyecto y Pruebas

### Ejecutar la Suite de Pruebas:
```bash
./mvnw clean test
```
*Resultado: 503 pruebas ejecutadas, 0 fallos, 0 errores.*

### Construir el Paquete Ejecutable (JAR):
```bash
./mvnw clean package
```

### Iniciar la Aplicación:
```bash
java -jar target/institucion-educativa-jesus-nazareno-0.0.1-SNAPSHOT.jar
```
O con Maven Wrapper:
```bash
./mvnw spring-boot:run
```

---

## 7. Cuentas de Acceso por Defecto
- **Usuario Administrador:** `admin`
- **Contraseña:** `Admin123!` (o la configurada en seed inicial)
- **Rol:** `ADMIN` (Acceso completo a todos los módulos y auditoría)
