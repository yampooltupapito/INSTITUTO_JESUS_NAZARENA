package com.jesusnazareno.asistencia.modules.acceso.service;

import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.dto.PermisoResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.RolResponse;
import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.mapper.PermisoMapper;
import com.jesusnazareno.asistencia.modules.acceso.mapper.RolMapper;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolPermisoRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final RolMapper rolMapper;
    private final PermisoMapper permisoMapper;

    public RolServiceImpl(RolRepository rolRepository,
                          RolPermisoRepository rolPermisoRepository,
                          RolMapper rolMapper,
                          PermisoMapper permisoMapper) {
        this.rolRepository = rolRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolMapper = rolMapper;
        this.permisoMapper = permisoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponse> listarTodos() {
        return rolRepository.findAll().stream()
                .map(rol -> rolMapper.toResponse(rol, obtenerPermisosDeRol(rol.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponse> listarActivos() {
        return rolRepository.findByActivoTrue().stream()
                .map(rol -> rolMapper.toResponse(rol, obtenerPermisosDeRol(rol.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponse buscarPorId(Long id) {
        Rol rol = obtenerEntidadPorId(id);
        return rolMapper.toResponse(rol, obtenerPermisosDeRol(rol.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponse buscarPorCodigo(String codigo) {
        Rol rol = obtenerEntidadPorCodigo(codigo);
        return rolMapper.toResponse(rol, obtenerPermisosDeRol(rol.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Rol obtenerEntidadPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Rol obtenerEntidadPorCodigo(String codigo) {
        return rolRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con código: " + codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> obtenerPermisosDeRol(Long rolId) {
        List<Permiso> permisos = rolPermisoRepository.findPermisosActivosByRolId(rolId);
        return permisos.stream()
                .map(permisoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
