package com.jesusnazareno.asistencia.modules.acceso.service;

import com.jesusnazareno.asistencia.modules.acceso.dto.PermisoResponse;
import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;

import java.util.List;

public interface PermisoService {

    List<PermisoResponse> listarTodos();

    List<PermisoResponse> listarActivos();

    PermisoResponse buscarPorId(Long id);

    PermisoResponse buscarPorCodigo(String codigo);

    List<PermisoResponse> buscarPorModulo(String modulo);

    Permiso obtenerEntidadPorId(Long id);

    Permiso obtenerEntidadPorCodigo(String codigo);
}
