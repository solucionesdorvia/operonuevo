package com.opero.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service (Servicio) de Gestión de Archivos en Cloudinary.
 *
 * ¿Qué hace este Service?
 * - Sube imágenes a Cloudinary (servicio de almacenamiento en la nube)
 * - Retorna URLs públicas de las imágenes subidas
 * - Valida que los archivos sean imágenes
 * - Valida el tamaño máximo de las imágenes
 *
 * ¿Por qué Cloudinary?
 * - Railway tiene filesystem efímero (se borra al hacer redeploy)
 * - Cloudinary almacena las imágenes de forma persistente
 * - Proporciona CDN global para carga rápida de imágenes
 * - Plan gratuito suficiente para el proyecto
 */
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Tamaño máximo: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * Constructor que inicializa Cloudinary con las credenciales.
     *
     * Las credenciales se obtienen de application.properties:
     * - cloudinary.cloud-name
     * - cloudinary.api-key
     * - cloudinary.api-secret
     *
     * @param cloudName Nombre de tu cuenta en Cloudinary
     * @param apiKey API Key de Cloudinary
     * @param apiSecret API Secret de Cloudinary
     */
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    /**
     * Sube una imagen a Cloudinary.
     *
     * ¿Qué hace este método?
     * - Valida que el archivo sea una imagen
     * - Valida que no supere el tamaño máximo
     * - Sube el archivo a Cloudinary
     * - Retorna la URL pública de la imagen
     *
     * @param file Archivo de imagen a subir
     * @return URL pública de la imagen en Cloudinary
     * @throws IOException Si hay error al leer el archivo o al subirlo
     * @throws IllegalArgumentException Si el archivo no es válido
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // 1. Validar que sea una imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        // 2. Validar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("La imagen no puede superar los 10MB");
        }

        // 3. Subir a Cloudinary
        // La carpeta "opero/incidents" organiza las imágenes en Cloudinary
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "opero/incidents",
                        "resource_type", "image"
                )
        );

        // 4. Obtener y retornar la URL pública
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Elimina una imagen de Cloudinary (opcional, para futuras mejoras).
     *
     * @param publicId ID público de la imagen en Cloudinary
     * @throws IOException Si hay error al eliminar
     */
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
