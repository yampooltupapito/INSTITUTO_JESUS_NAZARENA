package com.jesusnazareno.asistencia.modules.acceso.repository;

import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Rol> findByActivoTrue();
}
