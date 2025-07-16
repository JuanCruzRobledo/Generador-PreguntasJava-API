package org.jcr.generadorpreguntasjava.port.in.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO record para request de login.
 * Contiene las credenciales del usuario.
 */
public record LoginRequest(
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    String email,
    
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password
) {
    
    /**
     * Validación adicional para el record.
     */
    public LoginRequest {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}
