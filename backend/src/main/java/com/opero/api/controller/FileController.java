package com.opero.api.controller;

import com.opero.api.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para manejo de archivos (upload de imágenes).
 *
 * ¿Qué hace este Controller?
 * - Expone endpoints para subir imágenes a Cloudinary
 * - Valida que los archivos sean imágenes
 * - Retorna URLs públicas de las imágenes
 *
 * Cambios vs versión anterior:
 * - ANTES: Guardaba imágenes en filesystem local (no persistente en Railway)
 * - AHORA: Guarda imágenes en Cloudinary (persistente, con CDN)
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "File Management", description = "Endpoints para gestión de archivos")
public class FileController {

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Upload de imagen a Cloudinary.
     *
     * ¿Qué hace este endpoint?
     * - Recibe un archivo de imagen desde el frontend
     * - Delega la validación y subida a CloudinaryService
     * - Retorna la URL pública de Cloudinary
     *
     * @param file Archivo de imagen (desde FormData del frontend)
     * @return JSON con {url: "https://...", filename: "..."}
     */
    @PostMapping("/upload")
    @Operation(
            summary = "Subir imagen a Cloudinary",
            description = "Sube una imagen a Cloudinary y retorna la URL pública. " +
                          "La imagen se almacena de forma persistente en la nube, " +
                          "a diferencia del filesystem local que es efímero en Railway."
    )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Subir a Cloudinary (valida formato y tamaño internamente)
            String imageUrl = cloudinaryService.uploadImage(file);

            // Extraer el nombre del archivo de la URL de Cloudinary
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // Retornar respuesta
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("filename", filename);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Error de validación (no es imagen, tamaño excedido)
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } catch (IOException e) {
            // Error al subir a Cloudinary
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * NOTA: Endpoint GET /{filename} eliminado.
     *
     * Ya no es necesario servir imágenes desde el servidor porque:
     * - Cloudinary proporciona URLs directas con CDN global
     * - El frontend carga las imágenes directamente desde Cloudinary
     * - Ejemplo: https://res.cloudinary.com/demo/image/upload/v1234/opero/incidents/abc.jpg
     */
}
