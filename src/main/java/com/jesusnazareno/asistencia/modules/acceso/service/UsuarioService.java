package com.jesusnazareno.asistencia.modules.acceso.service;

import com.jesusnazareno.asistencia.modules.acceso.dto.CambioPasswordRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioCreateRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioUpdateRequest;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse crear(UsuarioCreateRequest request);

    UsuarioResponse buscarPorId(Long id);

    UsuarioResponse buscarPorIdConPermisos(Long id);

    UsuarioResponse buscarPorUsername(String username);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    UsuarioResponse activar(Long id);

    UsuarioResponse desactivar(Long id);

    void cambiarPassword(Long id, CambioPasswordRequest request);

    void resetearPassword(Long id, String nuevaPassword, String confirmarPassword);

    void registrarUltimoAcceso(String username);

    List<UsuarioResponse> listarTodos();

    List<UsuarioResponse> listarActivos();

    Page<UsuarioResponse> listarPaginado(String q, Long rolId, Boolean activo, Pageable pageable);

    Usuario obtenerEntidadPorId(Long id);

    Usuario obtenerEntidadPorUsername(String username);
}
