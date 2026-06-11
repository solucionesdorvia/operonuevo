package com.opero.api.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setFullName("Test User");
        user.setEmailUade("test@uade.edu.ar");
        user.setPasswordHash("hashedpassword");

        assertEquals("Test User", user.getFullName());
        assertEquals("test@uade.edu.ar", user.getEmailUade());
        assertEquals("hashedpassword", user.getPasswordHash());
    }

    @Test
    void testUserDefaultConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getFullName());
    }

    @Test
    void testUserSettersAndGetters() {
        User user = new User();
        user.setFullName("John Doe");

        assertEquals("John Doe", user.getFullName());
    }
}
