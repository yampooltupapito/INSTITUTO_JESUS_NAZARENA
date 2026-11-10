package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.SeccionMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeccionServiceImpl implements SeccionService {

    private final SeccionRepository seccionRepository;
    private final GradoRepository gradoRepository;
    private final SeccionMapper seccionMapper;
    private final MatriculaRepository matriculaRepository;

    public SeccionServiceImpl(SeccionRepository seccionRepository,
                              GradoRepository gradoRepository,
                              SeccionMapper seccionMapper,
                              MatriculaRepository matriculaRepository) {
        this.seccionRepository = seccionRepository;
        this.gradoRepository = gradoRepository;
        this.seccionMapper = seccionMapper;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional
    public SeccionResponse crearSeccion(SeccionRequest request) {
        Grado grado = obtenerGradoValido(request.getGradoId());
        validarReglasNegocio(request, null);

        Seccion entity = seccionMapper.toEntity(request, grado);
        Seccion guardada = seccionRepository.save(entity);
        return enriquecerConOcupacion(seccionMapper.toResponse(guardada), guardada.getId(), guardada.getCapacidad());
    }

    @Override
    @Transactional(readOnly = true)
    public SeccionResponse buscarPorId(Long id) {
        Seccion entity = seccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + id));
        return enriquecerConOcupacion(seccionMapper.toResponse(entity), entity.getId(), entity.getCapacidad());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionResponse> listarPorGrado(Long gradoId) {
        return seccionRepository.findByGradoIdOrderByNombreAsc(gradoId).stream()
                .map(entity -> enriquecerConOcupacion(seccionMapper.toResponse(entity), entity.getId(), entity.getCapacidad()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionResponse> listarTodas() {
        return seccionRepository.findAll().stream()
                .map(entity -> enriquecerConOcupacion(seccionMapper.toResponse(entity), entity.getId(), entity.getCapacidad()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SeccionResponse> buscarPaginado(NivelEducativo nivel, Long gradoId, Turno turno, String estado, Pageable pageable) {
        Boolean activo = null;
        if ("ACTIVOS".equalsIgnoreCase(estado)) {
            activo = true;
        } else if ("INACTIVOS".equalsIgnoreCase(estado)) {
            activo = false;
        }
        return seccionRepository.buscarConFiltros(nivel, gradoId, turno, activo, pageable)
                .map(entity -> enriquecerConOcupacion(seccionMapper.toResponse(entity), entity.getId(), entity.getCapacidad()));
    }

    @Override
    @Transactional
    public SeccionResponse actualizarSeccion(Long id, SeccionRequest request) {
        Seccion entity = seccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + id));

        Grado grado = obtenerGradoValido(request.getGradoId());
        validarReglasNegocio(request, id);

        long matriculados = matriculaRepository.countMatriculadosActivosEnSeccion(id);
        if (request.getCapacidad() < matriculados) {
            throw new BusinessException("La capacidad no puede ser menor que la cantidad actual de matriculados (" + matriculados + ").");
        }

        seccionMapper.updateEntity(request, entity, grado);
        Seccion actualizada = seccionRepository.save(entity);
        return enriquecerConOcupacion(seccionMapper.toResponse(actualizada), actualizada.getId(), actualizada.getCapacidad());
    }

    @Override
    @Transactional
    public SeccionResponse activarSeccion(Long id) {
        Seccion entity = seccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + id));
        entity.setActivo(true);
        Seccion actualizada = seccionRepository.save(entity);
        return enriquecerConOcupacion(seccionMapper.toResponse(actualizada), actualizada.getId(), actualizada.getCapacidad());
    }

    @Override
    @Transactional
    public void desactivarSeccion(Long id) {
        Seccion entity = seccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + id));
        entity.setActivo(false);
        seccionRepository.save(entity);
    }

    private SeccionResponse enriquecerConOcupacion(SeccionResponse resp, Long seccionId, Integer capacidad) {
        int matriculados = (int) matriculaRepository.countMatriculadosActivosEnSeccion(seccionId);
        int cap = capacidad != null ? capacidad : 0;
        int vacantes = Math.max(0, cap - matriculados);
        resp.setMatriculados(matriculados);
        resp.setVacantes(vacantes);
        return resp;
    }

    private Grado obtenerGradoValido(Long gradoId) {
        return gradoRepository.findById(gradoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + gradoId));
    }

    private void validarReglasNegocio(SeccionRequest request, Long idActual) {
        if (request.getCapacidad() == null || request.getCapacidad() <= 0) {
            throw new ValidationException("La capacidad debe ser mayor a 0.");
        }

        seccionRepository.findByGradoIdAndNombre(request.getGradoId(), request.getNombre())
                .ifPresent(existente -> {
                    if (idActual == null || !existente.getId().equals(idActual)) {
                        throw new BusinessException("Ya existe una sección '" + request.getNombre() + "' registrada para el grado especificado.");
                    }
                });
    }
}
