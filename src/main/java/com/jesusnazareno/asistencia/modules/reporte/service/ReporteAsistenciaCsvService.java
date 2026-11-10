package com.jesusnazareno.asistencia.modules.reporte.service;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaItem;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio encargado de la generación de reportes en formato CSV con codificación UTF-8 y BOM.
 * Utiliza delimitador de punto y coma (;) para óptima compatibilidad con Excel en entornos de habla hispana,
 * escape riguroso de caracteres y protección contra formula injection.
 */
@Service
public class ReporteAsistenciaCsvService {

    private static final Logger log = LoggerFactory.getLogger(ReporteAsistenciaCsvService.class);
    private static final String DELIMITADOR = ";";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public byte[] generarCsv(ReporteAsistenciaFiltro filtro,
                             List<ReporteAsistenciaItem> items,
                             ReporteAsistenciaResumen resumen) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Escribir UTF-8 BOM para que Excel en Windows detecte automáticamente la codificación con tildes y eñes
            out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
                // 1. Encabezados de columnas
                String[] headers = {
                        "N°", "Fecha", "Documento", "Estudiante", "Grado", "Seccion",
                        "Estado", "Hora_Ingreso", "Hora_Salida", "Observacion"
                };
                writer.println(String.join(DELIMITADOR, headers));

                // 2. Filas de datos
                if (items != null) {
                    for (ReporteAsistenciaItem item : items) {
                        String num = item.getNumero() != null ? String.valueOf(item.getNumero()) : "";
                        String fecha = item.getFecha() != null ? item.getFecha().format(FORMATO_FECHA) : "";
                        String doc = item.getNumeroDocumento() != null ? item.getNumeroDocumento() : "";
                        String est = item.getEstudianteCompleto() != null ? item.getEstudianteCompleto() : "";
                        String grado = item.getGrado() != null ? item.getGrado() : "";
                        String seccion = item.getSeccion() != null ? item.getSeccion() : "";
                        String estado = item.getEstado() != null ? item.getEstado().name() : "NO REGISTRADO";
                        String hIngreso = item.getHoraIngreso() != null ? item.getHoraIngreso().format(FORMATO_HORA) : "";
                        String hSalida = item.getHoraSalida() != null ? item.getHoraSalida().format(FORMATO_HORA) : "";
                        String obs = item.getObservacion() != null ? item.getObservacion() : "";

                        StringBuilder line = new StringBuilder();
                        line.append(escaparCampo(num)).append(DELIMITADOR);
                        line.append(escaparCampo(fecha)).append(DELIMITADOR);
                        line.append(escaparCampo(doc)).append(DELIMITADOR);
                        line.append(escaparCampo(est)).append(DELIMITADOR);
                        line.append(escaparCampo(grado)).append(DELIMITADOR);
                        line.append(escaparCampo(seccion)).append(DELIMITADOR);
                        line.append(escaparCampo(estado)).append(DELIMITADOR);
                        line.append(escaparCampo(hIngreso)).append(DELIMITADOR);
                        line.append(escaparCampo(hSalida)).append(DELIMITADOR);
                        line.append(escaparCampo(obs));

                        writer.println(line.toString());
                    }
                }

                // 3. Resumen estadístico al pie del CSV
                if (resumen != null) {
                    writer.println();
                    writer.println("# --- RESUMEN ESTADÍSTICO DE ASISTENCIA ---");
                    writer.println("Categoria" + DELIMITADOR + "Cantidad" + DELIMITADOR + "Porcentaje");
                    writer.println("Presentes" + DELIMITADOR + resumen.getPresentes() + DELIMITADOR + resumen.getPorcentajePresentes() + "%");
                    writer.println("Tardanzas" + DELIMITADOR + resumen.getTardanzas() + DELIMITADOR + resumen.getPorcentajeTardanzas() + "%");
                    writer.println("Ausencias" + DELIMITADOR + resumen.getAusentes() + DELIMITADOR + resumen.getPorcentajeAusentes() + "%");
                    writer.println("Justificados" + DELIMITADOR + resumen.getJustificados() + DELIMITADOR + resumen.getPorcentajeJustificados() + "%");
                    writer.println("Total Registros" + DELIMITADOR + resumen.getTotalRegistros() + DELIMITADOR + "100.0%");
                    writer.println("% Asistencia General" + DELIMITADOR + (resumen.getPresentes() + resumen.getTardanzas()) + DELIMITADOR + resumen.getPorcentajeAsistencia() + "%");
                }

                writer.flush();
            }

            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error al generar archivo CSV de reporte: {}", e.getMessage(), e);
            throw new RuntimeException("Error en la construcción del archivo CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Escapa un campo individual para CSV:
     * 1. Neutraliza Formula Injection anteponiendo apóstrofe (') si inicia con '=', '+', '-', '@', '\t', '\r'.
     * 2. Si contiene delimitador (;), comillas dobles (") o saltos de línea, envuelve en comillas dobles y duplica comillas internas.
     */
    public static String escaparCampo(String valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.trim();
        if (texto.isEmpty()) {
            return "";
        }

        // Formula Injection protection
        char first = texto.charAt(0);
        if (first == '=' || first == '+' || first == '-' || first == '@' || first == '\t' || first == '\r') {
            texto = "'" + texto;
        }

        boolean contieneCaracteresEspeciales = texto.contains(DELIMITADOR)
                || texto.contains("\"")
                || texto.contains("\n")
                || texto.contains("\r");

        if (contieneCaracteresEspeciales) {
            texto = texto.replace("\"", "\"\"");
            return "\"" + texto + "\"";
        }

        return texto;
    }
}
