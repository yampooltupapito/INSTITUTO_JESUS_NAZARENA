package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.CalendarioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.CalendarioEscolarMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.CalendarioEscolarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para la gestión del calendario escolar.
 */
@Service
public class CalendarioEscolarServiceImpl implements CalendarioEscolarService {

    private final CalendarioEscolarRepository calendarioEscolarRepository;
    private final AnioEscolarRepository anioEscolarRepository;
    private final CalendarioEscolarMapper calendarioEscolarMapper;

    public CalendarioEscolarServiceImpl(CalendarioEscolarRepository calendarioEscolarRepository,
                                       AnioEscolarRepository anioEscolarRepository,
                                       CalendarioEscolarMapper calendarioEscolarMapper) {
        this.calendarioEscolarRepository = calendarioEscolarRepository;
        this.anioEscolarRepository = anioEscolarRepository;
        this.calendarioEscolarMapper = calendarioEscolarMapper;
    }

    @Override
    @Transactional
    public CalendarioEscolarResponse crear(CalendarioEscolarRequest request) {
        if (request == null) {
            throw new BusinessException("La solicitud de calendario escolar no puede ser nula.");
        }

        AnioEscolar anioEscolar = anioEscolarRepository.findById(request.getAnioEscolarId())
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + request.getAnioEscolarId()));

        validarFechaDentroDePeriodo(request.getFecha(), anioEscolar);

        if (calendarioEscolarRepository.existsByAnioEscolarIdAndFechaAndActivoTrue(anioEscolar.getId(), request.getFecha())) {
            throw new BusinessException("Ya existe una configuración activa para esta fecha.");
        }

        CalendarioEscolar entity = calendarioEscolarMapper.toEntity(request, anioEscolar);
        CalendarioEscolar guardado = calendarioEscolarRepository.save(entity);
        return calendarioEscolarMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public CalendarioEscolarResponse actualizar(Long id, CalendarioEscolarRequest request) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de calendario escolar no puede ser nulo.");
        }
        if (request == null) {
            throw new BusinessException("La solicitud de calendario escolar no puede ser nula.");
        }

        CalendarioEscolar entity = calendarioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fecha del calendario escolar no encontrada."));

        AnioEscolar anioEscolar = anioEscolarRepository.findById(request.getAnioEscolarId())
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + request.getAnioEscolarId()));

        validarFechaDentroDePeriodo(request.getFecha(), anioEscolar);

        if (Boolean.TRUE.equals(entity.getActivo()) &&
                calendarioEscolarRepository.existsByAnioEscolarIdAndFechaAndActivoTrueAndIdNot(
                        anioEscolar.getId(), request.getFecha(), id)) {
            throw new BusinessException("Ya existe una configuración activa para esta fecha.");
        }

        calendarioEscolarMapper.updateEntityFromRequest(entity, request, anioEscolar);
        CalendarioEscolar guardado = calendarioEscolarRepository.save(entity);
        return calendarioEscolarMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarioEscolarResponse buscarPorId(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de calendario escolar no puede ser nulo.");
        }
        CalendarioEscolar entity = calendarioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fecha del calendario escolar no encontrada."));
        return calendarioEscolarMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CalendarioEscolarResponse> listar(Long anioEscolarId, TipoDiaCalendario tipoDia, Boolean esLectivo,
                                                  LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        return calendarioEscolarRepository.buscarConFiltros(anioEscolarId, tipoDia, esLectivo, fechaInicio, fechaFin, pageable)
                .map(calendarioEscolarMapper::toResponse);
    }

    @Override
    @Transactional
    public void activar(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de calendario escolar no puede ser nulo.");
        }
        CalendarioEscolar entity = calendarioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fecha del calendario escolar no encontrada."));

        if (calendarioEscolarRepository.existsByAnioEscolarIdAndFechaAndActivoTrueAndIdNot(
                entity.getAnioEscolar().getId(), entity.getFecha(), entity.getId())) {
            throw new BusinessException("Ya existe una configuración activa para esta fecha.");
        }

        entity.setActivo(true);
        calendarioEscolarRepository.save(entity);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de calendario escolar no puede ser nulo.");
        }
        CalendarioEscolar entity = calendarioEscolarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fecha del calendario escolar no encontrada."));
        entity.setActivo(false);
        calendarioEscolarRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CalendarioEscolarResponse> buscarPorFecha(Long anioEscolarId, LocalDate fecha) {
        if (anioEscolarId == null || fecha == null) {
            return Optional.empty();
        }
        return calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(anioEscolarId, fecha)
                .map(calendarioEscolarMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esDiaLectivo(Long anioEscolarId, LocalDate fecha) {
        if (anioEscolarId == null || fecha == null) {
            return false;
        }

        AnioEscolar anioEscolar = anioEscolarRepository.findById(anioEscolarId).orElse(null);
        if (anioEscolar == null) {
            return false;
        }

        // 1. Validar fecha dentro del periodo del año escolar
        if (fecha.isBefore(anioEscolar.getFechaInicio()) || fecha.isAfter(anioEscolar.getFechaFin())) {
            return false;
        }

        // 2. Buscar excepción activa para esa fecha
        Optional<CalendarioEscolar> excepcionOpt =
                calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(anioEscolarId, fecha);

        // 3. Si existe excepción activa, devolver su valor explícito de esLectivo
        if (excepcionOpt.isPresent()) {
            return Boolean.TRUE.equals(excepcionOpt.get().getEsLectivo());
        }

        // 4. Si no existe excepción: sábado y domingo no lectivos; lunes a viernes lectivos
        DayOfWeek diaSemana = fecha.getDayOfWeek();
        return diaSemana != DayOfWeek.SATURDAY && diaSemana != DayOfWeek.SUNDAY;
    }

    private void validarFechaDentroDePeriodo(LocalDate fecha, AnioEscolar anioEscolar) {
        if (fecha == null || anioEscolar == null) {
            throw new BusinessException("La fecha y el año escolar son obligatorios.");
        }
        if (fecha.isBefore(anioEscolar.getFechaInicio()) || fecha.isAfter(anioEscolar.getFechaFin())) {
            throw new BusinessException("La fecha no pertenece al periodo del año escolar.");
        }
    }
}
