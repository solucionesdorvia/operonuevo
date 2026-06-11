package com.opero.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opero.api.dto.CreateIncidentRequest;
import com.opero.api.dto.IncidentResponse;
import com.opero.api.dto.UpdateStatusRequest;
import com.opero.api.entity.IncidentPriority;
import com.opero.api.entity.IncidentStatus;
import com.opero.api.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para IncidentController.
 *
 * Estas pruebas verifican que los endpoints HTTP funcionan correctamente,
 * usando mocks del servicio en lugar de la base de datos real.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IncidentService incidentService;

    private IncidentResponse testIncidentResponse;

    @BeforeEach
    void setUp() {
        testIncidentResponse = new IncidentResponse();
        testIncidentResponse.setId(1);
        testIncidentResponse.setTitle("Test Incident");
        testIncidentResponse.setDescription("Test Description");
        testIncidentResponse.setStatus(IncidentStatus.PENDING);
        testIncidentResponse.setPriority(IncidentPriority.MEDIUM);
        testIncidentResponse.setReporterId(1);
        testIncidentResponse.setReporterName("Test User");
        testIncidentResponse.setReporterEmail("test@uade.edu.ar");
        testIncidentResponse.setDepartmentId(1);
        testIncidentResponse.setDepartmentName("Mantenimiento");
    }

    /**
     * Test 1: GET /api/incidents - Verificar que retorna lista de incidentes
     */
    @Test
    @WithMockUser(username = "test@uade.edu.ar", roles = {"USER"})
    void testGetAllIncidents_Success() throws Exception {
        // Arrange
        List<IncidentResponse> incidents = Arrays.asList(testIncidentResponse);
        when(incidentService.getAllIncidents(null, null, null, null)).thenReturn(incidents);

        // Act & Assert
        mockMvc.perform(get("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Incident"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    /**
     * Test 2: POST /api/incidents - Verificar que se puede crear un incidente
     */
    @Test
    @WithMockUser(username = "test@uade.edu.ar", roles = {"USER"})
    void testCreateIncident_Success() throws Exception {
        // Arrange
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setTitle("Nueva incidencia");
        request.setDescription("Descripción de prueba");
        request.setDepartmentId(1);
        request.setPriority(IncidentPriority.HIGH);

        when(incidentService.createIncident(any(CreateIncidentRequest.class)))
                .thenReturn(testIncidentResponse);

        // Act & Assert
        mockMvc.perform(post("/api/incidents")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Incident"));
    }

    /**
     * Test 3: GET /api/incidents/{id} - Verificar que retorna un incidente específico
     */
    @Test
    @WithMockUser(username = "test@uade.edu.ar", roles = {"USER"})
    void testGetIncidentById_Success() throws Exception {
        // Arrange
        when(incidentService.getIncidentById(1)).thenReturn(testIncidentResponse);

        // Act & Assert
        mockMvc.perform(get("/api/incidents/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Incident"));
    }

    /**
     * Test 4: GET /api/incidents/{id} - Verificar que retorna 404 si no existe
     */
    @Test
    @WithMockUser(username = "test@uade.edu.ar", roles = {"USER"})
    void testGetIncidentById_NotFound() throws Exception {
        // Arrange
        when(incidentService.getIncidentById(999))
                .thenThrow(new RuntimeException("Incidente no encontrado con ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/incidents/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test 5: PATCH /api/incidents/{id}/status - Verificar que actualiza el estado
     */
    @Test
    @WithMockUser(username = "test@uade.edu.ar", roles = {"WORKER"})
    void testUpdateStatus_Success() throws Exception {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest(IncidentStatus.IN_PROCESS);

        IncidentResponse updatedResponse = new IncidentResponse();
        updatedResponse.setId(1);
        updatedResponse.setTitle("Test Incident");
        updatedResponse.setStatus(IncidentStatus.IN_PROCESS);

        when(incidentService.updateStatus(eq(1), any(UpdateStatusRequest.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/incidents/1/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROCESS"));
    }

    /**
     * Test 6: POST /api/incidents - Verificar que retorna 400 si faltan datos
     */
    @Test
    @WithMockUser(username = "test@uade.edu.ar", roles = {"USER"})
    void testCreateIncident_BadRequest() throws Exception {
        // Arrange
        CreateIncidentRequest request = new CreateIncidentRequest();
        // No se establecen los campos requeridos

        when(incidentService.createIncident(any(CreateIncidentRequest.class)))
                .thenThrow(new RuntimeException("Datos inválidos"));

        // Act & Assert
        mockMvc.perform(post("/api/incidents")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
