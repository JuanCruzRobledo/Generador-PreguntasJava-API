package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para usuarios.
 */
public record UsuarioResponse(
    Long id,
    
    @JsonProperty("googleId")
    String googleId,
    
    String email,
    
    String nombre,
    
    String avatar,
    
    @JsonProperty("fechaRegistro")
    LocalDateTime fechaRegistro,
    
    @JsonProperty("ultimoAcceso")
    LocalDateTime ultimoAcceso,
    
    boolean activo,
    
    @JsonProperty("nombreParaMostrar")
    String nombreParaMostrar
) {}
