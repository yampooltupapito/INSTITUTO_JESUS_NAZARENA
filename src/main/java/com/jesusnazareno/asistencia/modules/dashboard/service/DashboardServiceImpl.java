package com.jesusnazareno.asistencia.modules.dashboard.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.repository.JustificacionRepository;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardFiltro;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumen;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumenGrado;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardResumenSeccion;
import com.jesusnazareno.asistencia.modules.dashboard.dto.DashboardTendenciaDia;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final Locale LOCALE_PE = new Locale("es", "PE");
    private static final DateTimeFormatter FORMATTER_FECHA =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", LOCALE_PE);

    private final PersonaActorRepository personaActorRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final JustificacionRepository justificacionRepository;
    private final AnioEscolarRepository anioEscolarRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;
    private final com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService;
    private final com.jesusnazareno.asistencia.modules.alerta.repository.AlertaAsistenciaRepository alertaRepository;

    public DashboardServiceImpl(PersonaActorRepository personaActorRepository,
                                MatriculaRepository matriculaRepository,
                                AsistenciaRepository asistenciaRepository,
                                JustificacionRepository justificacionRepository,
                                AnioEscolarRepository anioEscolarRepository,
                                GradoRepository gradoRepository,
                                SeccionRepository seccionRepository) {
        this(personaActorRepository, matriculaRepository, asistenciaRepository, justificacionRepository,
                anioEscolarRepository, gradoRepository, seccionRepository, null, null);
    }

    public DashboardServiceImpl(PersonaActorRepository personaActorRepository,
                                MatriculaRepository matriculaRepository,
                                AsistenciaRepository asistenciaRepository,
                                JustificacionRepository justificacionRepository,
                                AnioEscolarRepository anioEscolarRepository,
                                GradoRepository gradoRepository,
                                SeccionRepository seccionRepository,
                                com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService) {
        this(personaActorRepository, matriculaRepository, asistenciaRepository, justificacionRepository,
                anioEscolarRepository, gradoRepository, seccionRepository, calendarioEscolarService, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public DashboardServiceImpl(PersonaActorRepository personaActorRepository,
                                MatriculaRepository matriculaRepository,
                                AsistenciaRepository asistenciaRepository,
                                JustificacionRepository justificacionRepository,
                                AnioEscolarRepository anioEscolarRepository,
                                GradoRepository gradoRepository,
                                SeccionRepository seccionRepository,
                                com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService,
                                com.jesusnazareno.asistencia.modules.alerta.repository.AlertaAsistenciaRepository alertaRepository) {
        this.personaActorRepository = personaActorRepository;
        this.matriculaRepository = matriculaRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.justificacionRepository = justificacionRepository;
        this.anioEscolarRepository = anioEscolarRepository;
        this.gradoRepository = gradoRepository;
        this.seccionRepository = seccionRepository;
        this.calendarioEscolarService = calendarioEscolarService;
        this.alertaRepository = alertaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResumen obtenerResumen() {
        return obtenerResumen(new DashboardFiltro());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResumen obtenerResumen(DashboardFiltro filtro) {
        DashboardFiltro filtroEfectivo = (filtro != null) ? filtro : new DashboardFiltro();

        // Si no se proporcionó año escolar, determinar año activo de la BD como contexto predeterminado
        if (filtroEfectivo.getAnioEscolarId() == null && anioEscolarRepository != null) {
            List<AnioEscolar> activos = anioEscolarRepository.findByActivoTrueOrderByAnioDesc();
            if (activos != null && !activos.isEmpty()) {
                filtroEfectivo.setAnioEscolarId(activos.get(0).getId());
            } else {
                Optional<AnioEscolar> activoOpt = anioEscolarRepository.findByActivoTrue();
                if (activoOpt != null && activoOpt.isPresent()) {
                    filtroEfectivo.setAnioEscolarId(activoOpt.get().getId());
                }
            }
        }

        validarFiltros(filtroEfectivo);

        LocalDate hoy = LocalDate.now(ZONA_LIMA);
        String fechaFormateada = formatearFecha(hoy);

        long totalEstudiantes = personaActorRepository
                .countByTipoActorCodigoAndActivoTrueAndPersonaActivoTrue("ESTUDIANTE");

        long totalMatriculas;
        if (!filtroEfectivo.tieneFiltros()) {
            totalMatriculas = matriculaRepository.countByEstadoAndAnioEscolarActivo(EstadoMatricula.MATRICULADO);
        } else {
            totalMatriculas = matriculaRepository.countMatriculasActivasConFiltros(
                    filtroEfectivo.getAnioEscolarId(), filtroEfectivo.getGradoId(), filtroEfectivo.getSeccionId());
        }

        long presentesHoy = 0;
        long tardanzasHoy = 0;
        long ausentesHoy = 0;
        long justificadosHoy = 0;

        if (!filtroEfectivo.tieneFiltros()) {
            presentesHoy = asistenciaRepository.countByFechaAndEstado(hoy, EstadoAsistencia.PRESENTE);
            tardanzasHoy = asistenciaRepository.countByFechaAndEstado(hoy, EstadoAsistencia.TARDANZA);
            ausentesHoy = asistenciaRepository.countByFechaAndEstado(hoy, EstadoAsistencia.AUSENTE);
            justificadosHoy = asistenciaRepository.countByFechaAndEstado(hoy, EstadoAsistencia.JUSTIFICADO);
        } else {
            List<Object[]> conteos = asistenciaRepository.contarPorEstadoParaReporte(
                    null, filtroEfectivo.getSeccionId(), filtroEfectivo.getGradoId(), filtroEfectivo.getAnioEscolarId(), null, hoy, hoy);
            if (conteos != null) {
                for (Object[] fila : conteos) {
                    EstadoAsistencia estado = (EstadoAsistencia) fila[0];
                    long cantidad = ((Number) fila[1]).longValue();
                    if (estado == EstadoAsistencia.PRESENTE) {
                        presentesHoy = cantidad;
                    } else if (estado == EstadoAsistencia.TARDANZA) {
                        tardanzasHoy = cantidad;
                    } else if (estado == EstadoAsistencia.AUSENTE) {
                        ausentesHoy = cantidad;
                    } else if (estado == EstadoAsistencia.JUSTIFICADO) {
                        justificadosHoy = cantidad;
                    }
                }
            }
        }

        long justificacionesPendientes = justificacionRepository
                .countByEstadoAndActivoTrue(EstadoJustificacion.PENDIENTE);

        long alertasPendientes = 0;
        if (alertaRepository != null) {
            alertasPendientes = alertaRepository.countByEstadoAndActivoTrue(
                    com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta.PENDIENTE);
        }

        // Nombres de contexto de filtros
        String anioEscolarNombre = null;
        if (filtroEfectivo.getAnioEscolarId() != null && anioEscolarRepository != null) {
            anioEscolarNombre = anioEscolarRepository.findById(filtroEfectivo.getAnioEscolarId())
                    .map(AnioEscolar::getNombre).orElse(null);
        }

        String gradoNombre = null;
        if (filtroEfectivo.getGradoId() != null && gradoRepository != null) {
            gradoNombre = gradoRepository.findById(filtroEfectivo.getGradoId())
                    .map(Grado::getNombre).orElse(null);
        }

        String seccionNombre = null;
        if (filtroEfectivo.getSeccionId() != null && seccionRepository != null) {
            seccionNombre = seccionRepository.findById(filtroEfectivo.getSeccionId())
                    .map(Seccion::getNombre).orElse(null);
        }

        // Resúmenes agrupados calculados eficientemente en BD
        List<DashboardResumenGrado> resumenGrados = obtenerResumenPorGrado(hoy, filtroEfectivo);
        List<DashboardResumenSeccion> resumenSecciones = obtenerResumenPorSeccion(hoy, filtroEfectivo);
        List<DashboardTendenciaDia> tendenciaDias = obtenerTendenciaUltimos7Dias(hoy, filtroEfectivo);

        boolean esDiaLectivo = true;
        String mensajeDiaLectivo = null;
        if (calendarioEscolarService != null && filtroEfectivo.getAnioEscolarId() != null) {
            esDiaLectivo = calendarioEscolarService.esDiaLectivo(filtroEfectivo.getAnioEscolarId(), hoy);
            if (!esDiaLectivo) {
                mensajeDiaLectivo = "Hoy no es un día lectivo.";
            }
        }

        return DashboardResumen.builder()
                .totalEstudiantes(totalEstudiantes)
                .totalMatriculas(totalMatriculas)
                .presentesHoy(presentesHoy)
                .tardanzasHoy(tardanzasHoy)
                .ausentesHoy(ausentesHoy)
                .justificadosHoy(justificadosHoy)
                .justificacionesPendientes(justificacionesPendientes)
                .fecha(hoy)
                .fechaFormateada(fechaFormateada)
                .anioEscolarId(filtroEfectivo.getAnioEscolarId())
                .anioEscolarNombre(anioEscolarNombre)
                .gradoId(filtroEfectivo.getGradoId())
                .gradoNombre(gradoNombre)
                .seccionId(filtroEfectivo.getSeccionId())
                .seccionNombre(seccionNombre)
                .resumenGrados(resumenGrados)
                .resumenSecciones(resumenSecciones)
                .tendenciaDias(tendenciaDias)
                .esDiaLectivo(esDiaLectivo)
                .mensajeDiaLectivo(mensajeDiaLectivo)
                .totalAlertasPendientes(alertasPendientes)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public void validarFiltros(DashboardFiltro filtro) {
        if (filtro == null) return;

        if (filtro.getAnioEscolarId() != null && anioEscolarRepository != null) {
            if (!anioEscolarRepository.existsById(filtro.getAnioEscolarId())) {
                throw new ResourceNotFoundException("Año escolar", "id", filtro.getAnioEscolarId());
            }
        }

        if (filtro.getGradoId() != null && gradoRepository != null) {
            if (!gradoRepository.existsById(filtro.getGradoId())) {
                throw new ResourceNotFoundException("Grado", "id", filtro.getGradoId());
            }
        }

        if (filtro.getSeccionId() != null && seccionRepository != null) {
            Seccion seccion = seccionRepository.findById(filtro.getSeccionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sección", "id", filtro.getSeccionId()));
            if (filtro.getGradoId() != null && seccion.getGrado() != null
                    && !seccion.getGrado().getId().equals(filtro.getGradoId())) {
                throw new BusinessException("La sección seleccionada no pertenece al grado especificado.");
            }
        }
    }

    private List<DashboardResumenGrado> obtenerResumenPorGrado(LocalDate fecha, DashboardFiltro filtro) {
        List<Object[]> resultados = asistenciaRepository.contarAsistenciaPorGrado(
                fecha, filtro.getAnioEscolarId(), filtro.getGradoId(), filtro.getSeccionId());

        if (resultados == null || resultados.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, DashboardResumenGrado> map = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            Long gradoId = (Long) fila[0];
            String gradoNombre = (String) fila[1];
            Integer gradoNumero = (Integer) fila[2];
            EstadoAsistencia estado = (EstadoAsistencia) fila[3];
            long cantidad = ((Number) fila[4]).longValue();

            DashboardResumenGrado item = map.computeIfAbsent(gradoId,
                    id -> new DashboardResumenGrado(id, gradoNombre, gradoNumero, 0, 0, 0, 0));

            if (estado == EstadoAsistencia.PRESENTE) {
                item.setPresentes(item.getPresentes() + cantidad);
            } else if (estado == EstadoAsistencia.TARDANZA) {
                item.setTardanzas(item.getTardanzas() + cantidad);
            } else if (estado == EstadoAsistencia.AUSENTE) {
                item.setAusentes(item.getAusentes() + cantidad);
            } else if (estado == EstadoAsistencia.JUSTIFICADO) {
                item.setJustificados(item.getJustificados() + cantidad);
            }
        }

        return new ArrayList<>(map.values());
    }

    private List<DashboardResumenSeccion> obtenerResumenPorSeccion(LocalDate fecha, DashboardFiltro filtro) {
        List<Object[]> resultados = asistenciaRepository.contarAsistenciaPorSeccion(
                fecha, filtro.getAnioEscolarId(), filtro.getGradoId(), filtro.getSeccionId());

        if (resultados == null || resultados.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, DashboardResumenSeccion> map = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            Long gradoId = (Long) fila[0];
            String gradoNombre = (String) fila[1];
            Long seccionId = (Long) fila[2];
            String seccionNombre = (String) fila[3];
            EstadoAsistencia estado = (EstadoAsistencia) fila[4];
            long cantidad = ((Number) fila[5]).longValue();

            DashboardResumenSeccion item = map.computeIfAbsent(seccionId,
                    id -> new DashboardResumenSeccion(gradoId, gradoNombre, id, seccionNombre, 0, 0, 0, 0));

            if (estado == EstadoAsistencia.PRESENTE) {
                item.setPresentes(item.getPresentes() + cantidad);
            } else if (estado == EstadoAsistencia.TARDANZA) {
                item.setTardanzas(item.getTardanzas() + cantidad);
            } else if (estado == EstadoAsistencia.AUSENTE) {
                item.setAusentes(item.getAusentes() + cantidad);
            } else if (estado == EstadoAsistencia.JUSTIFICADO) {
                item.setJustificados(item.getJustificados() + cantidad);
            }
        }

        return new ArrayList<>(map.values());
    }

    private List<DashboardTendenciaDia> obtenerTendenciaUltimos7Dias(LocalDate hoy, DashboardFiltro filtro) {
        LocalDate inicio = hoy.minusDays(6);
        Map<LocalDate, DashboardTendenciaDia> mapDias = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicio.plusDays(i);
            mapDias.put(dia, new DashboardTendenciaDia(dia));
        }

        List<Object[]> resultados = asistenciaRepository.contarAsistenciaTendencia(
                inicio, hoy, filtro.getAnioEscolarId(), filtro.getGradoId(), filtro.getSeccionId());

        if (resultados != null) {
            for (Object[] fila : resultados) {
                LocalDate fecha = (LocalDate) fila[0];
                EstadoAsistencia estado = (EstadoAsistencia) fila[1];
                long cantidad = ((Number) fila[2]).longValue();

                DashboardTendenciaDia dia = mapDias.get(fecha);
                if (dia != null) {
                    if (estado == EstadoAsistencia.PRESENTE) {
                        dia.setPresentes(dia.getPresentes() + cantidad);
                    } else if (estado == EstadoAsistencia.TARDANZA) {
                        dia.setTardanzas(dia.getTardanzas() + cantidad);
                    } else if (estado == EstadoAsistencia.AUSENTE) {
                        dia.setAusentes(dia.getAusentes() + cantidad);
                    } else if (estado == EstadoAsistencia.JUSTIFICADO) {
                        dia.setJustificados(dia.getJustificados() + cantidad);
                    }
                }
            }
        }

        return new ArrayList<>(mapDias.values());
    }

    private String formatearFecha(LocalDate fecha) {
        String texto = fecha.format(FORMATTER_FECHA);
        if (texto != null && !texto.isEmpty()) {
            return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
        }
        return texto;
    }
}
