package com.jesusnazareno.asistencia.modules.importacion.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO que encapsula el resultado completo del proceso de lectura y validación de una importación.
 * Permite presentar la vista previa, estadísticas globales y gestionar el token temporal de confirmación.
 */
public class ImportacionResultadoDTO<T> {

    private String token;
    private String nombreArchivo;
    private TipoImportacion tipo;
    private LocalDateTime fechaCarga;
    private String usuarioUsername;

    private int totalFilas;
    private int filasValidas;
    private int filasConError;
    private int filasExistentes;

    private List<String> erroresGenerales = new ArrayList<>();
    private List<T> filas = new ArrayList<>();

    public ImportacionResultadoDTO() {
        this.fechaCarga = LocalDateTime.now();
    }

    public ImportacionResultadoDTO(String token, String nombreArchivo, TipoImportacion tipo, String usuarioUsername) {
        this.token = token;
        this.nombreArchivo = nombreArchivo;
        this.tipo = tipo;
        this.usuarioUsername = usuarioUsername;
        this.fechaCarga = LocalDateTime.now();
    }

    public void agregarErrorGeneral(String error) {
        this.erroresGenerales.add(error);
    }

    public boolean tieneErroresGenerales() {
        return !erroresGenerales.isEmpty();
    }

    public boolean esValidoParaImportar() {
        return totalFilas > 0 && filasConError == 0 && erroresGenerales.isEmpty() && filasValidas > 0;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public TipoImportacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoImportacion tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public int getTotalFilas() {
        return totalFilas;
    }

    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
    }

    public int getFilasValidas() {
        return filasValidas;
    }

    public void setFilasValidas(int filasValidas) {
        this.filasValidas = filasValidas;
    }

    public int getFilasConError() {
        return filasConError;
    }

    public void setFilasConError(int filasConError) {
        this.filasConError = filasConError;
    }

    public int getFilasExistentes() {
        return filasExistentes;
    }

    public void setFilasExistentes(int filasExistentes) {
        this.filasExistentes = filasExistentes;
    }

    public List<String> getErroresGenerales() {
        return erroresGenerales;
    }

    public void setErroresGenerales(List<String> erroresGenerales) {
        this.erroresGenerales = erroresGenerales;
    }

    public List<T> getFilas() {
        return filas;
    }

    public void setFilas(List<T> filas) {
        this.filas = filas;
    }
}
