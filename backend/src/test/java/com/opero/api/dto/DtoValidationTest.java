package com.opero.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    @Test
    void testLoginRequestCreation() {
        LoginRequest request = new LoginRequest();
        request.setEmailUade("test@uade.edu.ar");
        request.setPassword("password123");

        assertEquals("test@uade.edu.ar", request.getEmailUade());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testRegisterRequestCreation() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmailUade("test@uade.edu.ar");
        request.setPassword("password123");
        request.setRoleId(1);

        assertEquals("Test User", request.getFullName());
        assertEquals("test@uade.edu.ar", request.getEmailUade());
        assertEquals("password123", request.getPassword());
        assertEquals(1, request.getRoleId());
    }

    @Test
    void testUserResponseCreation() {
        UserResponse response = new UserResponse();
        response.setId(1);
        response.setFullName("Test User");
        response.setEmailUade("test@uade.edu.ar");
        response.setRoleId(1);
        response.setRoleName("STUDENT");

        assertEquals(1, response.getId());
        assertEquals("Test User", response.getFullName());
        assertEquals("test@uade.edu.ar", response.getEmailUade());
        assertEquals(1, response.getRoleId());
        assertEquals("STUDENT", response.getRoleName());
    }

    @Test
    void testIncidentResponseCreation() {
        IncidentResponse response = new IncidentResponse();
        response.setId(1);
        response.setTitle("Test Incident");
        response.setDescription("Test Description");

        assertEquals(1, response.getId());
        assertEquals("Test Incident", response.getTitle());
        assertEquals("Test Description", response.getDescription());
    }
}
