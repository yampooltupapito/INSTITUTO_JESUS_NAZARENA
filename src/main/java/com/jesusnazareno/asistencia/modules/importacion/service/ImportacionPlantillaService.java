package com.jesusnazareno.asistencia.modules.importacion.service;

/**
 * Servicio para la generación de plantillas Excel (.xlsx) oficiales de importación masiva.
 */
public interface ImportacionPlantillaService {

    /**
     * Genera la plantilla XLSX oficial para la importación masiva de estudiantes.
     *
     * @return arreglo de bytes del archivo XLSX
     */
    byte[] generarPlantillaEstudiantes();

    /**
     * Genera la plantilla XLSX oficial para la importación masiva de matrículas.
     *
     * @return arreglo de bytes del archivo XLSX
     */
    byte[] generarPlantillaMatriculas();
}
