package org.jcr.generadorpreguntasjava.infrastructure.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.service.AuthenticationService;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.infrastructure.security.service.CookieService;
import org.jcr.generadorpreguntasjava.infrastructure.security.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro JWT para Spring Security.
 * Intercepta las peticiones HTTP y valida los tokens JWT desde cookies.
 * Configura el contexto de seguridad para peticiones autenticadas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final AuthenticationService authenticationService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Omitir filtro para rutas de OAuth2
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/oauth2/") || requestURI.startsWith("/login/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Obtener access token desde cookies
            String accessToken = cookieService.getAccessTokenFromCookies(request)
                .orElse(null);
            
            // Si no hay token, continuar con la cadena de filtros
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Validar token y extraer información del usuario
            if (jwtService.isValidAccessToken(accessToken)) {
                String email = jwtService.extractEmail(accessToken);
                
                // Verificar que no haya ya una autenticación en el contexto
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        // Obtener usuario desde el token
                        Usuario usuario = authenticationService.getUserFromToken(accessToken);
                        
                        // Crear principal personalizado
                        UserDetails userDetails = new JwtUserDetails(usuario);
                        
                        // Crear token de autenticación de Spring Security
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Configurar contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        log.debug("Usuario autenticado: {}", email);
                        
                    } catch (Exception e) {
                        log.warn("Error al autenticar usuario desde token: {}", e.getMessage());
                        SecurityContextHolder.clearContext();
                    }
                }
            } else {
                log.debug("Token inválido o expirado");
            }
            
        } catch (Exception e) {
            log.error("Error en filtro JWT: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
    
    /**
     * Implementación personalizada de UserDetails para JWT.
     * Encapsula la información del usuario para Spring Security.
     */
    private static class JwtUserDetails implements UserDetails {
        
        private final Usuario usuario;
        
        public JwtUserDetails(Usuario usuario) {
            this.usuario = usuario;
        }
        
        @Override
        public String getUsername() {
            return usuario.email();
        }
        
        @Override
        public String getPassword() {
            return null; // No almacenamos contraseñas en JWT
        }
        
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return usuario.puedeUsarSistema();
        }
        
        @Override
        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
            // Por ahora, todos los usuarios tienen los mismos permisos
            // En una implementación más completa, esto vendría del dominio
            return new ArrayList<>();
        }
        
        /**
         * Obtiene el usuario del dominio.
         * 
         * @return Usuario del dominio
         */
        public Usuario getUsuario() {
            return usuario;
        }
    }
}
