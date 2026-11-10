package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoResponse;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.GradoMapper;
import com.jesusnazareno.asistencia.modules.academico.mapper.SeccionMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradoServiceImpl implements GradoService {

    private final GradoRepository gradoRepository;
    private final GradoMapper gradoMapper;
    private final SeccionRepository seccionRepository;
    private final SeccionMapper seccionMapper;

    public GradoServiceImpl(GradoRepository gradoRepository,
                            GradoMapper gradoMapper,
                            SeccionRepository seccionRepository,
                            SeccionMapper seccionMapper) {
        this.gradoRepository = gradoRepository;
        this.gradoMapper = gradoMapper;
        this.seccionRepository = seccionRepository;
        this.seccionMapper = seccionMapper;
    }

    @Override
    @Transactional
    public GradoResponse crearGrado(GradoRequest request) {
        validarReglasNegocio(request, null);

        Grado entity = gradoMapper.toEntity(request);
        Grado guardado = gradoRepository.save(entity);
        return gradoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public GradoResponse buscarPorId(Long id) {
        Grado entity = gradoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + id));
        GradoResponse response = gradoMapper.toResponse(entity);
        response.setCantidadSecciones(seccionRepository.countByGradoId(id));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradoResponse> listarPorNivel(NivelEducativo nivel) {
        return gradoRepository.findByNivel(nivel).stream()
                .map(entity -> {
                    GradoResponse resp = gradoMapper.toResponse(entity);
                    resp.setCantidadSecciones(seccionRepository.countByGradoId(entity.getId()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradoResponse> listarTodos() {
        return gradoRepository.findAll().stream()
                .map(entity -> {
                    GradoResponse resp = gradoMapper.toResponse(entity);
                    resp.setCantidadSecciones(seccionRepository.countByGradoId(entity.getId()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradoResponse> listarActivos() {
        return gradoRepository.findByActivoTrueOrderByNivelAscNumeroAsc().stream()
                .map(entity -> {
                    GradoResponse resp = gradoMapper.toResponse(entity);
                    resp.setCantidadSecciones(seccionRepository.countByGradoId(entity.getId()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradoResponse> listarPaginado(NivelEducativo nivel, String estado, Pageable pageable) {
        Boolean activo = null;
        if ("ACTIVOS".equalsIgnoreCase(estado)) {
            activo = true;
        } else if ("INACTIVOS".equalsIgnoreCase(estado)) {
            activo = false;
        }
        return gradoRepository.buscarConFiltros(nivel, activo, pageable)
                .map(entity -> {
                    GradoResponse resp = gradoMapper.toResponse(entity);
                    resp.setCantidadSecciones(seccionRepository.countByGradoId(entity.getId()));
                    return resp;
                });
    }

    @Override
    @Transactional
    public GradoResponse actualizarGrado(Long id, GradoRequest request) {
        Grado entity = gradoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + id));

        validarReglasNegocio(request, id);

        gradoMapper.updateEntity(request, entity);
        Grado actualizado = gradoRepository.save(entity);
        GradoResponse response = gradoMapper.toResponse(actualizado);
        response.setCantidadSecciones(seccionRepository.countByGradoId(id));
        return response;
    }

    @Override
    @Transactional
    public GradoResponse activarGrado(Long id) {
        Grado entity = gradoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + id));
        entity.setActivo(true);
        Grado actualizado = gradoRepository.save(entity);
        GradoResponse response = gradoMapper.toResponse(actualizado);
        response.setCantidadSecciones(seccionRepository.countByGradoId(id));
        return response;
    }

    @Override
    @Transactional
    public void desactivarGrado(Long id) {
        Grado entity = gradoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + id));
        entity.setActivo(false);
        gradoRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionResponse> obtenerSeccionesPorGrado(Long gradoId) {
        return seccionRepository.findByGradoIdOrderByNombreAsc(gradoId).stream()
                .map(seccionMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validarReglasNegocio(GradoRequest request, Long idActual) {
        gradoRepository.findByNivelAndNumero(request.getNivel(), request.getNumero())
                .ifPresent(existente -> {
                    if (idActual == null || !existente.getId().equals(idActual)) {
                        throw new BusinessException("Ya existe un grado registrado para el nivel " + request.getNivel() + " y número " + request.getNumero());
                    }
                });
    }
}
