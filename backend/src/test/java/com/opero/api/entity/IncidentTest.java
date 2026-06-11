package com.opero.api.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IncidentTest {

    @Test
    void testIncidentCreation() {
        Incident incident = new Incident();
        incident.setTitle("Test Incident");
        incident.setDescription("Test Description");
        incident.setLocationDescription("Test Location");
        incident.setStatus(IncidentStatus.PENDING);
        incident.setPriority(IncidentPriority.MEDIUM);

        assertEquals("Test Incident", incident.getTitle());
        assertEquals("Test Description", incident.getDescription());
        assertEquals("Test Location", incident.getLocationDescription());
        assertEquals(IncidentStatus.PENDING, incident.getStatus());
        assertEquals(IncidentPriority.MEDIUM, incident.getPriority());
    }

    @Test
    void testIncidentDefaultConstructor() {
        Incident incident = new Incident();
        assertNull(incident.getId());
        assertNull(incident.getTitle());
    }

    @Test
    void testIncidentStatusEnum() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.IN_PROCESS);

        assertEquals(IncidentStatus.IN_PROCESS, incident.getStatus());
    }

    @Test
    void testIncidentPriorityEnum() {
        Incident incident = new Incident();
        incident.setPriority(IncidentPriority.HIGH);

        assertEquals(IncidentPriority.HIGH, incident.getPriority());
    }
}
