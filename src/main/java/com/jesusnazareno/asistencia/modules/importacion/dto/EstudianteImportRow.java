package com.jesusnazareno.asistencia.modules.importacion.dto;

import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa una fila leída desde la plantilla Excel de Estudiantes.
 * NO es entidad JPA y contiene el estado de validación y lista de errores por fila.
 */
public class EstudianteImportRow {

    private int numeroFila;
    private TipoDocumento tipoDocumento;
    private String tipoDocumentoRaw;
    private String numeroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String fechaNacimientoRaw;
    private Sexo sexo;
    private String sexoRaw;
    private String telefono;
    private String correo;
    private String direccion;

    private EstadoFilaImportacion estado = EstadoFilaImportacion.VALIDA;
    private List<String> errores = new ArrayList<>();
    private List<String> advertencias = new ArrayList<>();
    private Long personaExistenteId;

    public EstudianteImportRow() {
    }

    public EstudianteImportRow(int numeroFila) {
        this.numeroFila = numeroFila;
    }

    public void agregarError(String error) {
        this.errores.add(error);
        this.estado = EstadoFilaImportacion.ERROR;
    }

    public void agregarAdvertencia(String advertencia) {
        this.advertencias.add(advertencia);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public int getNumeroFila() {
        return numeroFila;
    }

    public void setNumeroFila(int numeroFila) {
        this.numeroFila = numeroFila;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoDocumentoRaw() {
        return tipoDocumentoRaw;
    }

    public void setTipoDocumentoRaw(String tipoDocumentoRaw) {
        this.tipoDocumentoRaw = tipoDocumentoRaw;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaNacimientoRaw() {
        return fechaNacimientoRaw;
    }

    public void setFechaNacimientoRaw(String fechaNacimientoRaw) {
        this.fechaNacimientoRaw = fechaNacimientoRaw;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getSexoRaw() {
        return sexoRaw;
    }

    public void setSexoRaw(String sexoRaw) {
        this.sexoRaw = sexoRaw;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public EstadoFilaImportacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoFilaImportacion estado) {
        this.estado = estado;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }

    public List<String> getAdvertencias() {
        return advertencias;
    }

    public void setAdvertencias(List<String> advertencias) {
        this.advertencias = advertencias;
    }

    public Long getPersonaExistenteId() {
        return personaExistenteId;
    }

    public void setPersonaExistenteId(Long personaExistenteId) {
        this.personaExistenteId = personaExistenteId;
    }

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (apellidoPaterno != null) sb.append(apellidoPaterno).append(" ");
        if (apellidoMaterno != null) sb.append(apellidoMaterno).append(", ");
        if (nombres != null) sb.append(nombres);
        return sb.toString().trim();
    }
}
