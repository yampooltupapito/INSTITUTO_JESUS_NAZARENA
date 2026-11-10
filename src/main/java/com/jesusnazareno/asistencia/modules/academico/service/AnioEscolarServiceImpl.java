package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.AnioEscolarMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnioEscolarServiceImpl implements AnioEscolarService {

    private final AnioEscolarRepository anioEscolarRepository;
    private final AnioEscolarMapper anioEscolarMapper;
    private final MatriculaRepository matriculaRepository;

    public AnioEscolarServiceImpl(AnioEscolarRepository anioEscolarRepository,
                                   AnioEscolarMapper anioEscolarMapper,
                                   MatriculaRepository matriculaRepository) {
        this.anioEscolarRepository = anioEscolarRepository;
        this.anioEscolarMapper = anioEscolarMapper;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional
    public AnioEscolarResponse crearAnioEscolar(AnioEscolarRequest request) {
        validarReglasNegocio(request, null);

        AnioEscolar entity = anioEscolarMapper.toEntity(request);
        AnioEscolar guardado = anioEscolarRepository.save(entity);
        return anioEscolarMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public AnioEscolarResponse buscarPorId(Long id) {
        AnioEscolar entity = anioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + id));
        AnioEscolarResponse response = anioEscolarMapper.toResponse(entity);
        response.setCantidadMatriculas(matriculaRepository.countByAnioEscolarId(id));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AnioEscolarResponse buscarPorAnio(Integer anio) {
        AnioEscolar entity = anioEscolarRepository.findByAnio(anio)
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado para el año: " + anio));
        AnioEscolarResponse response = anioEscolarMapper.toResponse(entity);
        response.setCantidadMatriculas(matriculaRepository.countByAnioEscolarId(entity.getId()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnioEscolarResponse> listarTodos() {
        return anioEscolarRepository.findAll().stream()
                .map(entity -> {
                    AnioEscolarResponse resp = anioEscolarMapper.toResponse(entity);
                    resp.setCantidadMatriculas(matriculaRepository.countByAnioEscolarId(entity.getId()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnioEscolarResponse> listarPaginado(String estado, Pageable pageable) {
        Boolean activo = null;
        if ("ACTIVOS".equalsIgnoreCase(estado)) {
            activo = true;
        } else if ("INACTIVOS".equalsIgnoreCase(estado)) {
            activo = false;
        }
        return anioEscolarRepository.buscarConFiltro(activo, pageable)
                .map(entity -> {
                    AnioEscolarResponse resp = anioEscolarMapper.toResponse(entity);
                    resp.setCantidadMatriculas(matriculaRepository.countByAnioEscolarId(entity.getId()));
                    return resp;
                });
    }

    @Override
    @Transactional
    public AnioEscolarResponse actualizarAnioEscolar(Long id, AnioEscolarRequest request) {
        AnioEscolar entity = anioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + id));

        validarReglasNegocio(request, id);

        long totalMatriculas = matriculaRepository.countByAnioEscolarId(id);
        if (totalMatriculas > 0 && !entity.getAnio().equals(request.getAnio())) {
            throw new BusinessException("No se puede modificar el número del año escolar porque ya cuenta con matrículas registradas.");
        }

        anioEscolarMapper.updateEntity(request, entity);
        AnioEscolar actualizado = anioEscolarRepository.save(entity);
        AnioEscolarResponse response = anioEscolarMapper.toResponse(actualizado);
        response.setCantidadMatriculas(totalMatriculas);
        return response;
    }

    @Override
    @Transactional
    public AnioEscolarResponse activarAnioEscolar(Long id) {
        AnioEscolar entity = anioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + id));
        entity.setActivo(true);
        AnioEscolar actualizado = anioEscolarRepository.save(entity);
        AnioEscolarResponse response = anioEscolarMapper.toResponse(actualizado);
        response.setCantidadMatriculas(matriculaRepository.countByAnioEscolarId(id));
        return response;
    }

    @Override
    @Transactional
    public void desactivarAnioEscolar(Long id) {
        AnioEscolar entity = anioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + id));
        entity.setActivo(false);
        anioEscolarRepository.save(entity);
    }

    private void validarReglasNegocio(AnioEscolarRequest request, Long idActual) {
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        anioEscolarRepository.findByAnio(request.getAnio())
                .ifPresent(existente -> {
                    if (idActual == null || !existente.getId().equals(idActual)) {
                        throw new BusinessException("Ya existe un año escolar registrado para el año " + request.getAnio());
                    }
                });
    }
}
