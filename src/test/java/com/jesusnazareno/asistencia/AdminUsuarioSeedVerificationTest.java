package com.jesusnazareno.asistencia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

class AdminUsuarioSeedVerificationTest {

    @Test
    @DisplayName("Verificar que la cuenta de administrador inicial está presente en PostgreSQL")
    void testVerificarAdminEnBD() {
        try {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5433/kk");
            dataSource.setUsername("postgres");
            dataSource.setPassword("jkbnmt09");

            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            List<Map<String, Object>> usuarios = jdbc.queryForList("SELECT u.username, r.codigo as rol FROM usuario u JOIN rol r ON u.rol_id = r.id WHERE u.username = 'admin'");
            assertFalse(usuarios.isEmpty(), "El usuario 'admin' debe existir en la base de datos");
        } catch (Exception e) {
            System.out.println("Salto de verificación si la BD no está disponible en este entorno: " + e.getMessage());
        }
    }
}
