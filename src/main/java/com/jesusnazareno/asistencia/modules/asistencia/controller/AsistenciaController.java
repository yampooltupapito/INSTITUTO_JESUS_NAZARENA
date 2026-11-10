package com.jesusnazareno.asistencia.modules.asistencia.controller;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.service.AnioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.GradoService;
import com.jesusnazareno.asistencia.modules.academico.service.SeccionService;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.RegistroAsistenciaForm;
import com.jesusnazareno.asistencia.modules.asistencia.service.AsistenciaService;
import com.jesusnazareno.asistencia.modules.asistencia.service.JustificacionService;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final JustificacionService justificacionService;
    private final AnioEscolarService anioEscolarService;
    private final GradoService gradoService;
    private final SeccionService seccionService;
    private final UsuarioRepository usuarioRepository;
    private final MessageSource messageSource;
    private final com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService horarioAsistenciaService;
    private final com.jesusnazareno.asistencia.modules.academico.mapper.HorarioAsistenciaMapper horarioAsistenciaMapper;

    public AsistenciaController(AsistenciaService asistenciaService,
                                JustificacionService justificacionService,
                                AnioEscolarService anioEscolarService,
                                GradoService gradoService,
                                SeccionService seccionService,
                                UsuarioRepository usuarioRepository,
                                MessageSource messageSource,
                                com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService horarioAsistenciaService,
                                com.jesusnazareno.asistencia.modules.academico.mapper.HorarioAsistenciaMapper horarioAsistenciaMapper) {
        this.asistenciaService = asistenciaService;
        this.justificacionService = justificacionService;
        this.anioEscolarService = anioEscolarService;
        this.gradoService = gradoService;
        this.seccionService = seccionService;
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
        this.horarioAsistenciaService = horarioAsistenciaService;
        this.horarioAsistenciaMapper = horarioAsistenciaMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ASISTENCIA_VER')")
    public String listar(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId,
            @RequestParam(name = "gradoId", required = false) Long gradoId,
            @RequestParam(name = "seccionId", required = false) Long seccionId,
            @RequestParam(name = "estado", required = false) EstadoAsistencia estado,
            @RequestParam(name = "fechaInicio", required = false) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) LocalDate fechaFin,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(pagina, tamano,
                Sort.by(Sort.Direction.DESC, "fecha"));
        Page<AsistenciaResponse> asistencias = asistenciaService.buscarAsistencias(
                q, anioEscolarId, gradoId, seccionId, estado, fechaInicio, fechaFin, pageable);
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("anioEscolarId", anioEscolarId);
        model.addAttribute("gradoId", gradoId);
        model.addAttribute("seccionId", seccionId);
        model.addAttribute("estado", estado);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("page", pagina);
        model.addAttribute("size", tamano);
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("grados", gradoService.listarTodos());
        model.addAttribute("estados", EstadoAsistencia.values());
        if (gradoId != null) {
            model.addAttribute("secciones", seccionService.listarPorGrado(gradoId));
        } else {
            model.addAttribute("secciones", Collections.emptyList());
        }
        model.addAttribute("currentMenu", "asistencias");
        model.addAttribute("pageTitle", "Consulta de Asistencias");
        return "asistencias/list";
    }

    @GetMapping("/registrar")
    @PreAuthorize("hasAuthority('ASISTENCIA_REGISTRAR')")
    public String mostrarFormularioRegistro(
            @RequestParam(name = "anioEscolarId", required = false) Long anioEscolarId,
            @RequestParam(name = "gradoId", required = false) Long gradoId,
            @RequestParam(name = "seccionId", required = false) Long seccionId,
            @RequestParam(name = "fecha", required = false) LocalDate fecha,
            Model model) {
        LocalDate fechaDefault = fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Lima"));
        RegistroAsistenciaForm form = new RegistroAsistenciaForm();
        form.setFecha(fechaDefault);
        form.setAnioEscolarId(anioEscolarId);
        form.setGradoId(gradoId);
        form.setSeccionId(seccionId);
        if (seccionId != null && anioEscolarId != null) {
            cargarGrillaEstudiantes(form, seccionId, anioEscolarId, fechaDefault);
            var seccionResp = seccionService.buscarPorId(seccionId);
            if (seccionResp != null) {
                var horarioOpt = horarioAsistenciaService.resolverHorario(
                        fechaDefault, anioEscolarId, seccionResp.getGradoId(), seccionId, seccionResp.getTurno()
                );
                horarioOpt.ifPresent(h -> model.addAttribute("horarioAplicable", horarioAsistenciaMapper.toResponse(h)));
            }
        }
        cargarCatalogos(model, gradoId);
        model.addAttribute("form", form);
        model.addAttribute("currentMenu", "asistencias");
        model.addAttribute("pageTitle", "Registrar Asistencia");
        return "asistencias/register";
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasAuthority('ASISTENCIA_REGISTRAR')")
    public String guardarRegistroMasivo(
            @RequestParam(name = "fecha") LocalDate fecha,
            @RequestParam(name = "anioEscolarId") Long anioEscolarId,
            @RequestParam(name = "gradoId") Long gradoId,
            @RequestParam(name = "seccionId") Long seccionId,
            @RequestParam Map<String, String> allParams,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Usuario usuarioAutenticado = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario autenticado no encontrado."));
        var seccion = seccionService.buscarPorId(seccionId);
        if (!seccion.getGradoId().equals(gradoId)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("asistencia.seccion.invalida", null,
                            "La seccion no corresponde al grado.", LocaleContextHolder.getLocale()));
            return "redirect:/asistencias/registrar?anioEscolarId=" + anioEscolarId
                    + "&gradoId=" + gradoId + "&seccionId=" + seccionId + "&fecha=" + fecha;
        }
        List<Matricula> matriculas = asistenciaService.listarMatriculasValidasParaRegistro(seccionId, anioEscolarId);
        int registradas = 0;
        int omitidas = 0;
        for (Matricula m : matriculas) {
            String estadoStr = allParams.get("estado_" + m.getId());
            if (estadoStr == null || estadoStr.isBlank()) { omitidas++; continue; }
            EstadoAsistencia estadoSolicitado;
            try {
                estadoSolicitado = EstadoAsistencia.valueOf(estadoStr);
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("asistencia.estado.invalido", null, "Estado invalido.", LocaleContextHolder.getLocale()));
                return "redirect:/asistencias/registrar?anioEscolarId=" + anioEscolarId + "&gradoId=" + gradoId + "&seccionId=" + seccionId + "&fecha=" + fecha;
            }
            if (estadoSolicitado == EstadoAsistencia.JUSTIFICADO) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("asistencia.estado.invalido", null,
                                "No se puede asignar JUSTIFICADO manualmente.", LocaleContextHolder.getLocale()));
                return "redirect:/asistencias/registrar?anioEscolarId=" + anioEscolarId + "&gradoId=" + gradoId + "&seccionId=" + seccionId + "&fecha=" + fecha;
            }
            Optional<AsistenciaResponse> existente = asistenciaService.buscarAsistenciaExistente(m.getId(), fecha);
            if (existente.isPresent()) { omitidas++; continue; }
            String horaStr = allParams.get("hora_" + m.getId());
            LocalTime horaIngreso = null;
            if (horaStr != null && !horaStr.isBlank()) {
                try { horaIngreso = LocalTime.parse(horaStr); } catch (Exception ignored) {}
            }
            if (estadoSolicitado == EstadoAsistencia.AUSENTE) { horaIngreso = null; }
            String observacion = allParams.get("obs_" + m.getId());
            AsistenciaRequest req = AsistenciaRequest.builder()
                    .matriculaId(m.getId()).fecha(fecha).estado(estadoSolicitado)
                    .horaIngreso(horaIngreso)
                    .observacion(observacion != null ? observacion.strip() : null)
                    .registradoPor(usuarioAutenticado.getId()).build();
            try { asistenciaService.registrar(req); registradas++; }
            catch (BusinessException ex) { omitidas++; }
        }
        if (registradas > 0) {
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("asistencia.lote.registrado", null,
                            "Asistencia registrada correctamente.", LocaleContextHolder.getLocale())
                            + " (" + registradas + " guardados, " + omitidas + " omitidos)");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage",
                    "No se registraron nuevos registros. " + omitidas + " omitidos (ya registrados o justificados).");
        }
        return "redirect:/asistencias?anioEscolarId=" + anioEscolarId + "&gradoId=" + gradoId + "&seccionId=" + seccionId;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ASISTENCIA_VER')")
    public String detalle(@PathVariable("id") Long id, Model model) {
        AsistenciaResponse asistencia = asistenciaService.buscarPorId(id);
        model.addAttribute("asistencia", asistencia);
        justificacionService.buscarActivaPorAsistencia(id)
                .ifPresent(j -> model.addAttribute("justificacion", j));
        model.addAttribute("currentMenu", "asistencias");
        model.addAttribute("pageTitle", "Detalle de Asistencia");
        return "asistencias/detail";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAuthority('ASISTENCIA_EDITAR')")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
        AsistenciaResponse asistencia = asistenciaService.buscarPorId(id);
        if (asistencia.getEstado() == EstadoAsistencia.JUSTIFICADO) {
            return "redirect:/asistencias/" + id;
        }
        model.addAttribute("asistencia", asistencia);
        model.addAttribute("estadosEditables", new EstadoAsistencia[]{
                EstadoAsistencia.PRESENTE, EstadoAsistencia.TARDANZA, EstadoAsistencia.AUSENTE});
        model.addAttribute("currentMenu", "asistencias");
        model.addAttribute("pageTitle", "Editar Asistencia");
        return "asistencias/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ASISTENCIA_EDITAR')")
    public String actualizarAsistencia(
            @PathVariable("id") Long id,
            @RequestParam("estado") String estadoStr,
            @RequestParam(name = "horaIngreso", required = false) String horaIngresoStr,
            @RequestParam(name = "horaSalida", required = false) String horaSalidaStr,
            @RequestParam(name = "observacion", required = false) String observacion,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        AsistenciaResponse existente = asistenciaService.buscarPorId(id);
        if (existente.getEstado() == EstadoAsistencia.JUSTIFICADO) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("asistencia.justificada.no-editable", null,
                            "Una asistencia justificada no puede modificarse.", LocaleContextHolder.getLocale()));
            return "redirect:/asistencias/" + id;
        }
        EstadoAsistencia estadoNuevo;
        try { estadoNuevo = EstadoAsistencia.valueOf(estadoStr); }
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("asistencia.estado.invalido", null, "Estado invalido.", LocaleContextHolder.getLocale()));
            return "redirect:/asistencias/" + id + "/editar";
        }
        if (estadoNuevo == EstadoAsistencia.JUSTIFICADO) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("asistencia.estado.invalido", null,
                            "No se puede asignar JUSTIFICADO manualmente.", LocaleContextHolder.getLocale()));
            return "redirect:/asistencias/" + id + "/editar";
        }
        LocalTime horaIngreso = parseTime(horaIngresoStr);
        LocalTime horaSalida = parseTime(horaSalidaStr);
        if (estadoNuevo == EstadoAsistencia.AUSENTE) { horaIngreso = null; horaSalida = null; }
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario autenticado no encontrado."));
        AsistenciaRequest req = AsistenciaRequest.builder()
                .matriculaId(existente.getMatriculaId()).fecha(existente.getFecha())
                .estado(estadoNuevo).horaIngreso(horaIngreso).horaSalida(horaSalida)
                .observacion(observacion != null ? observacion.strip() : null)
                .registradoPor(usuario.getId()).build();
        try {
            asistenciaService.actualizar(id, req);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("asistencia.actualizada", null, "Asistencia actualizada.", LocaleContextHolder.getLocale()));
        } catch (BusinessException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/asistencias/" + id + "/editar";
        }
        return "redirect:/asistencias/" + id;
    }

    @GetMapping("/secciones")
    @PreAuthorize("hasAuthority('ASISTENCIA_VER') or hasAuthority('ASISTENCIA_REGISTRAR')")
    @ResponseBody
    public List<?> obtenerSeccionesPorGrado(@RequestParam("gradoId") Long gradoId) {
        return seccionService.listarPorGrado(gradoId);
    }

    @GetMapping("/estudiantes")
    @PreAuthorize("hasAuthority('ASISTENCIA_REGISTRAR')")
    @ResponseBody
    public List<Map<String, Object>> obtenerEstudiantesParaRegistro(
            @RequestParam("seccionId") Long seccionId,
            @RequestParam("anioEscolarId") Long anioEscolarId,
            @RequestParam(name = "fecha", required = false) LocalDate fecha) {
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Lima"));
        List<Matricula> matriculas = asistenciaService.listarMatriculasValidasParaRegistro(seccionId, anioEscolarId);
        return matriculas.stream().map(m -> {
            Persona p = m.getEstudiante();
            String nombre = (p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "") + " "
                    + (p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "") + ", "
                    + (p.getNombres() != null ? p.getNombres() : "");
            Optional<AsistenciaResponse> existente = asistenciaService.buscarAsistenciaExistente(m.getId(), fechaConsulta);
            return Map.<String, Object>of(
                    "matriculaId", m.getId(),
                    "nombre", nombre.strip(),
                    "documento", p.getNumeroDocumento() != null ? p.getNumeroDocumento() : "",
                    "registrado", existente.isPresent(),
                    "estadoPrevio", existente.map(a -> a.getEstado().name()).orElse(""));
        }).toList();
    }

    private void cargarGrillaEstudiantes(RegistroAsistenciaForm form, Long seccionId, Long anioEscolarId, LocalDate fecha) {
        List<Matricula> matriculas = asistenciaService.listarMatriculasValidasParaRegistro(seccionId, anioEscolarId);
        List<RegistroAsistenciaForm.RegistroItem> items = matriculas.stream().map(m -> {
            RegistroAsistenciaForm.RegistroItem item = new RegistroAsistenciaForm.RegistroItem();
            item.setMatriculaId(m.getId());
            Persona p = m.getEstudiante();
            String nombre = (p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "") + " "
                    + (p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "") + ", "
                    + (p.getNombres() != null ? p.getNombres() : "");
            item.setEstudianteNombre(nombre.strip());
            item.setEstudianteDocumento(p.getNumeroDocumento() != null ? p.getNumeroDocumento() : "");
            item.setEstado(EstadoAsistencia.PRESENTE);
            Optional<AsistenciaResponse> existente = asistenciaService.buscarAsistenciaExistente(m.getId(), fecha);
            if (existente.isPresent()) {
                item.setRegistradoPreviamente(true);
                item.setEstadoPrevio(existente.get().getEstado());
                item.setIdRegistroPrevio(existente.get().getId());
                item.setEstado(existente.get().getEstado());
            }
            return item;
        }).toList();
        form.setRegistros(items);
    }

    private void cargarCatalogos(Model model, Long gradoId) {
        model.addAttribute("aniosEscolares", anioEscolarService.listarTodos());
        model.addAttribute("grados", gradoService.listarTodos());
        if (gradoId != null) {
            model.addAttribute("secciones", seccionService.listarPorGrado(gradoId));
        } else {
            model.addAttribute("secciones", Collections.emptyList());
        }
        model.addAttribute("estadosDisponibles", new EstadoAsistencia[]{
                EstadoAsistencia.PRESENTE, EstadoAsistencia.TARDANZA, EstadoAsistencia.AUSENTE});
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;
        try { return LocalTime.parse(timeStr); } catch (Exception e) { return null; }
    }
}
