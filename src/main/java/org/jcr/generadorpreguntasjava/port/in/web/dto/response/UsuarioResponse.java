package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para usuarios.
 */
public record UsuarioResponse(
    Long id,
    String googleId,
    String email,
    String nombre,
    String avatar,
    LocalDateTime fechaRegistro,
    LocalDateTime ultimoAcceso,
    boolean activo,
    String nombreParaMostrar
) {}
