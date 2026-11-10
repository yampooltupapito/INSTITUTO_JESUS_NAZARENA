package com.jesusnazareno.asistencia.modules.importacion.service;

import com.jesusnazareno.asistencia.common.validation.DniPeruanoValidator;
import com.jesusnazareno.asistencia.common.validation.TelefonoPeruanoValidator;
import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstadoFilaImportacion;
import com.jesusnazareno.asistencia.modules.importacion.dto.EstudianteImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.ImportacionResultadoDTO;
import com.jesusnazareno.asistencia.modules.importacion.dto.MatriculaImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.TipoImportacion;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Servicio encargado de la lectura, análisis y validación en memoria de archivos Excel XLSX para importaciones.
 * Implementa control riguroso de límites, neutralización de zip bombs, detección de duplicados
 * tanto internos como en base de datos, y almacén temporal seguro con expiración.
 */
@Service
public class ImportacionExcelServiceImpl implements ImportacionExcelService {

    private static final Logger log = LoggerFactory.getLogger(ImportacionExcelService.class);
    private static final Pattern PATTERN_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PATTERN_SOLO_DIGITOS = Pattern.compile("^[0-9]+$");
    private static final DateTimeFormatter FORMATO_FECHA_PERU = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Value("${importacion.excel.max-filas:1000}")
    private int maxFilas = 1000;

    @Value("${importacion.excel.max-archivo-bytes:5242880}")
    private long maxArchivoBytes = 5242880; // 5 MB

    @Value("${importacion.excel.token-expiracion-minutos:30}")
    private int tokenExpiracionMinutos = 30;

    private final PersonaRepository personaRepository;
    private final PersonaActorRepository personaActorRepository;
    private final AnioEscolarRepository anioEscolarRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;
    private final MatriculaRepository matriculaRepository;

    // Almacén temporal en memoria indexado por token UUID (sin persistencia en BD ni sesión HTTP)
    private final Map<String, CacheEntry> cacheResultados = new ConcurrentHashMap<>();

