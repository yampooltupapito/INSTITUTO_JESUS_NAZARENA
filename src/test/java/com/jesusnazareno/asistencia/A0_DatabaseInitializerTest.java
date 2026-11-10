package com.jesusnazareno.asistencia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

class A0_DatabaseInitializerTest {

    @Test
    @DisplayName("00. Inicializar esquema de base de datos PostgreSQL desde scripts SQL")
    void initializeDatabase() {
        try {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5433/kk");
            dataSource.setUsername("postgres");
            dataSource.setPassword("jkbnmt09");

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new FileSystemResource("sql/01_schema.sql"));
            populator.addScript(new FileSystemResource("sql/02_indexes.sql"));
            populator.addScript(new FileSystemResource("sql/03_seed_base.sql"));
            populator.execute(dataSource);
            System.out.println("Base de datos PostgreSQL inicializada exitosamente con 01_schema.sql, 02_indexes.sql y 03_seed_base.sql.");
        } catch (Exception e) {
            System.err.println("No se pudo conectar a PostgreSQL o inicializar tablas: " + e.getMessage());
        }
    }
}
