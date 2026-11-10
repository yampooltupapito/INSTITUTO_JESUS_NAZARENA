package com.jesusnazareno.asistencia.modules.matricula.service;

import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrato de servicio para la gestión de matrículas escolares.
 */
public interface MatriculaService {

    /**
     * Registra una nueva matrícula aplicando todas las validaciones de negocio.
     *
     * @param request Datos de la matrícula a registrar
     * @return Matrícula registrada
     */
    MatriculaResponse crearMatricula(MatriculaRequest request);

    /**
     * Busca una matrícula por su identificador único.
     *
     * @param id Identificador de la matrícula
     * @return Datos de la matrícula
     */
    MatriculaResponse buscarPorId(Long id);

    /**
     * Actualiza los datos de una matrícula existente.
     *
     * @param id Identificador de la matrícula a actualizar
     * @param request Datos actualizados
     * @return Matrícula actualizada
     */
    MatriculaResponse actualizarMatricula(Long id, MatriculaRequest request);

    /**
     * Lista todas las matrículas con paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de matrículas
     */
    Page<MatriculaResponse> listarPaginado(Pageable pageable);

    /**
     * Lista todas las matrículas asociadas a un estudiante.
     *
     * @param personaEstudianteId Identificador del estudiante (persona)
     * @return Lista de matrículas
     */
    List<MatriculaResponse> listarPorEstudiante(Long personaEstudianteId);

    /**
     * Lista matrículas asociadas a un estudiante con paginación.
     *
     * @param personaEstudianteId Identificador del estudiante (persona)
     * @param pageable Configuración de paginación
     * @return Página de matrículas
     */
    Page<MatriculaResponse> listarPorEstudiante(Long personaEstudianteId, Pageable pageable);

    /**
     * Lista matrículas correspondientes a un año escolar con paginación.
     *
     * @param anioEscolarId Identificador del año escolar
     * @param pageable Configuración de paginación
     * @return Página de matrículas
     */
    Page<MatriculaResponse> listarPorAnioEscolar(Long anioEscolarId, Pageable pageable);

    /**
     * Cambia el estado de una matrícula (ej. RETIRADO, TRASLADADO, etc.).
     *
     * @param id Identificador de la matrícula
     * @param nuevoEstado Nuevo estado a establecer
     * @return Matrícula actualizada
     */
    MatriculaResponse cambiarEstado(Long id, EstadoMatricula nuevoEstado);

    /**
     * Búsqueda paginada de matrículas con múltiples filtros.
     */
    Page<MatriculaResponse> buscarMatriculas(String q, Long anioEscolarId, Long gradoId, Long seccionId, EstadoMatricula estado, Pageable pageable);

    /**
     * Obtiene las secciones de un grado con su capacidad, número de matriculados y vacantes disponibles.
     */
    List<com.jesusnazareno.asistencia.modules.matricula.dto.SeccionCapacidadDTO> listarSeccionesConCapacidad(Long gradoId, Long anioEscolarId);
}
