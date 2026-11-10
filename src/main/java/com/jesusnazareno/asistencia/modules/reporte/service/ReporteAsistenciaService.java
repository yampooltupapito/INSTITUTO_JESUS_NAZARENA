package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaItem;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResultado;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para la generación y consolidación de reportes de asistencia.
 */
public interface ReporteAsistenciaService {

    /**
     * Valida que los filtros de búsqueda sean coherentes y existan en la base de datos.
     */
    void validarFiltros(ReporteAsistenciaFiltro filtro);

    /**
     * Genera el resultado de reporte paginado para la vista web.
     */
    ReporteAsistenciaResultado generarReporte(ReporteAsistenciaFiltro filtro, Pageable pageable);

    /**
     * Obtiene todos los ítems para exportación o resumen completo (limitado a máximo seguro).
     */
    List<ReporteAsistenciaItem> obtenerTodosItems(ReporteAsistenciaFiltro filtro);

    /**
     * Calcula los totales e indicadores porcentuales del periodo consultado.
     */
    ReporteAsistenciaResumen calcularResumen(ReporteAsistenciaFiltro filtro);

    /**
     * Genera el documento PDF de reporte con OpenPDF.
     */
    byte[] generarPdf(ReporteAsistenciaFiltro filtro);

    /**
     * Genera un nombre de archivo seguro y descriptivo para el PDF.
     */
    String generarNombreArchivoPdf(ReporteAsistenciaFiltro filtro);

    /**
     * Genera el documento Excel (.xlsx) de reporte con Apache POI.
     */
    byte[] generarExcel(ReporteAsistenciaFiltro filtro);

    /**
     * Genera un nombre de archivo seguro y descriptivo para el Excel (.xlsx).
     */
    String generarNombreArchivoExcel(ReporteAsistenciaFiltro filtro);

    /**
     * Genera el documento CSV (UTF-8 con BOM) de reporte.
     */
    byte[] generarCsv(ReporteAsistenciaFiltro filtro);

    /**
     * Genera un nombre de archivo seguro y descriptivo para el CSV (.csv).
     */
    String generarNombreArchivoCsv(ReporteAsistenciaFiltro filtro);
}
