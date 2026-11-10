package com.jesusnazareno.asistencia.modules.reporte.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResultado;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import com.jesusnazareno.asistencia.modules.reporte.dto.TipoReporteAsistencia;
import com.jesusnazareno.asistencia.modules.reporte.service.ReporteAsistenciaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador Thymeleaf para el módulo de Reportes de Asistencia.
 * Rutas protegidas mediante @PreAuthorize("hasAuthority('REPORTE_ASISTENCIA')").
 */
@Controller
@RequestMapping("/reportes/asistencia")
@PreAuthorize("hasAuthority('REPORTE_ASISTENCIA')")
public class ReporteAsistenciaController {

    private static final Logger log = LoggerFactory.getLogger(ReporteAsistenciaController.class);
    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

    private final ReporteAsistenciaService reporteAsistenciaService;
    private final AnioEscolarRepository anioEscolarRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;
    private final PersonaRepository personaRepository;
    private final PersonaActorRepository personaActorRepository;
    private final MatriculaRepository matriculaRepository;
    private final com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService;

    public ReporteAsistenciaController(ReporteAsistenciaService reporteAsistenciaService,
                                       AnioEscolarRepository anioEscolarRepository,
                                       GradoRepository gradoRepository,
                                       SeccionRepository seccionRepository,
                                       PersonaRepository personaRepository,
                                       PersonaActorRepository personaActorRepository,
                                       MatriculaRepository matriculaRepository) {
        this(reporteAsistenciaService, anioEscolarRepository, gradoRepository, seccionRepository,
             personaRepository, personaActorRepository, matriculaRepository, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public ReporteAsistenciaController(ReporteAsistenciaService reporteAsistenciaService,
                                       AnioEscolarRepository anioEscolarRepository,
                                       GradoRepository gradoRepository,
                                       SeccionRepository seccionRepository,
                                       PersonaRepository personaRepository,
                                       PersonaActorRepository personaActorRepository,
                                       MatriculaRepository matriculaRepository,
                                       com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService) {
        this.reporteAsistenciaService = reporteAsistenciaService;
        this.anioEscolarRepository = anioEscolarRepository;
        this.gradoRepository = gradoRepository;
        this.seccionRepository = seccionRepository;
        this.personaRepository = personaRepository;
        this.personaActorRepository = personaActorRepository;
        this.matriculaRepository = matriculaRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Vista principal de reportes de asistencia con filtros y resultados paginados.
     */
    @GetMapping({"", "/", "/consultar"})
    public String verReporte(@ModelAttribute("filtro") ReporteAsistenciaFiltro filtro,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
        // Límite de paginación seguro
        int pageSize = Math.min(Math.max(size, 1), 100);
        int pageNumber = Math.max(page, 0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        cargarDatosFormulario(filtro, model);

        try {
            ReporteAsistenciaResultado resultado = reporteAsistenciaService.generarReporte(filtro, pageable);
            model.addAttribute("resultado", resultado);
            model.addAttribute("pagina", resultado.getPaginaItems());
            model.addAttribute("resumen", resultado.getResumen());
            model.addAttribute("filtro", resultado.getFiltro());
        } catch (ValidationException | BusinessException e) {
            log.warn("Error en filtros de reporte: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pagina", Page.empty(pageable));
            model.addAttribute("resumen", new ReporteAsistenciaResumen());
        } catch (Exception e) {
            log.error("Error inesperado en consulta de reporte: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ocurrió un error al procesar la consulta: " + e.getMessage());
            model.addAttribute("pagina", Page.empty(pageable));
            model.addAttribute("resumen", new ReporteAsistenciaResumen());
        }

        return "reportes/asistencia";
    }

    /**
     * Generación y descarga directa del reporte de asistencia en formato PDF.
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(@ModelAttribute ReporteAsistenciaFiltro filtro) {
        try {
            reporteAsistenciaService.validarFiltros(filtro);
            byte[] pdfBytes = reporteAsistenciaService.generarPdf(filtro);
            String filename = reporteAsistenciaService.generarNombreArchivoPdf(filtro);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(pdfBytes.length);

            if (auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.REPORTE_PDF,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.REPORTES,
                        "REPORTE",
                        null,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Exportación de reporte PDF: " + filename,
                        "Tipo: " + filtro.getTipoReporte()
                );
            }

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (ValidationException | BusinessException e) {
            log.warn("Filtro no válido para generación PDF: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al generar PDF de reporte: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generación y descarga directa del reporte de asistencia en formato Excel (.xlsx).
     */
    @GetMapping("/excel")
    public ResponseEntity<byte[]> descargarExcel(@ModelAttribute ReporteAsistenciaFiltro filtro) {
        try {
            reporteAsistenciaService.validarFiltros(filtro);
            byte[] excelBytes = reporteAsistenciaService.generarExcel(filtro);
            String filename = reporteAsistenciaService.generarNombreArchivoExcel(filtro);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(excelBytes.length);

            if (auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.REPORTE_EXCEL,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.REPORTES,
                        "REPORTE",
                        null,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Exportación de reporte Excel: " + filename,
                        "Tipo: " + filtro.getTipoReporte()
                );
            }

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (ValidationException | BusinessException e) {
            log.warn("Filtro no válido para generación Excel: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al generar Excel de reporte: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generación y descarga directa del reporte de asistencia en formato CSV (UTF-8 con BOM).
     */
    @GetMapping("/csv")
    public ResponseEntity<byte[]> descargarCsv(@ModelAttribute ReporteAsistenciaFiltro filtro) {
        try {
            reporteAsistenciaService.validarFiltros(filtro);
            byte[] csvBytes = reporteAsistenciaService.generarCsv(filtro);
            String filename = reporteAsistenciaService.generarNombreArchivoCsv(filtro);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(csvBytes.length);

            if (auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.REPORTE_CSV,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.REPORTES,
                        "REPORTE",
                        null,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Exportación de reporte CSV: " + filename,
                        "Tipo: " + filtro.getTipoReporte()
                );
            }

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
        } catch (ValidationException | BusinessException e) {
            log.warn("Filtro no válido para generación CSV: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al generar CSV de reporte: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Ruta específica para consultar el historial de asistencia de un estudiante.
     */
    @GetMapping("/estudiante/{id}")
    public String verReporteEstudiante(@PathVariable("id") Long estudianteId,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       Model model) {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.POR_ESTUDIANTE);
        filtro.setEstudianteId(estudianteId);
        return verReporte(filtro, page, size, model);
    }

    /**
     * Ruta específica para descargar el PDF de historial de un estudiante.
     */
    @GetMapping("/estudiante/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdfEstudiante(@PathVariable("id") Long estudianteId) {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.POR_ESTUDIANTE);
        filtro.setEstudianteId(estudianteId);
        return descargarPdf(filtro);
    }

    /**
     * Ruta específica para descargar el Excel (.xlsx) de historial de un estudiante.
     */
    @GetMapping("/estudiante/{id}/excel")
    public ResponseEntity<byte[]> descargarExcelEstudiante(@PathVariable("id") Long estudianteId) {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.POR_ESTUDIANTE);
        filtro.setEstudianteId(estudianteId);
        return descargarExcel(filtro);
    }

    /**
     * Ruta específica para descargar el CSV de historial de un estudiante.
     */
    @GetMapping("/estudiante/{id}/csv")
    public ResponseEntity<byte[]> descargarCsvEstudiante(@PathVariable("id") Long estudianteId) {
        ReporteAsistenciaFiltro filtro = new ReporteAsistenciaFiltro();
        filtro.setTipoReporte(TipoReporteAsistencia.POR_ESTUDIANTE);
        filtro.setEstudianteId(estudianteId);
        return descargarCsv(filtro);
    }

    /**
     * Endpoint auxiliar AJAX para obtener secciones activas según el grado seleccionado.
     */
    @GetMapping("/secciones-por-grado")
    @ResponseBody
    public List<Map<String, Object>> obtenerSeccionesPorGrado(@RequestParam("gradoId") Long gradoId) {
        List<Seccion> secciones = seccionRepository.findByGradoIdAndActivoTrue(gradoId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Seccion s : secciones) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("nombre", s.getNombre());
            result.add(map);
        }
        return result;
    }

    /**
     * Endpoint auxiliar AJAX para obtener estudiantes matriculados en una sección.
     */
    @GetMapping("/estudiantes-por-seccion")
    @ResponseBody
    public List<Map<String, Object>> obtenerEstudiantesPorSeccion(@RequestParam("seccionId") Long seccionId,
                                                                 @RequestParam(value = "anioEscolarId", required = false) Long anioEscolarId) {
        Long anioId = anioEscolarId;
        if (anioId == null) {
            anioId = anioEscolarRepository.findByActivoTrue().map(AnioEscolar::getId).orElse(null);
        }
        if (anioId == null) {
            return Collections.emptyList();
        }

        List<Matricula> matriculas = matriculaRepository.buscarMatriculas("", anioId, null, seccionId, null, Pageable.unpaged()).getContent();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Matricula m : matriculas) {
            Persona e = m.getEstudiante();
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("nombreCompleto", e.getApellidoPaterno() + " " + e.getApellidoMaterno() + ", " + e.getNombres());
            map.put("numeroDocumento", e.getNumeroDocumento());
            result.add(map);
        }
        return result;
    }

    private void cargarDatosFormulario(ReporteAsistenciaFiltro filtro, Model model) {
        model.addAttribute("tiposReporte", TipoReporteAsistencia.values());
        model.addAttribute("estados", EstadoAsistencia.values());
        model.addAttribute("aniosEscolares", anioEscolarRepository.findAll());
        model.addAttribute("grados", gradoRepository.findAll());

        if (filtro.getGradoId() != null) {
            model.addAttribute("secciones", seccionRepository.findByGradoIdAndActivoTrue(filtro.getGradoId()));
        } else {
            model.addAttribute("secciones", seccionRepository.findAll());
        }

        // Si hay estudiante seleccionado, cargar sus datos para el select
        if (filtro.getEstudianteId() != null) {
            personaRepository.findById(filtro.getEstudianteId()).ifPresent(est -> {
                model.addAttribute("estudianteSeleccionado", est);
            });
        }
    }
}
