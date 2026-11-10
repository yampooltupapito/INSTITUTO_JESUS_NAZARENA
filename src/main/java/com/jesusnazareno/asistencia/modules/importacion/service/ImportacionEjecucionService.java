package com.jesusnazareno.asistencia.modules.importacion.service;

/**
 * Interfaz del servicio transaccional para la confirmación y persistencia de importaciones validadas.
 */
public interface ImportacionEjecucionService {

    /**
     * Confirma e inserta los estudiantes validados.
     *
     * @param token Token temporal de importación validada
     * @param username Nombre del usuario autenticado que autoriza la confirmación
     * @return Cantidad de nuevos estudiantes registrados
     */
    int confirmarImportacionEstudiantes(String token, String username);

    /**
     * Confirma e inserta las matrículas validadas.
     *
     * @param token Token temporal de importación validada
     * @param username Nombre del usuario autenticado que autoriza la confirmación
     * @return Cantidad de matrículas registradas
     */
    int confirmarImportacionMatriculas(String token, String username);
}
