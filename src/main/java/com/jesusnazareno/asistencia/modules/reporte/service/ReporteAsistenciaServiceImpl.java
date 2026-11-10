package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaItem;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResultado;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import com.jesusnazareno.asistencia.modules.reporte.dto.TipoReporteAsistencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de reportes de asistencia.
 * Procesa filtros en backend, ejecuta consultas JPA optimizadas y calcula indicadores estadísticos.
 */
@Service
@Transactional(readOnly = true)
public class ReporteAsistenciaServiceImpl implements ReporteAsistenciaService {

    private static final Logger log = LoggerFactory.getLogger(ReporteAsistenciaServiceImpl.class);
    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final long LIMITE_DIAS_CONSULTA = 366;
    private static final int MAX_REGISTROS_PDF = 1000;

    private final AsistenciaRepository asistenciaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AnioEscolarRepository anioEscolarRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;
    private final PersonaRepository personaRepository;
    private final PersonaActorRepository personaActorRepository;
    private final ReporteAsistenciaPdfService reporteAsistenciaPdfService;
    private final ReporteAsistenciaExcelService reporteAsistenciaExcelService;
    private final ReporteAsistenciaCsvService reporteAsistenciaCsvService;

    @org.springframework.beans.factory.annotation.Value("${reporte.exportacion.max-registros:5000}")
    private int maxRegistrosExportacion = 5000;

