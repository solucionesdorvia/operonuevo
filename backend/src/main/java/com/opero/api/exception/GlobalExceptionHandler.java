package com.opero.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 *
 * ¿Por qué existe?
 * Antes no había ningún @ControllerAdvice, así que cualquier error inesperado
 * (por ejemplo, un problema de conexión con la base de datos) salía como un 500
 * crudo de Spring, sin log útil y con un cuerpo que el frontend interpretaba
 * como "Error de servidor". Esto centraliza el manejo:
 *
 * - Devuelve SIEMPRE JSON con la forma { "error": ..., "message": ... } que ya
 *   entiende el frontend (getErrorMessage lee data.error / data.message).
 * - Loguea el stack trace completo en Railway para poder diagnosticar el 500.
 * - Distingue errores de base de datos (503, reintentables) de errores genéricos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Errores de acceso a datos (conexión perdida con Postgres, timeout de query, etc.).
     * Se marcan como 503 Service Unavailable porque suelen ser transitorios: el
     * cliente puede reintentar y normalmente el segundo intento funciona.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccess(DataAccessException ex) {
        log.error("[GlobalException] Error de acceso a datos", ex);
        Map<String, String> body = new HashMap<>();
        body.put("error", "Servicio temporalmente no disponible");
        body.put("message", "No se pudo acceder a la base de datos. Reintentá en unos segundos.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    /**
     * Cualquier otra excepción no controlada -> 500 con JSON limpio y log completo.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        log.error("[GlobalException] Error inesperado", ex);
        Map<String, String> body = new HashMap<>();
        body.put("error", "Error interno del servidor");
        body.put("message", ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
