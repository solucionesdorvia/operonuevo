package com.opero.api.service;

import com.opero.api.dto.CreateIncidentRequest;
import com.opero.api.dto.UpdateStatusRequest;
import com.opero.api.entity.*;
import com.opero.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private IncidentHistoryRepository incidentHistoryRepository;

    @InjectMocks
    private IncidentService incidentService;

    private Incident mockIncident;
    private User mockReporter;
    private Department mockDepartment;

    @BeforeEach
    void setUp() {
        Role reporterRole = new Role();
        reporterRole.setName("STUDENT");

        mockReporter = new User();
        mockReporter.setId(1L);
        mockReporter.setEmail("reporter@example.com");
        mockReporter.setFullName("Reporter User");
        mockReporter.setRole(reporterRole);

        mockDepartment = new Department();
        mockDepartment.setId(1L);
        mockDepartment.setName("Mantenimiento");

        mockIncident = new Incident();
        mockIncident.setId(1L);
        mockIncident.setTitle("Test Incident");
        mockIncident.setDescription("Test Description");
        mockIncident.setStatus(IncidentStatus.PENDING);
        mockIncident.setPriority(Priority.MEDIUM);
        mockIncident.setReporter(mockReporter);
        mockIncident.setDepartment(mockDepartment);
    }

    @Test
    void getAllIncidents_DeberiaRetornarListaDeIncidentes() {
        List<Incident> incidents = Arrays.asList(mockIncident);
        when(incidentRepository.findAll()).thenReturn(incidents);

        var response = incidentService.getAllIncidents();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Incident", response.get(0).getTitle());
        verify(incidentRepository, times(1)).findAll();
    }

    @Test
    void getIncidentById_DeberiaRetornarIncidente_CuandoExiste() {
        when(incidentRepository.findById(anyLong())).thenReturn(Optional.of(mockIncident));

        var response = incidentService.getIncidentById(1L);

        assertNotNull(response);
        assertEquals("Test Incident", response.getTitle());
        assertEquals(1L, response.getId());
        verify(incidentRepository, times(1)).findById(1L);
    }

    @Test
    void getIncidentById_DeberiaLanzarException_CuandoNoExiste() {
        when(incidentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> incidentService.getIncidentById(999L));
        verify(incidentRepository, times(1)).findById(999L);
    }

    @Test
    void deleteIncident_DeberiaEliminarIncidente_CuandoExiste() {
        when(incidentRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(incidentRepository).deleteById(anyLong());

        incidentService.deleteIncident(1L);

        verify(incidentRepository, times(1)).existsById(1L);
        verify(incidentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteIncident_DeberiaLanzarException_CuandoNoExiste() {
        when(incidentRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> incidentService.deleteIncident(999L));
        verify(incidentRepository, times(1)).existsById(999L);
        verify(incidentRepository, never()).deleteById(anyLong());
    }
}
