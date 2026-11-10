package com.jesusnazareno.asistencia.modules.alerta.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.alerta.domain.AlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.domain.ConfiguracionAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.dto.AlertaAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ResolucionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.RevisionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.event.AlertaEvaluacionEvent;
import com.jesusnazareno.asistencia.modules.alerta.mapper.AlertaAsistenciaMapper;
import com.jesusnazareno.asistencia.modules.alerta.mapper.ConfiguracionAlertaMapper;
import com.jesusnazareno.asistencia.modules.alerta.repository.AlertaAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.alerta.repository.ConfiguracionAlertaAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementación del servicio de alertas internas de asistencia.
 */
@Service
public class AlertaAsistenciaServiceImpl implements AlertaAsistenciaService {

    private static final ZoneId ZONE_LIMA = ZoneId.of("America/Lima");
    private static final int DEFAULT_UMBRAL_CONSECUTIVAS = 3;
    private static final int DEFAULT_UMBRAL_ACUMULADAS = 5;
    private static final int DEFAULT_UMBRAL_TARDANZAS = 5;

    private final AlertaAsistenciaRepository alertaRepository;
    private final ConfiguracionAlertaAsistenciaRepository configuracionRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final CalendarioEscolarService calendarioEscolarService;
    private final UsuarioRepository usuarioRepository;
    private final AlertaAsistenciaMapper alertaMapper;
    private final ConfiguracionAlertaMapper configuracionMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    public AlertaAsistenciaServiceImpl(
            AlertaAsistenciaRepository alertaRepository,
            ConfiguracionAlertaAsistenciaRepository configuracionRepository,
            MatriculaRepository matriculaRepository,
            AsistenciaRepository asistenciaRepository,
            CalendarioEscolarService calendarioEscolarService,
            UsuarioRepository usuarioRepository,
            AlertaAsistenciaMapper alertaMapper,
            ConfiguracionAlertaMapper configuracionMapper) {
        this(alertaRepository, configuracionRepository, matriculaRepository, asistenciaRepository,
             calendarioEscolarService, usuarioRepository, alertaMapper, configuracionMapper, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AlertaAsistenciaServiceImpl(
            AlertaAsistenciaRepository alertaRepository,
            ConfiguracionAlertaAsistenciaRepository configuracionRepository,
            MatriculaRepository matriculaRepository,
            AsistenciaRepository asistenciaRepository,
            CalendarioEscolarService calendarioEscolarService,
            UsuarioRepository usuarioRepository,
            AlertaAsistenciaMapper alertaMapper,
            ConfiguracionAlertaMapper configuracionMapper,
            org.springframework.context.ApplicationEventPublisher eventPublisher) {
        this.alertaRepository = alertaRepository;
        this.configuracionRepository = configuracionRepository;
        this.matriculaRepository = matriculaRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.calendarioEscolarService = calendarioEscolarService;
        this.usuarioRepository = usuarioRepository;
        this.alertaMapper = alertaMapper;
        this.configuracionMapper = configuracionMapper;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onAlertaEvaluacionEvent(AlertaEvaluacionEvent event) {
        if (event != null && event.getMatriculaId() != null) {
            evaluarMatricula(event.getMatriculaId());
        }
    }

    @Override
    @Transactional
    public void evaluarMatricula(Long matriculaId) {
        if (matriculaId == null) {
            return;
        }

        Optional<Matricula> matriculaOpt = matriculaRepository.findById(matriculaId);
        if (matriculaOpt.isEmpty()) {
            return;
        }

        Matricula matricula = matriculaOpt.get();
        AnioEscolar anioEscolar = matricula.getAnioEscolar();
        if (anioEscolar == null) {
            return;
        }

        LocalDate fechaInicioPeriodo = anioEscolar.getFechaInicio();
        LocalDate fechaFinPeriodo = anioEscolar.getFechaFin();
        LocalDate hoy = LocalDate.now(ZONE_LIMA);

        // Obtener umbrales configurados
        int umbralConsecutivas = obtenerUmbral(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS, DEFAULT_UMBRAL_CONSECUTIVAS);
        int umbralAcumuladas = obtenerUmbral(TipoAlertaAsistencia.AUSENCIAS_ACUMULADAS, DEFAULT_UMBRAL_ACUMULADAS);
        int umbralTardanzas = obtenerUmbral(TipoAlertaAsistencia.TARDANZAS_ACUMULADAS, DEFAULT_UMBRAL_TARDANZAS);

        // Obtener historial de asistencias ordenadas cronológicamente dentro del año escolar
        List<Asistencia> asistencias = asistenciaRepository.findByMatriculaIdAndFechaBetweenOrderByFechaAsc(
                matriculaId, fechaInicioPeriodo, fechaFinPeriodo);

        // 1. Evaluar AUSENCIAS_CONSECUTIVAS considerando solo días lectivos
        evaluarAusenciasConsecutivas(matricula, anioEscolar, asistencias, umbralConsecutivas, hoy);

        // 2. Evaluar AUSENCIAS_ACUMULADAS
        evaluarAusenciasAcumuladas(matricula, anioEscolar, asistencias, umbralAcumuladas, hoy);

        // 3. Evaluar TARDANZAS_ACUMULADAS
        evaluarTardanzasAcumuladas(matricula, anioEscolar, asistencias, umbralTardanzas, hoy);
    }

    private void evaluarAusenciasConsecutivas(
            Matricula matricula, AnioEscolar anioEscolar, List<Asistencia> asistencias, int umbral, LocalDate hoy) {

        int maxConsecutivas = 0;
        LocalDate maxInicio = null;
        LocalDate maxFin = null;

        int currentStreak = 0;
        LocalDate currentInicio = null;
        LocalDate currentFin = null;
        LocalDate ultimaFechaAusente = null;

        for (Asistencia a : asistencias) {
            if (a.getEstado() == EstadoAsistencia.AUSENTE) {
                if (currentStreak == 0) {
                    currentStreak = 1;
                    currentInicio = a.getFecha();
                    currentFin = a.getFecha();
                    ultimaFechaAusente = a.getFecha();
                } else {
                    // Verificar si todos los días intermedios fueron no lectivos
                    boolean soloDiasNoLectivos = true;
                    LocalDate d = ultimaFechaAusente.plusDays(1);
                    while (d.isBefore(a.getFecha())) {
                        if (calendarioEscolarService.esDiaLectivo(anioEscolar.getId(), d)) {
                            soloDiasNoLectivos = false;
                            break;
                        }
                        d = d.plusDays(1);
                    }

                    if (soloDiasNoLectivos) {
                        currentStreak++;
                        currentFin = a.getFecha();
                        ultimaFechaAusente = a.getFecha();
                    } else {
                        // Hubo al menos un día lectivo entre ausencias -> se rompe la racha
                        if (currentStreak > maxConsecutivas) {
                            maxConsecutivas = currentStreak;
                            maxInicio = currentInicio;
                            maxFin = currentFin;
                        }
                        currentStreak = 1;
                        currentInicio = a.getFecha();
                        currentFin = a.getFecha();
                        ultimaFechaAusente = a.getFecha();
                    }
                }
            } else {
                // PRESENTE, TARDANZA o JUSTIFICADO rompen la racha consecutiva
                if (currentStreak > maxConsecutivas) {
                    maxConsecutivas = currentStreak;
                    maxInicio = currentInicio;
                    maxFin = currentFin;
                }
                currentStreak = 0;
                currentInicio = null;
                currentFin = null;
                ultimaFechaAusente = null;
            }
        }

        if (currentStreak > maxConsecutivas) {
            maxConsecutivas = currentStreak;
            maxInicio = currentInicio;
            maxFin = currentFin;
        }

        // Buscar alerta activa existente para deduplicación
        Optional<AlertaAsistencia> alertaActivaOpt = alertaRepository
                .findFirstByMatriculaIdAndTipoAndFechaInicioPeriodoAndEstadoInAndActivoTrue(
                        matricula.getId(),
                        TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS,
                        anioEscolar.getFechaInicio(),
                        Set.of(EstadoAlerta.PENDIENTE, EstadoAlerta.EN_REVISION)
                );

        if (maxConsecutivas >= umbral) {
            String desc = String.format("Estudiante presenta %d ausencias consecutivas en días lectivos.", maxConsecutivas);
            if (alertaActivaOpt.isPresent()) {
                AlertaAsistencia alertaExistente = alertaActivaOpt.get();
                if (alertaExistente.getCantidad() != maxConsecutivas || !alertaExistente.getFechaFinPeriodo().equals(maxFin)) {
                    alertaExistente.setCantidad(maxConsecutivas);
                    alertaExistente.setFechaFinPeriodo(maxFin != null ? maxFin : hoy);
                    alertaExistente.setDescripcion(desc);
                    alertaRepository.save(alertaExistente);
                }
            } else {
                AlertaAsistencia nuevaAlerta = AlertaAsistencia.builder()
                        .matricula(matricula)
                        .tipo(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS)
                        .fechaDeteccion(hoy)
                        .fechaInicioPeriodo(anioEscolar.getFechaInicio())
                        .fechaFinPeriodo(maxFin != null ? maxFin : hoy)
                        .cantidad(maxConsecutivas)
                        .estado(EstadoAlerta.PENDIENTE)
                        .descripcion(desc)
                        .activo(true)
                        .build();
                AlertaAsistencia guardada = alertaRepository.save(nuevaAlerta);
                if (eventPublisher != null) {
                    eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionAlertaEvent(guardada.getId()));
                }
            }
        } else {
            // Si la alerta estaba PENDIENTE pero ya no cumple el umbral (por justificación aprobada), desactivarla
            if (alertaActivaOpt.isPresent()) {
                AlertaAsistencia alertaExistente = alertaActivaOpt.get();
                if (alertaExistente.getEstado() == EstadoAlerta.PENDIENTE) {
                    alertaExistente.setActivo(false);
                    alertaExistente.setObservacionRevision("Desactivada automáticamente: las ausencias consecutivas se redujeron a " + maxConsecutivas);
                    alertaRepository.save(alertaExistente);
                }
            }
        }
    }

    private void evaluarAusenciasAcumuladas(
            Matricula matricula, AnioEscolar anioEscolar, List<Asistencia> asistencias, int umbral, LocalDate hoy) {

        int totalAusencias = 0;
        LocalDate ultimaFecha = null;

        for (Asistencia a : asistencias) {
            if (a.getEstado() == EstadoAsistencia.AUSENTE) {
                totalAusencias++;
                ultimaFecha = a.getFecha();
            }
        }

        Optional<AlertaAsistencia> alertaActivaOpt = alertaRepository
                .findFirstByMatriculaIdAndTipoAndFechaInicioPeriodoAndEstadoInAndActivoTrue(
                        matricula.getId(),
                        TipoAlertaAsistencia.AUSENCIAS_ACUMULADAS,
                        anioEscolar.getFechaInicio(),
                        Set.of(EstadoAlerta.PENDIENTE, EstadoAlerta.EN_REVISION)
                );

        if (totalAusencias >= umbral) {
            String desc = String.format("Estudiante acumula %d ausencias en el año escolar %s.",
                    totalAusencias, anioEscolar.getNombre());
            LocalDate finPeriodo = (ultimaFecha != null) ? ultimaFecha : hoy;

            if (alertaActivaOpt.isPresent()) {
                AlertaAsistencia alertaExistente = alertaActivaOpt.get();
                if (alertaExistente.getCantidad() != totalAusencias) {
                    alertaExistente.setCantidad(totalAusencias);
                    alertaExistente.setFechaFinPeriodo(finPeriodo);
                    alertaExistente.setDescripcion(desc);
                    alertaRepository.save(alertaExistente);
                }
            } else {
                AlertaAsistencia nuevaAlerta = AlertaAsistencia.builder()
                        .matricula(matricula)
                        .tipo(TipoAlertaAsistencia.AUSENCIAS_ACUMULADAS)
                        .fechaDeteccion(hoy)
                        .fechaInicioPeriodo(anioEscolar.getFechaInicio())
                        .fechaFinPeriodo(finPeriodo)
                        .cantidad(totalAusencias)
                        .estado(EstadoAlerta.PENDIENTE)
                        .descripcion(desc)
                        .activo(true)
                        .build();
                AlertaAsistencia guardada = alertaRepository.save(nuevaAlerta);
                if (eventPublisher != null) {
                    eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionAlertaEvent(guardada.getId()));
                }
            }
        } else {
            if (alertaActivaOpt.isPresent()) {
                AlertaAsistencia alertaExistente = alertaActivaOpt.get();
                if (alertaExistente.getEstado() == EstadoAlerta.PENDIENTE) {
                    alertaExistente.setActivo(false);
                    alertaExistente.setObservacionRevision("Desactivada automáticamente: las ausencias acumuladas se redujeron a " + totalAusencias);
                    alertaRepository.save(alertaExistente);
                }
            }
        }
    }

    private void evaluarTardanzasAcumuladas(
            Matricula matricula, AnioEscolar anioEscolar, List<Asistencia> asistencias, int umbral, LocalDate hoy) {

        int totalTardanzas = 0;
        LocalDate ultimaFecha = null;

        for (Asistencia a : asistencias) {
            if (a.getEstado() == EstadoAsistencia.TARDANZA) {
                totalTardanzas++;
                ultimaFecha = a.getFecha();
            }
        }

        Optional<AlertaAsistencia> alertaActivaOpt = alertaRepository
                .findFirstByMatriculaIdAndTipoAndFechaInicioPeriodoAndEstadoInAndActivoTrue(
                        matricula.getId(),
                        TipoAlertaAsistencia.TARDANZAS_ACUMULADAS,
                        anioEscolar.getFechaInicio(),
                        Set.of(EstadoAlerta.PENDIENTE, EstadoAlerta.EN_REVISION)
                );

        if (totalTardanzas >= umbral) {
            String desc = String.format("Estudiante acumula %d tardanzas en el año escolar %s.",
                    totalTardanzas, anioEscolar.getNombre());
            LocalDate finPeriodo = (ultimaFecha != null) ? ultimaFecha : hoy;

            if (alertaActivaOpt.isPresent()) {
                AlertaAsistencia alertaExistente = alertaActivaOpt.get();
                if (alertaExistente.getCantidad() != totalTardanzas) {
                    alertaExistente.setCantidad(totalTardanzas);
                    alertaExistente.setFechaFinPeriodo(finPeriodo);
                    alertaExistente.setDescripcion(desc);
                    alertaRepository.save(alertaExistente);
                }
            } else {
                AlertaAsistencia nuevaAlerta = AlertaAsistencia.builder()
                        .matricula(matricula)
                        .tipo(TipoAlertaAsistencia.TARDANZAS_ACUMULADAS)
                        .fechaDeteccion(hoy)
                        .fechaInicioPeriodo(anioEscolar.getFechaInicio())
                        .fechaFinPeriodo(finPeriodo)
                        .cantidad(totalTardanzas)
                        .estado(EstadoAlerta.PENDIENTE)
                        .descripcion(desc)
                        .activo(true)
                        .build();
                AlertaAsistencia guardada = alertaRepository.save(nuevaAlerta);
                if (eventPublisher != null) {
                    eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionAlertaEvent(guardada.getId()));
                }
            }
        } else {
            if (alertaActivaOpt.isPresent()) {
                AlertaAsistencia alertaExistente = alertaActivaOpt.get();
                if (alertaExistente.getEstado() == EstadoAlerta.PENDIENTE) {
                    alertaExistente.setActivo(false);
                    alertaExistente.setObservacionRevision("Desactivada automáticamente: las tardanzas acumuladas se redujeron a " + totalTardanzas);
                    alertaRepository.save(alertaExistente);
                }
            }
        }
    }

