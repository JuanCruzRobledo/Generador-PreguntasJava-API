package org.jcr.generadorpreguntasjava.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Manejador de errores de autenticación OAuth2.
 * Redirige al usuario al frontend con información del error.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${front.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        
        log.warn("Fallo en autenticación OAuth2: {}", exception.getMessage());
        
        try {
            // Crear mensaje de error codificado para URL
            String errorMessage = exception.getMessage() != null 
                ? exception.getMessage() 
                : "Error en autenticación OAuth2";
            
            String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            
            // Redirigir al frontend con el error
            String redirectUrl = String.format(
                    frontUrl+ "/oauth2/error?error=%s",
                encodedError
            );
            
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            log.error("Error al manejar fallo de OAuth2", e);
            response.sendRedirect( frontUrl+"/oauth2/error?error=unknown_error");
        }
    }
}
