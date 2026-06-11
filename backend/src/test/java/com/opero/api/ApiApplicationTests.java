package com.opero.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de carga del contexto de Spring.
 * Verifica que la aplicación puede iniciar correctamente en entorno de test.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApiApplicationTests {

	@Test
	void contextLoads() {
		// Test que verifica que el contexto de Spring se carga correctamente
		// Si el contexto falla al cargar, este test fallará
	}

}
