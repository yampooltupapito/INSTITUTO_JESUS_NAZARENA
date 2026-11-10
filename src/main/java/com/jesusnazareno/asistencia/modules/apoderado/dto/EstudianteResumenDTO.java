package com.jesusnazareno.asistencia.modules.apoderado.dto;

import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;

import java.time.LocalDate;

public record EstudianteResumenDTO(
        Long relacionId,
        Long estudianteId,
        String numeroDocumento,
        String nombreCompleto,
        Parentesco parentesco,
        String parentescoDescripcion,
        Boolean esPrincipal,
        Boolean relacionActiva,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {}
