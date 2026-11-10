package com.jesusnazareno.asistencia.modules.importacion.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstadoFilaImportacion;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstudianteImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.ImportacionResultadoDTO;
import com.jesusnazareno.asistencia.modules.importacion.dto.MatriculaImportRow;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.matricula.service.MatriculaService;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio transaccional encargado de la persistencia final de importaciones previamente validadas.
 * Revalida condiciones críticas contra la base de datos (concurrencia), delega la creación
 * a los servicios existentes (EstudianteService, MatriculaService) y garantiza comportamiento
 * atómico "todo o nada" con rollback ante cualquier error.
 */
@Service
public class ImportacionEjecucionServiceImpl implements ImportacionEjecucionService {

    private static final Logger log = LoggerFactory.getLogger(ImportacionEjecucionService.class);

    private final ImportacionExcelService importacionExcelService;
    private final EstudianteService estudianteService;
    private final MatriculaService matriculaService;
    private final PersonaRepository personaRepository;
    private final MatriculaRepository matriculaRepository;
    private final com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService;

    public ImportacionEjecucionServiceImpl(ImportacionExcelService importacionExcelService,
                                       EstudianteService estudianteService,
                                       MatriculaService matriculaService,
                                       PersonaRepository personaRepository,
                                       MatriculaRepository matriculaRepository) {
        this.importacionExcelService = importacionExcelService;
        this.estudianteService = estudianteService;
        this.matriculaService = matriculaService;
        this.personaRepository = personaRepository;
        this.matriculaRepository = matriculaRepository;
        this.auditoriaService = null;
    }

    @org.springframework.beans.factory.annotation.Autowired
    public ImportacionEjecucionServiceImpl(ImportacionExcelService importacionExcelService,
                                           EstudianteService estudianteService,
                                           MatriculaService matriculaService,
                                           PersonaRepository personaRepository,
                                           MatriculaRepository matriculaRepository,
                                           com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService) {
        this.importacionExcelService = importacionExcelService;
        this.estudianteService = estudianteService;
        this.matriculaService = matriculaService;
        this.personaRepository = personaRepository;
        this.matriculaRepository = matriculaRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Confirma e inserta los estudiantes validados.
     *
     * @param token Token temporal de importación validada
     * @param username Nombre del usuario autenticado que autoriza la confirmación
     * @return Cantidad de nuevos estudiantes registrados
     */
    @Transactional
    public int confirmarImportacionEstudiantes(String token, String username) {
        ImportacionResultadoDTO<EstudianteImportRow> resultado = importacionExcelService.obtenerResultadoTemporal(token, username);

        if (resultado.getFilasConError() > 0) {
            throw new BusinessException("No se puede confirmar la importación porque el archivo contiene errores. Corrija el archivo y vuelva a subirlo.");
        }

        int nuevosRegistrados = 0;

        for (EstudianteImportRow fila : resultado.getFilas()) {
            if (fila.getEstado() == EstadoFilaImportacion.VALIDA) {
                // Revalidación de concurrencia contra BD
                if (personaRepository.existsByNumeroDocumento(fila.getNumeroDocumento())) {
                    log.warn("Fila {}: la persona con documento {} ya fue creada concurrentemente. Omitiendo duplicado.",
                            fila.getNumeroFila(), fila.getNumeroDocumento());
                    continue;
                }

                PersonaRequest request = PersonaRequest.builder()
                        .tipoDocumento(fila.getTipoDocumento())
                        .numeroDocumento(fila.getNumeroDocumento())
                        .nombres(fila.getNombres())
                        .apellidoPaterno(fila.getApellidoPaterno())
                        .apellidoMaterno(fila.getApellidoMaterno())
                        .fechaNacimiento(fila.getFechaNacimiento())
                        .sexo(fila.getSexo())
                        .telefono(fila.getTelefono())
                        .correo(fila.getCorreo())
                        .direccion(fila.getDireccion())
                        .build();

                estudianteService.registrarEstudiante(request);
                nuevosRegistrados++;
            }
        }

        // Limpiar resultado temporal al concluir exitosamente
        importacionExcelService.limpiarResultadoTemporal(token);
        log.info("Importación de estudiantes completada exitosamente. Total registrados: {}", nuevosRegistrados);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.IMPORTACION_ESTUDIANTES,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.IMPORTACIONES,
                    "ESTUDIANTE",
                    null,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se confirmó importación masiva de estudiantes: " + nuevosRegistrados + " registrados.",
                    "Total procesados: " + nuevosRegistrados
            );
        }

        return nuevosRegistrados;
    }

    /**
     * Confirma e inserta las matrículas validadas.
     *
     * @param token Token temporal de importación validada
     * @param username Nombre del usuario autenticado que autoriza la confirmación
     * @return Cantidad de matrículas registradas
     */
    @Transactional
    public int confirmarImportacionMatriculas(String token, String username) {
        ImportacionResultadoDTO<MatriculaImportRow> resultado = importacionExcelService.obtenerResultadoTemporal(token, username);

        if (resultado.getFilasConError() > 0) {
            throw new BusinessException("No se puede confirmar la importación porque el archivo contiene errores. Corrija el archivo y vuelva a subirlo.");
        }

        int matriculasCreadas = 0;

        for (MatriculaImportRow fila : resultado.getFilas()) {
            if (fila.getEstadoFila() == EstadoFilaImportacion.VALIDA) {
                // Revalidación concurrente de código y duplicidad
                if (matriculaRepository.existsByCodigoMatricula(fila.getCodigoMatricula())) {
                    throw new BusinessException("Fila " + fila.getNumeroFila() + ": El código de matrícula '"
                            + fila.getCodigoMatricula() + "' ya existe en la base de datos.");
                }

                if (matriculaRepository.existsByEstudianteIdAndAnioEscolarId(fila.getPersonaEstudianteId(), fila.getAnioEscolarId())) {
                    throw new BusinessException("Fila " + fila.getNumeroFila() + ": El estudiante con documento "
                            + fila.getNumeroDocumentoEstudiante() + " ya cuenta con matrícula en el año seleccionado.");
                }

                MatriculaRequest request = MatriculaRequest.builder()
                        .personaEstudianteId(fila.getPersonaEstudianteId())
                        .anioEscolarId(fila.getAnioEscolarId())
                        .gradoId(fila.getGradoId())
                        .seccionId(fila.getSeccionId())
                        .codigoMatricula(fila.getCodigoMatricula())
                        .fechaMatricula(fila.getFechaMatricula())
                        .estado(fila.getEstado() != null ? fila.getEstado() : EstadoMatricula.MATRICULADO)
                        .observaciones(fila.getObservaciones())
                        .build();

                matriculaService.crearMatricula(request);
                matriculasCreadas++;
            }
        }

        importacionExcelService.limpiarResultadoTemporal(token);
        log.info("Importación de matrículas completada exitosamente. Total registradas: {}", matriculasCreadas);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.IMPORTACION_MATRICULAS,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.IMPORTACIONES,
                    "MATRICULA",
                    null,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se confirmó importación masiva de matrículas: " + matriculasCreadas + " registradas.",
                    "Total procesadas: " + matriculasCreadas
            );
        }

        return matriculasCreadas;
    }
}