    private int obtenerUmbral(TipoAlertaAsistencia tipo, int valorPorDefecto) {
        return configuracionRepository.findByTipoAndActivoTrue(tipo)
                .map(ConfiguracionAlertaAsistencia::getUmbral)
                .filter(u -> u > 0)
                .orElse(valorPorDefecto);
    }

    @Override
    @Transactional
    public void recalcularTodasLasAlertas() {
        List<Matricula> matriculasActivas = matriculaRepository.findByEstado(EstadoMatricula.MATRICULADO);
        for (Matricula m : matriculasActivas) {
            evaluarMatricula(m.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertaAsistenciaResponse> buscarAlertas(
            String q, TipoAlertaAsistencia tipo, EstadoAlerta estado,
            Long anioEscolarId, Long gradoId, Long seccionId,
            LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {

        String cleanQ = (q != null && !q.isBlank()) ? q.trim() : null;
        return alertaRepository.buscarAlertas(
                cleanQ, tipo, estado, anioEscolarId, gradoId, seccionId,
                fechaInicio, fechaFin, pageable
        ).map(alertaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AlertaAsistenciaResponse buscarPorId(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de alerta no puede ser nulo.");
        }
        AlertaAsistencia alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + id));
        return alertaMapper.toResponse(alerta);
    }

    @Override
    @Transactional
    public AlertaAsistenciaResponse marcarEnRevision(Long id, RevisionAlertaRequest request, Long usuarioId) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de alerta no puede ser nulo.");
        }
        AlertaAsistencia alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + id));