    @org.springframework.beans.factory.annotation.Autowired
    public ReporteAsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                        MatriculaRepository matriculaRepository,
                                        AnioEscolarRepository anioEscolarRepository,
                                        GradoRepository gradoRepository,
                                        SeccionRepository seccionRepository,
                                        PersonaRepository personaRepository,
                                        PersonaActorRepository personaActorRepository,
                                        ReporteAsistenciaPdfService reporteAsistenciaPdfService,
                                        ReporteAsistenciaExcelService reporteAsistenciaExcelService,
                                        ReporteAsistenciaCsvService reporteAsistenciaCsvService) {
        this.asistenciaRepository = asistenciaRepository;
        this.matriculaRepository = matriculaRepository;
        this.anioEscolarRepository = anioEscolarRepository;
        this.gradoRepository = gradoRepository;
        this.seccionRepository = seccionRepository;
        this.personaRepository = personaRepository;
        this.personaActorRepository = personaActorRepository;
        this.reporteAsistenciaPdfService = reporteAsistenciaPdfService;
        this.reporteAsistenciaExcelService = reporteAsistenciaExcelService;
        this.reporteAsistenciaCsvService = reporteAsistenciaCsvService;
    }

    public ReporteAsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                        MatriculaRepository matriculaRepository,
                                        AnioEscolarRepository anioEscolarRepository,
                                        GradoRepository gradoRepository,
                                        SeccionRepository seccionRepository,
                                        PersonaRepository personaRepository,
                                        PersonaActorRepository personaActorRepository,
                                        ReporteAsistenciaPdfService reporteAsistenciaPdfService) {
        this(asistenciaRepository, matriculaRepository, anioEscolarRepository, gradoRepository,
                seccionRepository, personaRepository, personaActorRepository, reporteAsistenciaPdfService,
                new ReporteAsistenciaExcelService(), new ReporteAsistenciaCsvService());
    }

    public int getMaxRegistrosExportacion() {
        return maxRegistrosExportacion;
    }

    public void setMaxRegistrosExportacion(int maxRegistrosExportacion) {
        this.maxRegistrosExportacion = maxRegistrosExportacion;
    }

    @Override
    public void validarFiltros(ReporteAsistenciaFiltro filtro) {
        if (filtro == null) {
            return;
        }

        // 1. Validación de fechas
        if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
            if (filtro.getFechaInicio().isAfter(filtro.getFechaFin())) {
                throw new ValidationException("El rango de fechas no es válido: la fecha de inicio ("
                        + filtro.getFechaInicio() + ") no puede ser posterior a la fecha de fin (" + filtro.getFechaFin() + ").");
            }
            long dias = ChronoUnit.DAYS.between(filtro.getFechaInicio(), filtro.getFechaFin());
            if (dias > LIMITE_DIAS_CONSULTA) {
                throw new BusinessException("El rango de fechas no puede exceder los 366 días (un año calendario).");
            }
        }

        // 2. Validación de Año Escolar
        if (filtro.getAnioEscolarId() != null) {
            if (!anioEscolarRepository.existsById(filtro.getAnioEscolarId())) {
                throw new ResourceNotFoundException("Año escolar no encontrado con ID: " + filtro.getAnioEscolarId());
            }
        }

        // 3. Validación de Grado
        if (filtro.getGradoId() != null) {
            if (!gradoRepository.existsById(filtro.getGradoId())) {
                throw new ResourceNotFoundException("Grado no encontrado con ID: " + filtro.getGradoId());
            }
        }

        // 4. Validación de Sección y pertenencia a Grado
        if (filtro.getSeccionId() != null) {
            Seccion seccion = seccionRepository.findById(filtro.getSeccionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + filtro.getSeccionId()));

            if (filtro.getGradoId() != null && !seccion.getGrado().getId().equals(filtro.getGradoId())) {
                throw new BusinessException("La sección no pertenece al grado seleccionado.");
            }
        }

        // 5. Validación de Estudiante
        if (filtro.getEstudianteId() != null) {
            if (!personaRepository.existsById(filtro.getEstudianteId())) {
                throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + filtro.getEstudianteId());
            }
            boolean esEstudiante = personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(
                    filtro.getEstudianteId(), "ESTUDIANTE");
            if (!esEstudiante) {
                throw new BusinessException("La persona seleccionada no es un estudiante activo.");
            }
        }
    }

    @Override
    public ReporteAsistenciaResultado generarReporte(ReporteAsistenciaFiltro filtro, Pageable pageable) {
        validarFiltros(filtro);
        normalizarFiltro(filtro);

        if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getSeccionId() != null) {
            return generarReporteDiarioSeccion(filtro, pageable);
        }

        // Búsqueda paginada en BD
        LocalDate fInicio = filtro.getFechaInicio();
        LocalDate fFin = filtro.getFechaFin();
        if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getFecha() != null) {
            fInicio = filtro.getFecha();
            fFin = filtro.getFecha();
        }

        Page<Asistencia> asistenciasPage = asistenciaRepository.buscarAsistenciasPaginadasParaReporte(
                filtro.getEstudianteId(),
                filtro.getSeccionId(),
                filtro.getGradoId(),
                filtro.getAnioEscolarId(),
                filtro.getEstado(),
                fInicio,
                fFin,
                pageable
        );

        List<ReporteAsistenciaItem> items = new ArrayList<>();
        int baseIndex = (int) pageable.getOffset() + 1;
        for (Asistencia a : asistenciasPage.getContent()) {
            items.add(mapearItem(a, baseIndex++));
        }

        Page<ReporteAsistenciaItem> pageResult = new PageImpl<>(items, pageable, asistenciasPage.getTotalElements());
        ReporteAsistenciaResumen resumen = calcularResumen(filtro);

        return new ReporteAsistenciaResultado(pageResult, items, resumen, filtro);
    }

    private ReporteAsistenciaResultado generarReporteDiarioSeccion(ReporteAsistenciaFiltro filtro, Pageable pageable) {
        Long anioId = filtro.getAnioEscolarId();
        if (anioId == null) {
            anioId = anioEscolarRepository.findByActivoTrue().map(AnioEscolar::getId).orElse(null);
            filtro.setAnioEscolarId(anioId);
        }

        LocalDate fecha = filtro.getFecha() != null ? filtro.getFecha() : LocalDate.now(ZONA_LIMA);
        filtro.setFecha(fecha);

        // 1. Obtener matrículas válidas de la sección
        List<Matricula> matriculas = anioId != null
                ? asistenciaRepository.findMatriculasValidasParaAsistencia(filtro.getSeccionId(), anioId)
                : Collections.emptyList();

        // 2. Obtener asistencias ya registradas para la fecha
        List<Asistencia> asistencias = asistenciaRepository.findBySeccionIdAndFecha(filtro.getSeccionId(), fecha);
        Map<Long, Asistencia> asistenciaPorMatricula = asistencias.stream()
                .collect(Collectors.toMap(a -> a.getMatricula().getId(), Function.identity(), (a1, a2) -> a1));

        // 3. Construir lista unificada
        List<ReporteAsistenciaItem> todosItems = new ArrayList<>();
        int num = 1;
        for (Matricula m : matriculas) {
            Asistencia asis = asistenciaPorMatricula.get(m.getId());

            // Si hay filtro por estado y no coincide, omitir
            if (filtro.getEstado() != null) {
                if (asis == null || asis.getEstado() != filtro.getEstado()) {
                    continue;
                }
            }

            ReporteAsistenciaItem item = new ReporteAsistenciaItem();
            item.setNumero(num++);
            item.setMatriculaId(m.getId());
            item.setEstudianteId(m.getEstudiante().getId());
            item.setCodigoMatricula(m.getCodigoMatricula());
            item.setNumeroDocumento(m.getEstudiante().getNumeroDocumento());
            item.setApellidos(m.getEstudiante().getApellidoPaterno() + " " + m.getEstudiante().getApellidoMaterno());
            item.setNombres(m.getEstudiante().getNombres());
            item.setEstudianteCompleto(m.getEstudiante().getApellidoPaterno() + " " + m.getEstudiante().getApellidoMaterno() + ", " + m.getEstudiante().getNombres());
            item.setAnioEscolar(String.valueOf(m.getAnioEscolar().getAnio()));
            item.setGrado(m.getGrado().getNombre());
            item.setSeccion(m.getSeccion().getNombre());
            item.setFecha(fecha);

            if (asis != null) {
                item.setAsistenciaId(asis.getId());
                item.setEstado(asis.getEstado());
                item.setHoraIngreso(asis.getHoraIngreso());
                item.setHoraSalida(asis.getHoraSalida());
                item.setObservacion(asis.getObservacion());
            } else {
                item.setEstado(null);
                item.setEstadoNombre("NO REGISTRADO");
            }

            todosItems.add(item);
        }

        // 4. Calcular resumen sobre las asistencias reales de esa fecha y sección
        long presentes = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.PRESENTE).count();
        long tardanzas = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.TARDANZA).count();
        long ausentes = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.AUSENTE).count();
        long justificados = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.JUSTIFICADO).count();
        long totalRegistros = presentes + tardanzas + ausentes + justificados;
        long totalMatriculados = matriculas.size();

        ReporteAsistenciaResumen resumen = new ReporteAsistenciaResumen(
                totalMatriculados, totalRegistros, presentes, tardanzas, ausentes, justificados
        );

        // 5. Paginación en memoria de la lista resultante
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), todosItems.size());
        List<ReporteAsistenciaItem> subList = start <= end ? todosItems.subList(start, end) : Collections.emptyList();

        Page<ReporteAsistenciaItem> pageResult = new PageImpl<>(subList, pageable, todosItems.size());
        return new ReporteAsistenciaResultado(pageResult, todosItems, resumen, filtro);
    }

    @Override
    public List<ReporteAsistenciaItem> obtenerTodosItems(ReporteAsistenciaFiltro filtro) {
        validarFiltros(filtro);
        normalizarFiltro(filtro);

        if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getSeccionId() != null) {
            ReporteAsistenciaResultado res = generarReporteDiarioSeccion(filtro, Pageable.unpaged());
            return res.getTodosItems();
        }

        LocalDate fInicio = filtro.getFechaInicio();
        LocalDate fFin = filtro.getFechaFin();
        if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getFecha() != null) {
            fInicio = filtro.getFecha();
            fFin = filtro.getFecha();
        }

        List<Asistencia> asistencias = asistenciaRepository.buscarAsistenciasParaReporte(
                filtro.getEstudianteId(),
                filtro.getSeccionId(),
                filtro.getGradoId(),
                filtro.getAnioEscolarId(),
                filtro.getEstado(),
                fInicio,
                fFin
        );

        // Protección frente a conjuntos masivos en memoria: límite configurable centralizado
        if (asistencias.size() > maxRegistrosExportacion) {
            throw new BusinessException("La consulta supera el límite permitido para exportación ("
                    + maxRegistrosExportacion + " registros). Por favor refine los filtros de búsqueda.");
        }

        List<ReporteAsistenciaItem> items = new ArrayList<>();
        int num = 1;
        for (Asistencia a : asistencias) {
            items.add(mapearItem(a, num++));
        }
        return items;
    }

    @Override
    public ReporteAsistenciaResumen calcularResumen(ReporteAsistenciaFiltro filtro) {
        normalizarFiltro(filtro);

        LocalDate fInicio = filtro.getFechaInicio();
        LocalDate fFin = filtro.getFechaFin();
        if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getFecha() != null) {
            fInicio = filtro.getFecha();
            fFin = filtro.getFecha();
        }

        List<Object[]> conteos = asistenciaRepository.contarPorEstadoParaReporte(
                filtro.getEstudianteId(),
                filtro.getSeccionId(),
                filtro.getGradoId(),
                filtro.getAnioEscolarId(),
                filtro.getEstado(),
                fInicio,
                fFin
        );

        long presentes = 0;
        long tardanzas = 0;
        long ausentes = 0;
        long justificados = 0;

        for (Object[] row : conteos) {
            EstadoAsistencia est = (EstadoAsistencia) row[0];
            Long cnt = ((Number) row[1]).longValue();
            if (est == EstadoAsistencia.PRESENTE) {
                presentes = cnt;
            } else if (est == EstadoAsistencia.TARDANZA) {
                tardanzas = cnt;
            } else if (est == EstadoAsistencia.AUSENTE) {
                ausentes = cnt;
            } else if (est == EstadoAsistencia.JUSTIFICADO) {
                justificados = cnt;
            }
        }

        long totalRegistros = presentes + tardanzas + ausentes + justificados;
        long totalMatriculados = 0;

        if (filtro.getSeccionId() != null && filtro.getAnioEscolarId() != null) {
            totalMatriculados = matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(
                    filtro.getSeccionId(), filtro.getAnioEscolarId(), EstadoMatricula.MATRICULADO);
        }

        return new ReporteAsistenciaResumen(
                totalMatriculados, totalRegistros, presentes, tardanzas, ausentes, justificados
        );
    }

    @Override
    public byte[] generarPdf(ReporteAsistenciaFiltro filtro) {
        validarFiltros(filtro);
        List<ReporteAsistenciaItem> items = obtenerTodosItems(filtro);
        ReporteAsistenciaResumen resumen = calcularResumen(filtro);

        String infoEncabezado = construirTextoFiltros(filtro);
        return reporteAsistenciaPdfService.generarPdf(filtro, items, resumen, infoEncabezado);
    }

    @Override
    public String generarNombreArchivoPdf(ReporteAsistenciaFiltro filtro) {
        String base = "reporte_asistencia";
        String sufijo = LocalDate.now(ZONA_LIMA).toString();

        if (filtro != null) {
            if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getFecha() != null) {
                sufijo = filtro.getFecha().toString();
            } else if (filtro.getTipoReporte() == TipoReporteAsistencia.POR_ESTUDIANTE && filtro.getEstudianteId() != null) {
                Optional<Persona> p = personaRepository.findById(filtro.getEstudianteId());
                if (p.isPresent()) {
                    sufijo = "estudiante_" + p.get().getNumeroDocumento();
                }
            } else if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
                sufijo = filtro.getFechaInicio() + "_al_" + filtro.getFechaFin();
            }
        }

        String nombre = base + "_" + sufijo + ".pdf";
        return nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    @Override
    public byte[] generarExcel(ReporteAsistenciaFiltro filtro) {
        validarFiltros(filtro);
        List<ReporteAsistenciaItem> items = obtenerTodosItems(filtro);
        ReporteAsistenciaResumen resumen = calcularResumen(filtro);

        String infoEncabezado = construirTextoFiltros(filtro);
        return reporteAsistenciaExcelService.generarExcel(filtro, items, resumen, infoEncabezado);
    }

    @Override
    public String generarNombreArchivoExcel(ReporteAsistenciaFiltro filtro) {
        String base = "reporte_asistencia";
        String sufijo = LocalDate.now(ZONA_LIMA).toString();

        if (filtro != null) {
            if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getFecha() != null) {
                sufijo = filtro.getFecha().toString();
            } else if (filtro.getTipoReporte() == TipoReporteAsistencia.POR_ESTUDIANTE && filtro.getEstudianteId() != null) {
                Optional<Persona> p = personaRepository.findById(filtro.getEstudianteId());
                if (p.isPresent()) {
                    sufijo = "estudiante_" + p.get().getNumeroDocumento();
                }
            } else if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
                sufijo = filtro.getFechaInicio() + "_al_" + filtro.getFechaFin();
            }
        }

        String nombre = base + "_" + sufijo + ".xlsx";
        return nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    @Override
    public byte[] generarCsv(ReporteAsistenciaFiltro filtro) {
        validarFiltros(filtro);
        List<ReporteAsistenciaItem> items = obtenerTodosItems(filtro);
        ReporteAsistenciaResumen resumen = calcularResumen(filtro);

        return reporteAsistenciaCsvService.generarCsv(filtro, items, resumen);
    }

    @Override
    public String generarNombreArchivoCsv(ReporteAsistenciaFiltro filtro) {
        String base = "reporte_asistencia";
        String sufijo = LocalDate.now(ZONA_LIMA).toString();

        if (filtro != null) {
            if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getFecha() != null) {
                sufijo = filtro.getFecha().toString();
            } else if (filtro.getTipoReporte() == TipoReporteAsistencia.POR_ESTUDIANTE && filtro.getEstudianteId() != null) {
                Optional<Persona> p = personaRepository.findById(filtro.getEstudianteId());
                if (p.isPresent()) {
                    sufijo = "estudiante_" + p.get().getNumeroDocumento();
                }
            } else if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
                sufijo = filtro.getFechaInicio() + "_al_" + filtro.getFechaFin();
            }
        }

        String nombre = base + "_" + sufijo + ".csv";
        return nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void normalizarFiltro(ReporteAsistenciaFiltro filtro) {
        if (filtro == null) return;

        // Por defecto año activo si no se especificó
        if (filtro.getAnioEscolarId() == null) {
            anioEscolarRepository.findByActivoTrue().ifPresent(a -> filtro.setAnioEscolarId(a.getId()));
        }

        if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION) {
            if (filtro.getFecha() == null) {
                filtro.setFecha(LocalDate.now(ZONA_LIMA));
            }
        } else if (filtro.getTipoReporte() == TipoReporteAsistencia.RANGO_FECHAS
                || filtro.getTipoReporte() == TipoReporteAsistencia.RESUMEN_SECCION
                || filtro.getTipoReporte() == TipoReporteAsistencia.POR_ESTUDIANTE) {
            if (filtro.getFechaFin() == null) {
                filtro.setFechaFin(LocalDate.now(ZONA_LIMA));
            }
            if (filtro.getFechaInicio() == null) {
                filtro.setFechaInicio(filtro.getFechaFin().withDayOfMonth(1));
            }
        }
    }

    private ReporteAsistenciaItem mapearItem(Asistencia a, int numero) {
        ReporteAsistenciaItem item = new ReporteAsistenciaItem();
        item.setNumero(numero);
        item.setAsistenciaId(a.getId());
        item.setMatriculaId(a.getMatricula().getId());
        item.setEstudianteId(a.getMatricula().getEstudiante().getId());
        item.setCodigoMatricula(a.getMatricula().getCodigoMatricula());
        item.setNumeroDocumento(a.getMatricula().getEstudiante().getNumeroDocumento());
        item.setApellidos(a.getMatricula().getEstudiante().getApellidoPaterno() + " " + a.getMatricula().getEstudiante().getApellidoMaterno());
        item.setNombres(a.getMatricula().getEstudiante().getNombres());
        item.setEstudianteCompleto(a.getMatricula().getEstudiante().getApellidoPaterno() + " "
                + a.getMatricula().getEstudiante().getApellidoMaterno() + ", "
                + a.getMatricula().getEstudiante().getNombres());
        item.setAnioEscolar(String.valueOf(a.getMatricula().getAnioEscolar().getAnio()));
        item.setGrado(a.getMatricula().getGrado().getNombre());
        item.setSeccion(a.getMatricula().getSeccion().getNombre());
        item.setFecha(a.getFecha());
        item.setEstado(a.getEstado());
        item.setHoraIngreso(a.getHoraIngreso());
        item.setHoraSalida(a.getHoraSalida());
        item.setObservacion(a.getObservacion());
        return item;
    }

    private String construirTextoFiltros(ReporteAsistenciaFiltro filtro) {
        StringBuilder sb = new StringBuilder();
        if (filtro.getAnioEscolarId() != null) {
            anioEscolarRepository.findById(filtro.getAnioEscolarId()).ifPresent(a -> sb.append("Año: ").append(a.getAnio()).append(" | "));
        }
        if (filtro.getGradoId() != null) {
            gradoRepository.findById(filtro.getGradoId()).ifPresent(g -> sb.append("Grado: ").append(g.getNombre()).append(" | "));
        }
        if (filtro.getSeccionId() != null) {
            seccionRepository.findById(filtro.getSeccionId()).ifPresent(s -> sb.append("Sección: ").append(s.getNombre()).append(" | "));
        }
        if (filtro.getEstudianteId() != null) {
            personaRepository.findById(filtro.getEstudianteId()).ifPresent(p ->
                    sb.append("Estudiante: ").append(p.getApellidoPaterno()).append(" ").append(p.getNombres())
                            .append(" (").append(p.getNumeroDocumento()).append(") | "));
        }
        if (filtro.getTipoReporte() == TipoReporteAsistencia.DIARIO_SECCION && filtro.getFecha() != null) {
            sb.append("Fecha: ").append(filtro.getFecha().format(FORMATO_FECHA)).append(" | ");
        } else if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
            sb.append("Periodo: ").append(filtro.getFechaInicio().format(FORMATO_FECHA))
                    .append(" al ").append(filtro.getFechaFin().format(FORMATO_FECHA)).append(" | ");
        }
        if (filtro.getEstado() != null) {
            sb.append("Estado: ").append(filtro.getEstado().name()).append(" | ");
        }

        String str = sb.toString();
        if (str.endsWith(" | ")) {
            str = str.substring(0, str.length() - 3);
        }
        return str.isBlank() ? "Sin filtros específicos" : str;
    }
}
