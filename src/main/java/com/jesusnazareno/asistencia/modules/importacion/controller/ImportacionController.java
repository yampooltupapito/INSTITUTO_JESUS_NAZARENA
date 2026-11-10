package com.jesusnazareno.asistencia.modules.importacion.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstudianteImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.ImportacionResultadoDTO;
import com.jesusnazareno.asistencia.modules.importacion.dto.MatriculaImportRow;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionEjecucionService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionExcelService;
import com.jesusnazareno.asistencia.modules.importacion.service.ImportacionPlantillaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Thymeleaf para el módulo de Importación Masiva desde Excel.
 * Maneja descarga de plantillas, subida de archivos XLSX, validación previa en memoria y confirmación segura.
 */
@Controller
@RequestMapping("/importaciones")
public class ImportacionController {

    private static final Logger log = LoggerFactory.getLogger(ImportacionController.class);

    private final ImportacionPlantillaService plantillaService;
    private final ImportacionExcelService excelService;
    private final ImportacionEjecucionService ejecucionService;

    public ImportacionController(ImportacionPlantillaService plantillaService,
                                 ImportacionExcelService excelService,
                                 ImportacionEjecucionService ejecucionService) {
        this.plantillaService = plantillaService;
        this.excelService = excelService;
        this.ejecucionService = ejecucionService;
    }

    /**
     * Vista principal del módulo de importaciones.
     */
    @GetMapping({"", "/"})
    @PreAuthorize("hasAuthority('IMPORTACION_ESTUDIANTE') or hasAuthority('IMPORTACION_MATRICULA')")
    public String index(Model model) {
        model.addAttribute("currentMenu", "importaciones");
        return "importaciones/index";
    }

    // =========================================================================
    // IMPORTACIÓN DE ESTUDIANTES
    // =========================================================================

    @GetMapping("/estudiantes")
    @PreAuthorize("hasAuthority('IMPORTACION_ESTUDIANTE')")
    public String verFormularioEstudiantes(Model model) {
        model.addAttribute("currentMenu", "importaciones");
        return "importaciones/estudiantes";
    }

    @GetMapping("/estudiantes/plantilla")
    @PreAuthorize("hasAuthority('IMPORTACION_ESTUDIANTE')")
    public ResponseEntity<byte[]> descargarPlantillaEstudiantes() {
        byte[] bytes = plantillaService.generarPlantillaEstudiantes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("plantilla_estudiantes.xlsx").build());
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/estudiantes/validar")
    @PreAuthorize("hasAuthority('IMPORTACION_ESTUDIANTE')")
    public String validarEstudiantes(@RequestParam("archivo") MultipartFile archivo,
                                     Authentication auth,
                                     Model model) {
        model.addAttribute("currentMenu", "importaciones");
        try {
            ImportacionResultadoDTO<EstudianteImportRow> resultado = excelService.leerYValidarEstudiantes(archivo, auth.getName());
            model.addAttribute("resultado", resultado);
            model.addAttribute("tipoImportacion", "ESTUDIANTES");
            return "importaciones/preview";
        } catch (ValidationException | BusinessException e) {
            log.warn("Error de validación al cargar estudiantes: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "importaciones/estudiantes";
        } catch (Exception e) {
            log.error("Error inesperado al validar archivo de estudiantes: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al procesar el archivo: " + e.getMessage());
            return "importaciones/estudiantes";
        }
    }

    @PostMapping("/estudiantes/confirmar")
    @PreAuthorize("hasAuthority('IMPORTACION_ESTUDIANTE')")
    public String confirmarEstudiantes(@RequestParam("token") String token,
                                       Authentication auth,
                                       RedirectAttributes redirectAttributes) {
        try {
            int registrados = ejecucionService.confirmarImportacionEstudiantes(token, auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Se importaron exitosamente " + registrados + " estudiantes.");
            return "redirect:/estudiantes";
        } catch (ValidationException | BusinessException e) {
            log.warn("Error al confirmar importación de estudiantes: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/importaciones/estudiantes";
        } catch (Exception e) {
            log.error("Error crítico en confirmación de estudiantes: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error al persistir los estudiantes: " + e.getMessage());
            return "redirect:/importaciones/estudiantes";
        }
    }

    // =========================================================================
    // IMPORTACIÓN DE MATRÍCULAS
    // =========================================================================

    @GetMapping("/matriculas")
    @PreAuthorize("hasAuthority('IMPORTACION_MATRICULA')")
    public String verFormularioMatriculas(Model model) {
        model.addAttribute("currentMenu", "importaciones");
        return "importaciones/matriculas";
    }

    @GetMapping("/matriculas/plantilla")
    @PreAuthorize("hasAuthority('IMPORTACION_MATRICULA')")
    public ResponseEntity<byte[]> descargarPlantillaMatriculas() {
        byte[] bytes = plantillaService.generarPlantillaMatriculas();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("plantilla_matriculas.xlsx").build());
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/matriculas/validar")
    @PreAuthorize("hasAuthority('IMPORTACION_MATRICULA')")
    public String validarMatriculas(@RequestParam("archivo") MultipartFile archivo,
                                    Authentication auth,
                                    Model model) {
        model.addAttribute("currentMenu", "importaciones");
        try {
            ImportacionResultadoDTO<MatriculaImportRow> resultado = excelService.leerYValidarMatriculas(archivo, auth.getName());
            model.addAttribute("resultado", resultado);
            model.addAttribute("tipoImportacion", "MATRICULAS");
            return "importaciones/preview";
        } catch (ValidationException | BusinessException e) {
            log.warn("Error de validación al cargar matrículas: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "importaciones/matriculas";
        } catch (Exception e) {
            log.error("Error inesperado al validar archivo de matrículas: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al procesar el archivo: " + e.getMessage());
            return "importaciones/matriculas";
        }
    }

    @PostMapping("/matriculas/confirmar")
    @PreAuthorize("hasAuthority('IMPORTACION_MATRICULA')")
    public String confirmarMatriculas(@RequestParam("token") String token,
                                      Authentication auth,
                                      RedirectAttributes redirectAttributes) {
        try {
            int registradas = ejecucionService.confirmarImportacionMatriculas(token, auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Se importaron exitosamente " + registradas + " matrículas.");
            return "redirect:/matriculas";
        } catch (ValidationException | BusinessException e) {
            log.warn("Error al confirmar importación de matrículas: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/importaciones/matriculas";
        } catch (Exception e) {
            log.error("Error crítico en confirmación de matrículas: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error al persistir las matrículas: " + e.getMessage());
            return "redirect:/importaciones/matriculas";
        }
    }
}
