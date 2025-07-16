package org.jcr.generadorpreguntasjava.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.service.AuthenticationService;
import org.jcr.generadorpreguntasjava.infrastructure.security.service.CookieService;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final CookieService cookieService;
    @Value("${front.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.debug("Autenticación exitosa con OAuth2 para usuario: {}", authentication.getName());

        try {
            // Obtener o registrar usuario
            Usuario usuario = authenticationService.getOrRegisterOauth2User(authentication)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Generar tokens JWT
            AuthenticationService.AuthTokens tokens = authenticationService.generateTokens(usuario);

            // Configurar cookies con tokens
            cookieService.createAccessTokenCookie(response, tokens.accessToken());
            cookieService.createRefreshTokenCookie(response, tokens.refreshToken());

            // Redirigir a la URL de éxito del frontend
            response.sendRedirect(frontUrl+"/oauth2/success");
        } catch (Exception e) {
            log.error("Error al manejar el éxito de la autenticación OAuth2", e);
            response.sendRedirect("/api/error");
        }
    }
}

