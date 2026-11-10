package com.jesusnazareno.asistencia.modules.acceso.service;

import com.jesusnazareno.asistencia.modules.acceso.dto.PermisoResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.RolResponse;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;

import java.util.List;

public interface RolService {

    List<RolResponse> listarTodos();

    List<RolResponse> listarActivos();

    RolResponse buscarPorId(Long id);

    RolResponse buscarPorCodigo(String codigo);

    Rol obtenerEntidadPorId(Long id);

    Rol obtenerEntidadPorCodigo(String codigo);

    List<PermisoResponse> obtenerPermisosDeRol(Long rolId);
}
