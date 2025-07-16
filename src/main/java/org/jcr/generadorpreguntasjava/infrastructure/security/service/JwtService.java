package org.jcr.generadorpreguntasjava.infrastructure.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.infrastructure.security.config.JwtProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para manejo de tokens JWT.
 * Implementa la lógica de generación, validación y extracción de información de tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtProperties jwtProperties;
    
    /**
     * Genera un access token JWT para el usuario.
     * 
     * @param userId ID del usuario
     * @param email Email del usuario
     * @param nombre Nombre del usuario
     * @return Token JWT generado
     */
    public String generateAccessToken(Long userId, String email, String nombre) {
        log.debug("Generando access token para usuario: {}", email);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("nombre", nombre);
        claims.put("tokenType", "access");
        
        return createToken(claims, email, jwtProperties.getAccessToken().getExpiration());
    }
    
    /**
     * Genera un refresh token JWT para el usuario.
     * 
     * @param userId ID del usuario
     * @param email Email del usuario
     * @return Refresh token JWT generado
     */
    public String generateRefreshToken(Long userId, String email) {
        log.debug("Generando refresh token para usuario: {}", email);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("tokenType", "refresh");
        
        return createToken(claims, email, jwtProperties.getRefreshToken().getExpiration());
    }
    
    /**
     * Crea un token JWT con los claims especificados.
     * 
     * @param claims Claims a incluir en el token
     * @param subject Sujeto del token
     * @param expiration Tiempo de expiración en milisegundos
     * @return Token JWT generado
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extrae el email (subject) del token.
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrae el ID del usuario del token.
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    
    /**
     * Extrae el nombre del usuario del token.
     * 
     * @param token Token JWT
     * @return Nombre del usuario
     */
    public String extractNombre(String token) {
        return extractClaim(token, claims -> claims.get("nombre", String.class));
    }
    
    /**
     * Extrae el tipo de token.
     * 
     * @param token Token JWT
     * @return Tipo de token (access o refresh)
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }
    
    /**
     * Extrae la fecha de expiración del token.
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extrae un claim específico del token.
     * 
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim
     * @return Valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extrae todos los claims del token.
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        try {
            Jws<Claims> jwt = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return jwt.getPayload();
        } catch (JwtException e) {
            log.error("Error al extraer claims del token: {}", e.getMessage());
            throw new RuntimeException("Token inválido", e);
        }
    }
    
    /**
     * Valida si el token ha expirado.
     * 
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error al verificar expiración del token: {}", e.getMessage());
            return true; // Si hay error, consideramos el token como expirado
        }
    }
    
    /**
     * Valida si el token es válido para el usuario especificado.
     * 
     * @param token Token JWT
     * @param email Email del usuario
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida si el token es un access token válido.
     * 
     * @param token Token JWT
     * @return true si es un access token válido, false en caso contrario
     */
    public boolean isValidAccessToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "access".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error al validar access token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida si el token es un refresh token válido.
     * 
     * @param token Token JWT
     * @return true si es un refresh token válido, false en caso contrario
     */
    public boolean isValidRefreshToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "refresh".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error al validar refresh token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene la clave de firma para los tokens.
     * 
     * @return Clave secreta para firmar tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
