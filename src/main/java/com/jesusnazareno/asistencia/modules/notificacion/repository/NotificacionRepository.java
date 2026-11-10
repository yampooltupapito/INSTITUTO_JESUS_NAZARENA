package com.jesusnazareno.asistencia.modules.notificacion.repository;

import com.jesusnazareno.asistencia.modules.notificacion.domain.Notificacion;
import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Notificacion.
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Optional<Notificacion> findByIdAndUsuarioIdAndActivoTrue(Long id, Long usuarioId);

    long countByUsuarioIdAndLeidaFalseAndActivoTrue(Long usuarioId);

    boolean existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndActivoTrue(
            TipoNotificacion tipo, String referenciaTipo, Long referenciaId, Long usuarioId);

    List<Notificacion> findTop5ByUsuarioIdAndActivoTrueOrderByCreatedAtDesc(Long usuarioId);

    List<Notificacion> findTop5ByUsuarioIdAndLeidaFalseAndActivoTrueOrderByCreatedAtDesc(Long usuarioId);

    @Query(value = """
        SELECT n FROM Notificacion n
        WHERE n.usuario.id = :usuarioId
          AND n.activo = true
          AND (:leida IS NULL OR n.leida = :leida)
          AND (:tipo IS NULL OR n.tipo = :tipo)
        ORDER BY n.createdAt DESC
    """)
    Page<Notificacion> buscarNotificacionesUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("leida") Boolean leida,
            @Param("tipo") TipoNotificacion tipo,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = :ahora, n.updatedAt = :ahora " +
           "WHERE n.usuario.id = :usuarioId AND n.leida = false AND n.activo = true")
    int marcarTodasComoLeidasUsuario(@Param("usuarioId") Long usuarioId, @Param("ahora") LocalDateTime ahora);
}
