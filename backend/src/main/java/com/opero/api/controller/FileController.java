package com.opero.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para manejo de archivos (upload de imágenes).
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "File Management", description = "Endpoints para gestión de archivos")
public class FileController {

    // Directorio donde se guardarán las imágenes
    private static final String UPLOAD_DIR = "uploads/images/";

    @Value("${app.base-url:https://backend-opero-production.up.railway.app}")
    private String baseUrl;

    /**
     * Upload de imagen.
     *
     * @param file Archivo de imagen
     * @return URL pública de la imagen subida
     */
    @PostMapping("/upload")
    @Operation(
            summary = "Subir imagen",
            description = "Sube una imagen al servidor y retorna la URL pública de acceso"
    )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validar que sea una imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El archivo debe ser una imagen");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validar tamaño (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La imagen no puede superar los 10MB");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Crear directorio si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Guardar archivo
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Construir URL pública
            String fileUrl = baseUrl + "/api/files/" + filename;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("filename", filename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al guardar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Servir imagen.
     *
     * @param filename Nombre del archivo
     * @return Bytes de la imagen
     */
    @GetMapping("/{filename}")
    @Operation(
            summary = "Obtener imagen",
            description = "Retorna una imagen previamente subida"
    )
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);

            // Determinar content type basado en extensión
            String contentType = "image/jpeg";
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (filename.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
