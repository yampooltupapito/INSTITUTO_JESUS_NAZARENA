package com.jesusnazareno.asistencia.modules.reporte.controller;

import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaFiltro;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResultado;
import com.jesusnazareno.asistencia.modules.reporte.dto.ReporteAsistenciaResumen;
import com.jesusnazareno.asistencia.modules.reporte.service.ReporteAsistenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Seguridad y Web - ReporteAsistenciaController")
class ReporteAsistenciaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteAsistenciaService reporteAsistenciaService;

    @MockBean
    private AnioEscolarRepository anioEscolarRepository;

    @MockBean
    private GradoRepository gradoRepository;

    @MockBean
    private SeccionRepository seccionRepository;

    @MockBean
    private PersonaRepository personaRepository;

    @MockBean
    private PersonaActorRepository personaActorRepository;

    @MockBean
    private MatriculaRepository matriculaRepository;

    @BeforeEach
    void setUp() {
        ReporteAsistenciaResumen resumen = new ReporteAsistenciaResumen(30, 28, 25, 2, 1, 0);
        ReporteAsistenciaResultado resultado = new ReporteAsistenciaResultado(
                Page.empty(),
                Collections.emptyList(),
                resumen,
                new ReporteAsistenciaFiltro()
        );

        when(reporteAsistenciaService.generarReporte(any(ReporteAsistenciaFiltro.class), any(Pageable.class)))
                .thenReturn(resultado);

        when(anioEscolarRepository.findAll()).thenReturn(Collections.emptyList());
        when(gradoRepository.findAll()).thenReturn(Collections.emptyList());
        when(seccionRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("1. /reportes/asistencia sin autenticación redirige al login")
    void reportes_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/reportes/asistencia"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("2. REPORTE_ASISTENCIA permite acceder a la vista web")
    void reportes_conPermiso_retornaVistaReportes() throws Exception {
        mockMvc.perform(get("/reportes/asistencia"))
                .andExpect(status().isOk())
                .andExpect(view().name("reportes/asistencia"))
                .andExpect(model().attributeExists("pagina"))
                .andExpect(model().attributeExists("resumen"))
                .andExpect(model().attributeExists("tiposReporte"))
                .andExpect(model().attributeExists("estados"));
    }

    @Test
    @WithMockUser(username = "usuario_sin_permiso", authorities = {"OTRO_PERMISO"})
    @DisplayName("3. Usuario sin permiso recibe 403 Forbidden")
    void reportes_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/reportes/asistencia"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("20. endpoint PDF sin autenticación redirige a login")
    void descargarPdf_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/reportes/asistencia/pdf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "usuario_sin_permiso", authorities = {"ESTUDIANTE_VER"})
    @DisplayName("20b. endpoint PDF sin permiso REPORTE_ASISTENCIA devuelve 403")
    void descargarPdf_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/reportes/asistencia/pdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("17. PDF se genera con Content-Type application/pdf y Content-Disposition")
    void descargarPdf_conPermiso_retornaPdfBytes() throws Exception {
        byte[] pdfBytes = "%PDF-1.4 mock pdf content".getBytes();
        when(reporteAsistenciaService.generarPdf(any(ReporteAsistenciaFiltro.class)))
                .thenReturn(pdfBytes);
        when(reporteAsistenciaService.generarNombreArchivoPdf(any(ReporteAsistenciaFiltro.class)))
                .thenReturn("reporte_asistencia_2026-10-02.pdf");

        mockMvc.perform(get("/reportes/asistencia/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"reporte_asistencia_2026-10-02.pdf\""))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("Ruta /reportes/asistencia/estudiante/{id} con permiso permite consultar reporte de estudiante")
    void verReporteEstudiante_conPermiso_retornaVista() throws Exception {
        mockMvc.perform(get("/reportes/asistencia/estudiante/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("reportes/asistencia"));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("Ruta /reportes/asistencia/estudiante/{id}/pdf con permiso descarga el PDF")
    void descargarPdfEstudiante_conPermiso_retornaPdf() throws Exception {
        byte[] pdfBytes = "%PDF-1.4 mock student pdf".getBytes();
        when(reporteAsistenciaService.generarPdf(any(ReporteAsistenciaFiltro.class)))
                .thenReturn(pdfBytes);
        when(reporteAsistenciaService.generarNombreArchivoPdf(any(ReporteAsistenciaFiltro.class)))
                .thenReturn("reporte_asistencia_estudiante_10.pdf");

        mockMvc.perform(get("/reportes/asistencia/estudiante/10/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("4. endpoint Excel sin autenticación redirige a login")
    void descargarExcel_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/reportes/asistencia/excel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "usuario_sin_permiso", authorities = {"ESTUDIANTE_VER"})
    @DisplayName("5. endpoint Excel sin permiso REPORTE_ASISTENCIA devuelve 403")
    void descargarExcel_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/reportes/asistencia/excel"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("6. Excel responde con Content-Type application/vnd.openxmlformats-officedocument.spreadsheetml.sheet y Content-Disposition")
    void descargarExcel_conPermiso_retornaExcelBytes() throws Exception {
        byte[] excelBytes = new byte[]{0x50, 0x4B, 0x03, 0x04}; // ZIP/XLSX header
        when(reporteAsistenciaService.generarExcel(any(ReporteAsistenciaFiltro.class)))
                .thenReturn(excelBytes);
        when(reporteAsistenciaService.generarNombreArchivoExcel(any(ReporteAsistenciaFiltro.class)))
                .thenReturn("reporte_asistencia_2026-10-02.xlsx");

        mockMvc.perform(get("/reportes/asistencia/excel"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"reporte_asistencia_2026-10-02.xlsx\""))
                .andExpect(content().bytes(excelBytes));
    }

    @Test
    @DisplayName("7. endpoint CSV sin autenticación redirige a login")
    void descargarCsv_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/reportes/asistencia/csv"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "usuario_sin_permiso", authorities = {"ESTUDIANTE_VER"})
    @DisplayName("8. endpoint CSV sin permiso REPORTE_ASISTENCIA devuelve 403")
    void descargarCsv_sinPermiso_devuelve403() throws Exception {
        mockMvc.perform(get("/reportes/asistencia/csv"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("9. CSV responde con Content-Type text/csv y Content-Disposition")
    void descargarCsv_conPermiso_retornaCsvBytes() throws Exception {
        byte[] csvBytes = "N°;Fecha;Documento;Estudiante".getBytes();
        when(reporteAsistenciaService.generarCsv(any(ReporteAsistenciaFiltro.class)))
                .thenReturn(csvBytes);
        when(reporteAsistenciaService.generarNombreArchivoCsv(any(ReporteAsistenciaFiltro.class)))
                .thenReturn("reporte_asistencia_2026-10-02.csv");

        mockMvc.perform(get("/reportes/asistencia/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("text/csv; charset=UTF-8")))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"reporte_asistencia_2026-10-02.csv\""))
                .andExpect(content().bytes(csvBytes));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("10. Ruta /reportes/asistencia/estudiante/{id}/excel con permiso descarga el Excel")
    void descargarExcelEstudiante_conPermiso_retornaExcel() throws Exception {
        byte[] excelBytes = new byte[]{0x50, 0x4B, 0x03, 0x04};
        when(reporteAsistenciaService.generarExcel(any(ReporteAsistenciaFiltro.class)))
                .thenReturn(excelBytes);
        when(reporteAsistenciaService.generarNombreArchivoExcel(any(ReporteAsistenciaFiltro.class)))
                .thenReturn("reporte_asistencia_estudiante_10.xlsx");

        mockMvc.perform(get("/reportes/asistencia/estudiante/10/excel"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")));
    }

    @Test
    @WithMockUser(username = "director", authorities = {"REPORTE_ASISTENCIA"})
    @DisplayName("11. Ruta /reportes/asistencia/estudiante/{id}/csv con permiso descarga el CSV")
    void descargarCsvEstudiante_conPermiso_retornaCsv() throws Exception {
        byte[] csvBytes = "N°;Fecha;Documento;Estudiante".getBytes();
        when(reporteAsistenciaService.generarCsv(any(ReporteAsistenciaFiltro.class)))
                .thenReturn(csvBytes);
        when(reporteAsistenciaService.generarNombreArchivoCsv(any(ReporteAsistenciaFiltro.class)))
                .thenReturn("reporte_asistencia_estudiante_10.csv");

        mockMvc.perform(get("/reportes/asistencia/estudiante/10/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("text/csv; charset=UTF-8")));
    }
}
