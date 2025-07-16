package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

/**
 * DTO record para respuesta de autenticación.
 * Contiene información del usuario autenticado.
 */
public record AuthResponse(
    Long id,
    String email,
    String nombre,
    String avatar,
    String message
) {
    
    /**
     * Crea una respuesta de autenticación exitosa.
     */
    public static AuthResponse success(Long userId, String email, String nombre, String avatar) {
        return new AuthResponse(userId, email, nombre, avatar, "Autenticación exitosa");
    }
    
    /**
     * Crea una respuesta de error de autenticación.
     */
    public static AuthResponse error(String message) {
        return new AuthResponse(null, null, null, null, message);
    }
}
