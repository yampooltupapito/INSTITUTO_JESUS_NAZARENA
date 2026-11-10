package com.jesusnazareno.asistencia.modules.acceso.repository;

import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import com.jesusnazareno.asistencia.modules.acceso.domain.RolPermiso;
import com.jesusnazareno.asistencia.modules.acceso.domain.RolPermisoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    List<RolPermiso> findByIdRolId(Long rolId);

    @Query("SELECT rp.permiso FROM RolPermiso rp WHERE rp.rol.id = :rolId")
    List<Permiso> findPermisosByRolId(@Param("rolId") Long rolId);

    @Query("SELECT rp.permiso FROM RolPermiso rp WHERE rp.rol.id = :rolId AND rp.permiso.activo = true")
    List<Permiso> findPermisosActivosByRolId(@Param("rolId") Long rolId);
}
