package com.opero.api.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void testLocalDateTimeCreation() {
        LocalDateTime now = LocalDateTime.now();
        assertNotNull(now);
        assertTrue(now.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testLocalDateTimeComparison() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        assertTrue(past.isBefore(future));
    }

    @Test
    void testLocalDateTimeYear() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(2026, now.getYear());
    }
}