    public ImportacionExcelServiceImpl(PersonaRepository personaRepository,
                                   PersonaActorRepository personaActorRepository,
                                   AnioEscolarRepository anioEscolarRepository,
                                   GradoRepository gradoRepository,
                                   SeccionRepository seccionRepository,
                                   MatriculaRepository matriculaRepository) {
        this.personaRepository = personaRepository;
        this.personaActorRepository = personaActorRepository;
        this.anioEscolarRepository = anioEscolarRepository;
        this.gradoRepository = gradoRepository;
        this.seccionRepository = seccionRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public void setMaxFilas(int maxFilas) {
        this.maxFilas = maxFilas;
    }

    public void setMaxArchivoBytes(long maxArchivoBytes) {
        this.maxArchivoBytes = maxArchivoBytes;
    }

    // =========================================================================
    // 1. VALIDACIÓN DE ESTUDIANTES
    // =========================================================================

    public ImportacionResultadoDTO<EstudianteImportRow> leerYValidarEstudiantes(MultipartFile file, String username) {
        limpiarCacheExpirada();
        validarArchivoXlsx(file);

        String token = UUID.randomUUID().toString();
        String nombreOriginal = file.getOriginalFilename() != null ? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_") : "archivo.xlsx";
        ImportacionResultadoDTO<EstudianteImportRow> resultado = new ImportacionResultadoDTO<>(token, nombreOriginal, TipoImportacion.ESTUDIANTES, username);

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null || sheet.getLastRowNum() < 1) {
                resultado.agregarErrorGeneral("El archivo Excel no contiene filas de datos.");
                return resultado;
            }

            // Validar límite de filas
            int numFilasDatos = contarFilasNoVacias(sheet);
            if (numFilasDatos > maxFilas) {
                throw new BusinessException("El archivo contiene " + numFilasDatos + " filas, superando el límite máximo permitido de " + maxFilas + " filas.");
            }

            // Mapeo y validación de encabezados
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colMap = mapearEncabezados(headerRow);
            validarEncabezadosEstudiantes(colMap);

            DataFormatter formatter = new DataFormatter(Locale.getDefault());
            Set<String> documentosEnArchivo = new HashSet<>();
            List<EstudianteImportRow> listaFilas = new ArrayList<>();

            int validas = 0;
            int errores = 0;
            int existentes = 0;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (esFilaVacia(row)) {
                    continue;
                }

                EstudianteImportRow filaDto = new EstudianteImportRow(r + 1);

                String tipoDocRaw = obtenerValorCelda(row, colMap.get("tipo_documento"), formatter);
                String numDocRaw = obtenerValorCelda(row, colMap.get("numero_documento"), formatter);
                String nombresRaw = obtenerValorCelda(row, colMap.get("nombres"), formatter);
                String apePatRaw = obtenerValorCelda(row, colMap.get("apellido_paterno"), formatter);
                String apeMatRaw = obtenerValorCelda(row, colMap.get("apellido_materno"), formatter);
                String fechaNacRaw = obtenerValorFecha(row, colMap.get("fecha_nacimiento"), formatter);
                String sexoRaw = obtenerValorCelda(row, colMap.get("sexo"), formatter);
                String telefonoRaw = obtenerValorCelda(row, colMap.get("telefono"), formatter);
                String correoRaw = obtenerValorCelda(row, colMap.get("correo"), formatter);
                String direccionRaw = obtenerValorCelda(row, colMap.get("direccion"), formatter);

                filaDto.setTipoDocumentoRaw(tipoDocRaw);
                filaDto.setNumeroDocumento(numDocRaw);
                filaDto.setNombres(nombresRaw);
                filaDto.setApellidoPaterno(apePatRaw);
                filaDto.setApellidoMaterno(apeMatRaw);
                filaDto.setFechaNacimientoRaw(fechaNacRaw);
                filaDto.setSexoRaw(sexoRaw);
                filaDto.setTelefono(telefonoRaw);
                filaDto.setCorreo(correoRaw);
                filaDto.setDireccion(direccionRaw);

                // 1. Tipo de Documento
                if (tipoDocRaw == null || tipoDocRaw.isBlank()) {
                    filaDto.agregarError("El tipo de documento es obligatorio.");
                } else {
                    try {
                        filaDto.setTipoDocumento(TipoDocumento.valueOf(tipoDocRaw.trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        filaDto.agregarError("Tipo de documento inválido ('" + tipoDocRaw + "'). Debe ser DNI, CE o PASAPORTE.");
                    }
                }

                // 2. Número de Documento
                if (numDocRaw == null || numDocRaw.isBlank()) {
                    filaDto.agregarError("El número de documento es obligatorio.");
                } else {
                    String docLimpio = numDocRaw.trim();
                    filaDto.setNumeroDocumento(docLimpio);

                    if (filaDto.getTipoDocumento() == TipoDocumento.DNI) {
                        if (!PATTERN_SOLO_DIGITOS.matcher(docLimpio).matches() || docLimpio.length() != 8) {
                            filaDto.agregarError("El DNI debe contener exactamente 8 dígitos numéricos.");
                        }
                    } else if (docLimpio.length() > 20) {
                        filaDto.agregarError("El número de documento no puede exceder 20 caracteres.");
                    }

                    // Duplicados dentro del mismo archivo
                    if (documentosEnArchivo.contains(docLimpio)) {
                        filaDto.agregarError("El número de documento '" + docLimpio + "' está duplicado en este mismo archivo.");
                    } else {
                        documentosEnArchivo.add(docLimpio);
                    }
                }

                // 3. Nombres y Apellidos
                if (nombresRaw == null || nombresRaw.isBlank()) {
                    filaDto.agregarError("El nombre es obligatorio.");
                } else if (nombresRaw.length() > 100) {
                    filaDto.agregarError("Los nombres no pueden exceder 100 caracteres.");
                }

                if (apePatRaw == null || apePatRaw.isBlank()) {
                    filaDto.agregarError("El apellido paterno es obligatorio.");
                } else if (apePatRaw.length() > 100) {
                    filaDto.agregarError("El apellido paterno no puede exceder 100 caracteres.");
                }

                if (apeMatRaw == null || apeMatRaw.isBlank()) {
                    filaDto.agregarError("El apellido materno es obligatorio.");
                } else if (apeMatRaw.length() > 100) {
                    filaDto.agregarError("El apellido materno no puede exceder 100 caracteres.");
                }

                // 4. Fecha de Nacimiento
                if (fechaNacRaw == null || fechaNacRaw.isBlank()) {
                    filaDto.agregarError("La fecha de nacimiento es obligatoria.");
                } else {
                    LocalDate fechaNac = parsearFecha(fechaNacRaw);
                    if (fechaNac == null) {
                        filaDto.agregarError("Formato de fecha inválido ('" + fechaNacRaw + "'). Utilice dd/MM/yyyy.");
                    } else if (fechaNac.isAfter(LocalDate.now())) {
                        filaDto.agregarError("La fecha de nacimiento no puede ser posterior al día de hoy.");
                    } else {
                        filaDto.setFechaNacimiento(fechaNac);
                    }
                }

                // 5. Sexo
                if (sexoRaw == null || sexoRaw.isBlank()) {
                    filaDto.agregarError("El sexo es obligatorio.");
                } else {
                    try {
                        filaDto.setSexo(Sexo.valueOf(sexoRaw.trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        filaDto.agregarError("Sexo inválido ('" + sexoRaw + "'). Debe ser MASCULINO o FEMENINO.");
                    }
                }

                // 6. Teléfono (opcional)
                if (telefonoRaw != null && !telefonoRaw.isBlank()) {
                    String telLimpio = telefonoRaw.trim();
                    if (!telLimpio.startsWith("9") || telLimpio.length() != 9 || !PATTERN_SOLO_DIGITOS.matcher(telLimpio).matches()) {
                        filaDto.agregarError("El teléfono debe tener 9 dígitos numéricos y comenzar con 9.");
                    }
                }

                // 7. Correo (opcional)
                if (correoRaw != null && !correoRaw.isBlank()) {
                    String correoLimpio = correoRaw.trim();
                    if (!PATTERN_EMAIL.matcher(correoLimpio).matches() || correoLimpio.length() > 100) {
                        filaDto.agregarError("El formato del correo electrónico no es válido.");
                    }
                }

                // 8. Verificación contra Base de Datos (si no tiene errores de formato)
                if (!filaDto.tieneErrores() && filaDto.getNumeroDocumento() != null) {
                    Optional<Persona> personaExistente = personaRepository.findByNumeroDocumento(filaDto.getNumeroDocumento());
                    if (personaExistente.isPresent()) {
                        filaDto.setEstado(EstadoFilaImportacion.YA_EXISTE);
                        filaDto.setPersonaExistenteId(personaExistente.get().getId());
                        filaDto.agregarAdvertencia("La persona ya existe en la base de datos (no se volverá a crear).");
                        existentes++;
                    } else {
                        filaDto.setEstado(EstadoFilaImportacion.VALIDA);
                        validas++;
                    }
                } else {
                    errores++;
                }

                listaFilas.add(filaDto);
            }

            resultado.setFilas(listaFilas);
            resultado.setTotalFilas(listaFilas.size());
            resultado.setFilasValidas(validas);
            resultado.setFilasConError(errores);
            resultado.setFilasExistentes(existentes);

            // Guardar en caché temporal para permitir confirmación segura
            cacheResultados.put(token, new CacheEntry(resultado, username, LocalDateTime.now().plusMinutes(tokenExpiracionMinutos)));

            return resultado;

        } catch (BusinessException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al procesar archivo Excel de estudiantes: {}", e.getMessage(), e);
            throw new ValidationException("El archivo Excel está dañado o no tiene un formato válido: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. VALIDACIÓN DE MATRÍCULAS
    // =========================================================================

    public ImportacionResultadoDTO<MatriculaImportRow> leerYValidarMatriculas(MultipartFile file, String username) {
        limpiarCacheExpirada();
        validarArchivoXlsx(file);

        String token = UUID.randomUUID().toString();
        String nombreOriginal = file.getOriginalFilename() != null ? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_") : "archivo.xlsx";
        ImportacionResultadoDTO<MatriculaImportRow> resultado = new ImportacionResultadoDTO<>(token, nombreOriginal, TipoImportacion.MATRICULAS, username);

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null || sheet.getLastRowNum() < 1) {
                resultado.agregarErrorGeneral("El archivo Excel no contiene filas de datos.");
                return resultado;
            }

            int numFilasDatos = contarFilasNoVacias(sheet);
            if (numFilasDatos > maxFilas) {
                throw new BusinessException("El archivo contiene " + numFilasDatos + " filas, superando el límite máximo permitido de " + maxFilas + " filas.");
            }

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colMap = mapearEncabezados(headerRow);
            validarEncabezadosMatriculas(colMap);

            DataFormatter formatter = new DataFormatter(Locale.getDefault());
            Set<String> codigosEnArchivo = new HashSet<>();
            Set<String> estudianteAnioEnArchivo = new HashSet<>();
            Map<Long, Integer> cuposOcupadosEnArchivo = new HashMap<>(); // seccionId -> cantidad acumulada en este Excel

            List<MatriculaImportRow> listaFilas = new ArrayList<>();
            int validas = 0;
            int errores = 0;
            int existentes = 0;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (esFilaVacia(row)) {
                    continue;
                }

                MatriculaImportRow filaDto = new MatriculaImportRow(r + 1);

                String docEstRaw = obtenerValorCelda(row, colMap.get("numero_documento_estudiante"), formatter);
                String anioRaw = obtenerValorCelda(row, colMap.get("anio"), formatter);
                String nivelRaw = obtenerValorCelda(row, colMap.get("nivel"), formatter);
                String gradoRaw = obtenerValorCelda(row, colMap.get("grado"), formatter);
                String seccionRaw = obtenerValorCelda(row, colMap.get("seccion"), formatter);
                String codMatRaw = obtenerValorCelda(row, colMap.get("codigo_matricula"), formatter);
                String fechaMatRaw = obtenerValorFecha(row, colMap.get("fecha_matricula"), formatter);
                String estadoRaw = obtenerValorCelda(row, colMap.get("estado"), formatter);
                String obsRaw = obtenerValorCelda(row, colMap.get("observaciones"), formatter);

                filaDto.setNumeroDocumentoEstudiante(docEstRaw);
                filaDto.setAnioRaw(anioRaw);
                filaDto.setNivelRaw(nivelRaw);
                filaDto.setGrado(gradoRaw);
                filaDto.setSeccion(seccionRaw);
                filaDto.setCodigoMatricula(codMatRaw);
                filaDto.setFechaMatriculaRaw(fechaMatRaw);
                filaDto.setEstadoRaw(estadoRaw);
                filaDto.setObservaciones(obsRaw);

                // 1. Estudiante por documento
                Persona estudiante = null;
                if (docEstRaw == null || docEstRaw.isBlank()) {
                    filaDto.agregarError("El número de documento del estudiante es obligatorio.");
                } else {
                    String docLimpio = docEstRaw.trim();
                    Optional<Persona> optP = personaRepository.findByNumeroDocumento(docLimpio);
                    if (optP.isEmpty()) {
                        filaDto.agregarError("El estudiante con documento '" + docLimpio + "' no existe en el sistema.");
                    } else {
                        estudiante = optP.get();
                        boolean esEstudianteActivo = personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(
                                estudiante.getId(), "ESTUDIANTE");
                        if (!esEstudianteActivo) {
                            filaDto.agregarError("La persona con documento '" + docLimpio + "' no tiene el perfil de ESTUDIANTE activo.");
                        } else {
                            filaDto.setPersonaEstudianteId(estudiante.getId());
                            filaDto.setNombreEstudiante(estudiante.getApellidoPaterno() + " " + estudiante.getApellidoMaterno() + ", " + estudiante.getNombres());
                        }
                    }
                }

                // 2. Año Escolar
                AnioEscolar anioEscolar = null;
                if (anioRaw == null || anioRaw.isBlank()) {
                    filaDto.agregarError("El año escolar es obligatorio.");
                } else {
                    try {
                        int anioNum = Integer.parseInt(anioRaw.trim());
                        filaDto.setAnio(anioNum);
                        Optional<AnioEscolar> optAnio = anioEscolarRepository.findByAnio(anioNum);
                        if (optAnio.isEmpty()) {
                            filaDto.agregarError("El año escolar " + anioNum + " no está registrado.");
                        } else {
                            anioEscolar = optAnio.get();
                            filaDto.setAnioEscolarId(anioEscolar.getId());
                        }
                    } catch (NumberFormatException e) {
                        filaDto.agregarError("El año escolar debe ser un número entero (ej. 2026).");
                    }
                }

                // 3. Nivel Educativo
                NivelEducativo nivelEducativo = null;
                if (nivelRaw == null || nivelRaw.isBlank()) {
                    filaDto.agregarError("El nivel educativo es obligatorio.");
                } else {
                    try {
                        nivelEducativo = NivelEducativo.valueOf(nivelRaw.trim().toUpperCase());
                        filaDto.setNivel(nivelEducativo);
                    } catch (IllegalArgumentException e) {
                        filaDto.agregarError("Nivel educativo inválido ('" + nivelRaw + "'). Debe ser INICIAL, PRIMARIA o SECUNDARIA.");
                    }
                }

                // 4. Grado
                Grado grado = null;
                if (gradoRaw == null || gradoRaw.isBlank()) {
                    filaDto.agregarError("El grado es obligatorio.");
                } else if (nivelEducativo != null) {
                    try {
                        int numeroGrado = Integer.parseInt(gradoRaw.trim().replaceAll("[^0-9]", ""));
                        Optional<Grado> optGrado = gradoRepository.findByNivelAndNumero(nivelEducativo, numeroGrado);
                        if (optGrado.isEmpty()) {
                            filaDto.agregarError("No existe el grado " + numeroGrado + " para el nivel " + nivelEducativo.name() + ".");
                        } else {
                            grado = optGrado.get();
                            filaDto.setGradoId(grado.getId());
                        }
                    } catch (Exception e) {
                        filaDto.agregarError("Número de grado inválido ('" + gradoRaw + "').");
                    }
                }

                // 5. Sección y pertenencia
                Seccion seccion = null;
                if (seccionRaw == null || seccionRaw.isBlank()) {
                    filaDto.agregarError("La sección es obligatoria.");
                } else if (grado != null) {
                    String secNombre = seccionRaw.trim().toUpperCase();
                    Optional<Seccion> optSec = seccionRepository.findByGradoIdAndNombre(grado.getId(), secNombre);
                    if (optSec.isEmpty()) {
                        filaDto.agregarError("La sección '" + secNombre + "' no existe en el grado " + grado.getNombre() + ".");
                    } else {
                        seccion = optSec.get();
                        filaDto.setSeccionId(seccion.getId());
                    }
                }

                // 6. Código de matrícula
                if (codMatRaw == null || codMatRaw.isBlank()) {
                    filaDto.agregarError("El código de matrícula es obligatorio.");
                } else {
                    String codLimpio = codMatRaw.trim();
                    filaDto.setCodigoMatricula(codLimpio);

                    if (codigosEnArchivo.contains(codLimpio)) {
                        filaDto.agregarError("El código de matrícula '" + codLimpio + "' está duplicado en este mismo archivo.");
                    } else {
                        codigosEnArchivo.add(codLimpio);
                    }

                    if (matriculaRepository.existsByCodigoMatricula(codLimpio)) {
                        filaDto.agregarError("El código de matrícula '" + codLimpio + "' ya existe en la base de datos.");
                    }
                }

                // 7. Fecha de matrícula
                if (fechaMatRaw == null || fechaMatRaw.isBlank()) {
                    filaDto.agregarError("La fecha de matrícula es obligatoria.");
                } else {
                    LocalDate fMat = parsearFecha(fechaMatRaw);
                    if (fMat == null) {
                        filaDto.agregarError("Formato de fecha inválido ('" + fechaMatRaw + "'). Utilice dd/MM/yyyy.");
                    } else {
                        filaDto.setFechaMatricula(fMat);
                    }
                }

                // 8. Estado de matrícula
                if (estadoRaw != null && !estadoRaw.isBlank()) {
                    try {
                        filaDto.setEstado(EstadoMatricula.valueOf(estadoRaw.trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        filaDto.agregarError("Estado inválido ('" + estadoRaw + "'). Debe ser MATRICULADO, TRASLADADO o RETIRADO.");
                    }
                } else {
                    filaDto.setEstado(EstadoMatricula.MATRICULADO);
                }

                // 9. Comprobar duplicado de estudiante en mismo año lectivo
                if (estudiante != null && anioEscolar != null) {
                    String parEstAnio = estudiante.getId() + "_" + anioEscolar.getId();
                    if (estudianteAnioEnArchivo.contains(parEstAnio)) {
                        filaDto.agregarError("El estudiante ya tiene otra fila de matrícula para el año " + anioEscolar.getAnio() + " en este archivo.");
                    } else {
                        estudianteAnioEnArchivo.add(parEstAnio);
                    }

                    if (matriculaRepository.existsByEstudianteIdAndAnioEscolarId(estudiante.getId(), anioEscolar.getId())) {
                        filaDto.agregarError("El estudiante ya cuenta con matrícula registrada para el año escolar " + anioEscolar.getAnio() + ".");
                    }
                }

                // 10. Validación de capacidad acumulada de la sección
                if (seccion != null && anioEscolar != null && !filaDto.tieneErrores()) {
                    long ocupadasBd = matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(
                            seccion.getId(), anioEscolar.getId(), EstadoMatricula.MATRICULADO);
                    int acumuladasArchivo = cuposOcupadosEnArchivo.getOrDefault(seccion.getId(), 0);
                    int totalProyectado = (int) ocupadasBd + acumuladasArchivo + 1;

                    if (totalProyectado > seccion.getCapacidad()) {
                        filaDto.agregarError("La sección '" + seccion.getNombre() + "' no cuenta con vacantes suficientes (Capacidad: "
                                + seccion.getCapacidad() + ", Ocupadas BD: " + ocupadasBd + ", Acumuladas archivo: " + acumuladasArchivo + ").");
                    } else {
                        cuposOcupadosEnArchivo.put(seccion.getId(), acumuladasArchivo + 1);
                    }
                }

                if (filaDto.tieneErrores()) {
                    filaDto.setEstadoFila(EstadoFilaImportacion.ERROR);
                    errores++;
                } else {
                    filaDto.setEstadoFila(EstadoFilaImportacion.VALIDA);
                    validas++;
                }

                listaFilas.add(filaDto);
            }

            resultado.setFilas(listaFilas);
            resultado.setTotalFilas(listaFilas.size());
            resultado.setFilasValidas(validas);
            resultado.setFilasConError(errores);
            resultado.setFilasExistentes(existentes);

            cacheResultados.put(token, new CacheEntry(resultado, username, LocalDateTime.now().plusMinutes(tokenExpiracionMinutos)));

            return resultado;

        } catch (BusinessException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al procesar archivo Excel de matrículas: {}", e.getMessage(), e);
            throw new ValidationException("El archivo Excel está dañado o no tiene un formato válido: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. CONSULTA Y GESTIÓN DE CACHÉ TEMPORAL
    // =========================================================================

    @SuppressWarnings("unchecked")
    public <T> ImportacionResultadoDTO<T> obtenerResultadoTemporal(String token, String username) {
        limpiarCacheExpirada();
        CacheEntry entry = cacheResultados.get(token);
        if (entry == null) {
            throw new BusinessException("La validación de importación expiró o no existe. Vuelva a cargar el archivo.");
        }
        if (!entry.username.equals(username)) {
            throw new BusinessException("No tiene permisos para acceder a esta importación.");
        }
        if (LocalDateTime.now().isAfter(entry.expiraEn)) {
            cacheResultados.remove(token);
            throw new BusinessException("La validación de importación expiró. Vuelva a cargar el archivo.");
        }
        return (ImportacionResultadoDTO<T>) entry.resultado;
    }

    public void limpiarResultadoTemporal(String token) {
        if (token != null) {
            cacheResultados.remove(token);
        }
    }

    public void guardarResultadoEnCache(String token, ImportacionResultadoDTO<?> resultado, String username, int minutosExpiracion) {
        cacheResultados.put(token, new CacheEntry(resultado, username, LocalDateTime.now().plusMinutes(minutosExpiracion)));
    }

    private void limpiarCacheExpirada() {
        LocalDateTime now = LocalDateTime.now();
        cacheResultados.entrySet().removeIf(e -> now.isAfter(e.getValue().expiraEn));
    }

    // =========================================================================
    // 4. MÉTODOS AUXILIARES DE LECTURA Y SEGURIDAD POI
    // =========================================================================

    private void validarArchivoXlsx(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Seleccione un archivo Excel para continuar.");
        }

        String nombre = file.getOriginalFilename();
        if (nombre == null || !nombre.toLowerCase().endsWith(".xlsx")) {
            throw new ValidationException("El archivo debe tener formato XLSX válido (.xlsx).");
        }

        if (file.getSize() > maxArchivoBytes) {
            throw new BusinessException("El archivo supera el tamaño máximo permitido de " + (maxArchivoBytes / 1024 / 1024) + " MB.");
        }
    }

    private int contarFilasNoVacias(Sheet sheet) {
        int count = 0;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (!esFilaVacia(row)) {
                count++;
            }
        }
        return count;
    }

    private boolean esFilaVacia(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = cell.toString().trim();
                if (!val.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Map<String, Integer> mapearEncabezados(Row headerRow) {
        if (headerRow == null) {
            throw new ValidationException("El archivo Excel no contiene una fila de encabezados.");
        }
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell c = headerRow.getCell(i);
            if (c != null) {
                String name = c.getStringCellValue();
                if (name != null) {
                    map.put(name.trim().toLowerCase(), i);
                }
            }
        }
        return map;
    }

    private void validarEncabezadosEstudiantes(Map<String, Integer> colMap) {
        String[] requeridos = {
                "tipo_documento", "numero_documento", "nombres", "apellido_paterno",
                "apellido_materno", "fecha_nacimiento", "sexo"
        };
        for (String req : requeridos) {
            if (!colMap.containsKey(req)) {
                throw new ValidationException("La plantilla no contiene la columna obligatoria '" + req + "'.");
            }
        }
    }

    private void validarEncabezadosMatriculas(Map<String, Integer> colMap) {
        String[] requeridos = {
                "numero_documento_estudiante", "anio", "nivel", "grado",
                "seccion", "codigo_matricula", "fecha_matricula"
        };
        for (String req : requeridos) {
            if (!colMap.containsKey(req)) {
                throw new ValidationException("La plantilla no contiene la columna obligatoria '" + req + "'.");
            }
        }
    }

    private String obtenerValorCelda(Row row, Integer colIdx, DataFormatter formatter) {
        if (colIdx == null || row == null) return null;
        Cell cell = row.getCell(colIdx);
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Evitar notación científica para enteros (como DNI o año)
            double num = cell.getNumericCellValue();
            if (num == Math.floor(num) && !Double.isInfinite(num)) {
                return String.format(Locale.ROOT, "%.0f", num);
            }
            return formatter.formatCellValue(cell).trim();
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (Exception e) {
                return formatter.formatCellValue(cell).trim();
            }
        }
        return formatter.formatCellValue(cell).trim();
    }

    private String obtenerValorFecha(Row row, Integer colIdx, DataFormatter formatter) {
        if (colIdx == null || row == null) return null;
        Cell cell = row.getCell(colIdx);
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            LocalDate ld = cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return ld.format(FORMATO_FECHA_PERU);
        }
        return obtenerValorCelda(row, colIdx, formatter);
    }

    private LocalDate parsearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) return null;
        String limpia = fechaStr.trim();
        try {
            return LocalDate.parse(limpia, FORMATO_FECHA_PERU);
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDate.parse(limpia, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }

    private static class CacheEntry {
        final ImportacionResultadoDTO<?> resultado;
        final String username;
        final LocalDateTime expiraEn;

        CacheEntry(ImportacionResultadoDTO<?> resultado, String username, LocalDateTime expiraEn) {
            this.resultado = resultado;
            this.username = username;
            this.expiraEn = expiraEn;
        }
    }
}
