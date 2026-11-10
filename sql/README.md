# Guía de Ejecución de Scripts SQL Base - PostgreSQL

Esta carpeta contiene los scripts SQL necesarios para preparar la base de datos relacional PostgreSQL del **Sistema Web de Control de Asistencia - Institución Educativa Jesús Nazareno**.

---

## 1. Instrucciones de Ejecución

### Paso 1: Abrir pgAdmin o psql
Conéctate al servidor de PostgreSQL (por ejemplo, mediante `pgAdmin 4` o la consola `psql`).

### Paso 2: Crear la Base de Datos
Ejecuta la siguiente instrucción para crear la base de datos oficial del proyecto:

```sql
CREATE DATABASE colegio_jesus_nazareno;
```

### Paso 3: Conectarse a la Base de Datos
Conéctate a la base de datos recién creada:

```sql
\c colegio_jesus_nazareno
```

### Paso 4: Ejecutar los Scripts en Orden
Ejecuta los siguientes tres scripts en el orden estricto indicado:

1. **`01_schema.sql`**: Crea la estructura de las 12 tablas con sus llaves primarias, llaves foráneas y restricciones (`CHECK`, `UNIQUE`, `NOT NULL`).
2. **`02_indexes.sql`**: Crea los 10 índices secundarios requeridos para optimizar las consultas frecuentes de la aplicación.
3. **`03_seed_base.sql`**: Inserta los datos maestros iniciales (tipos de actor, roles de seguridad, permisos y relaciones rol-permiso).

---

## 2. Configuración JPA y Hibernate

En el archivo [`application.properties`](../src/main/resources/application.properties) se ha establecido la propiedad:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

### Explicación Arquitectural:
Configurar `ddl-auto=validate` garantiza que la estructura de la base de datos sea administrada y mantenida exclusivamente por los scripts SQL versión a versión. Hibernate únicamente verifica la compatibilidad de los tipos de datos y nombres de campos de las entidades JPA con las tablas físicas al arrancar el servidor, **sin crear, alterar ni eliminar tablas automáticamente en la base de datos**.

---

## 3. Consultas de Verificación de Datos

Una vez ejecutados los scripts, puedes verificar los datos iniciales ejecutando las siguientes consultas SQL:

```sql
SELECT * FROM persona;
SELECT * FROM tipo_actor;
SELECT * FROM rol;
SELECT * FROM permiso;
SELECT * FROM usuario;
SELECT * FROM anio_escolar;
SELECT * FROM grado;
SELECT * FROM seccion;
SELECT * FROM matricula;
SELECT * FROM asistencia;
```

---

## 4. Consulta para Listar Todas las Tablas Creadas en PostgreSQL

Para obtener el listado completo de tablas creadas en el esquema público de PostgreSQL y verificar que las 12 tablas estén presentes, ejecuta:

```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_type = 'BASE TABLE'
ORDER BY table_name;
```
