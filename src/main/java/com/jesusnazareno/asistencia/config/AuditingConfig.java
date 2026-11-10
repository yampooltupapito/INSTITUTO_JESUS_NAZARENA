package com.jesusnazareno.asistencia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuración para habilitar la auditoría automática de JPA (@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy).
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    /**
     * Proveedor del auditor actual para registrar el usuario que crea o modifica registros.
     * Si no existe un usuario autenticado (por ejemplo, en tareas iniciales o del sistema),
     * se asigna "system" por defecto.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() 
                    || "anonymousUser".equalsIgnoreCase(authentication.getPrincipal().toString())) {
                return Optional.of("system");
            }
            return Optional.ofNullable(authentication.getName()).or(() -> Optional.of("system"));
        };
    }
}
