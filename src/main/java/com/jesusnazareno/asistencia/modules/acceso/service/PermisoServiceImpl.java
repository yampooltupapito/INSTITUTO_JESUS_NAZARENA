package com.jesusnazareno.asistencia.modules.acceso.service;

import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.dto.PermisoResponse;
import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import com.jesusnazareno.asistencia.modules.acceso.mapper.PermisoMapper;
import com.jesusnazareno.asistencia.modules.acceso.repository.PermisoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;
    private final PermisoMapper permisoMapper;

    public PermisoServiceImpl(PermisoRepository permisoRepository, PermisoMapper permisoMapper) {
        this.permisoRepository = permisoRepository;
        this.permisoMapper = permisoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> listarTodos() {
        return permisoRepository.findAll().stream()
                .map(permisoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> listarActivos() {
        return permisoRepository.findByActivoTrue().stream()
                .map(permisoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoResponse buscarPorId(Long id) {
        return permisoMapper.toResponse(obtenerEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoResponse buscarPorCodigo(String codigo) {
        return permisoMapper.toResponse(obtenerEntidadPorCodigo(codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> buscarPorModulo(String modulo) {
        return permisoRepository.findByModulo(modulo).stream()
                .map(permisoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Permiso obtenerEntidadPorId(Long id) {
        return permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Permiso obtenerEntidadPorCodigo(String codigo) {
        return permisoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con código: " + codigo));
    }
}