        if (alerta.getEstado() != EstadoAlerta.PENDIENTE) {
            throw new BusinessException("La alerta no se encuentra en un estado válido para esta operación.");
        }

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        }

        alerta.setEstado(EstadoAlerta.EN_REVISION);
        alerta.setRevisadoPor(usuario);
        alerta.setFechaRevision(LocalDateTime.now(ZONE_LIMA));
        if (request != null && request.getObservacion() != null && !request.getObservacion().isBlank()) {
            alerta.setObservacionRevision(request.getObservacion().trim());
        }

        AlertaAsistencia guardada = alertaRepository.save(alerta);
        return alertaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public AlertaAsistenciaResponse resolver(Long id, ResolucionAlertaRequest request, Long usuarioId) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de alerta no puede ser nulo.");
        }
        if (request == null || request.getObservacion() == null || request.getObservacion().isBlank()) {
            throw new BusinessException("La observación de resolución es obligatoria.");
        }

        AlertaAsistencia alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + id));

        // Transición permitida desde PENDIENTE o EN_REVISION
        if (alerta.getEstado() == EstadoAlerta.RESUELTA) {
            throw new BusinessException("La alerta ya se encuentra en estado RESUELTA.");
        }

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        }

        alerta.setEstado(EstadoAlerta.RESUELTA);
        alerta.setRevisadoPor(usuario);
        alerta.setFechaRevision(LocalDateTime.now(ZONE_LIMA));
        alerta.setObservacionRevision(request.getObservacion().trim());

        AlertaAsistencia guardada = alertaRepository.save(alerta);
        return alertaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarAlertasPendientes() {
        return alertaRepository.countByEstadoAndActivoTrue(EstadoAlerta.PENDIENTE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionAlertaResponse> listarConfiguraciones() {
        return configuracionRepository.findAllByOrderByTipoAsc().stream()
                .map(configuracionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConfiguracionAlertaResponse actualizarConfiguracion(ConfiguracionAlertaRequest request) {
        if (request == null) {
            throw new BusinessException("Los datos de configuración no pueden ser nulos.");
        }
        if (request.getTipo() == null) {
            throw new BusinessException("El tipo de alerta es obligatorio.");
        }
        if (request.getUmbral() == null || request.getUmbral() <= 0) {
            throw new BusinessException("El umbral debe ser mayor que cero.");
        }

        ConfiguracionAlertaAsistencia config = configuracionRepository.findByTipo(request.getTipo())
                .orElseGet(() -> ConfiguracionAlertaAsistencia.builder()
                        .tipo(request.getTipo())
                        .umbral(request.getUmbral())
                        .activo(true)
                        .build());

        config.setUmbral(request.getUmbral());
        config.setActivo(true);

        ConfiguracionAlertaAsistencia guardada = configuracionRepository.save(config);
        return configuracionMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionAlertaResponse buscarConfiguracionPorTipo(TipoAlertaAsistencia tipo) {
        if (tipo == null) {
            throw new BusinessException("El tipo de alerta no puede ser nulo.");
        }
        ConfiguracionAlertaAsistencia config = configuracionRepository.findByTipo(tipo)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada para el tipo: " + tipo));
        return configuracionMapper.toResponse(config);
    }
}
