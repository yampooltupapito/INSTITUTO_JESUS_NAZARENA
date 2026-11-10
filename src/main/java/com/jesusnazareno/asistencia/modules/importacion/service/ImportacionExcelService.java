package com.jesusnazareno.asistencia.modules.importacion.service;

import com.jesusnazareno.asistencia.modules.importacion.dto.EstudianteImportRow;
import com.jesusnazareno.asistencia.modules.importacion.dto.ImportacionResultadoDTO;
import com.jesusnazareno.asistencia.modules.importacion.dto.MatriculaImportRow;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz de servicio para la lectura, validación y gestión en memoria de archivos Excel XLSX para importaciones.
 */
public interface ImportacionExcelService {

    /**
     * Lee y valida en memoria el archivo Excel de estudiantes.
     * NO realiza inserciones en la base de datos.
     */
    ImportacionResultadoDTO<EstudianteImportRow> leerYValidarEstudiantes(MultipartFile file, String username);

    /**
     * Lee y valida en memoria el archivo Excel de matrículas.
     * NO realiza inserciones en la base de datos.
     */
    ImportacionResultadoDTO<MatriculaImportRow> leerYValidarMatriculas(MultipartFile file, String username);

    /**
     * Obtiene el resultado temporal previamente validado y almacenado en memoria.
     */
    <T> ImportacionResultadoDTO<T> obtenerResultadoTemporal(String token, String username);

    /**
     * Elimina el resultado temporal de la memoria tras su confirmación o cancelación.
     */
    void limpiarResultadoTemporal(String token);

    /**
     * Almacena un resultado en la memoria temporal segura con tiempo de expiración.
     */
    void guardarResultadoEnCache(String token, ImportacionResultadoDTO<?> resultado, String username, int minutosExpiracion);

    /**
     * Configura el límite máximo de filas admitidas por archivo.
     */
    void setMaxFilas(int maxFilas);

    /**
     * Configura el tamaño máximo en bytes permitido para los archivos subidos.
     */
    void setMaxArchivoBytes(long maxArchivoBytes);
}
