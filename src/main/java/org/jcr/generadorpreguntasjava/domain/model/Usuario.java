package org.jcr.generadorpreguntasjava.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad de dominio que representa un usuario del sistema.
 * Preparada para integración con OAuth2 (Google).
 */
public record Usuario(
    Long id,
    String googleId,           // ID único de Google OAuth2
    String email,
    String nombre,
    String avatar,
    LocalDateTime fechaRegistro,
    LocalDateTime ultimoAcceso,
    boolean activo
) {
    
    /**
     * Constructor para crear un nuevo usuario sin ID (para persistir).
     */
    public Usuario(String googleId, String email, String nombre, String avatar) {
        this(null, googleId, email, nombre, avatar, 
             LocalDateTime.now(), LocalDateTime.now(), true);
    }
    
    /**
     * Crea una copia del usuario con un nuevo ID.
     */
    public Usuario withId(Long nuevoId) {
        return new Usuario(nuevoId, this.googleId, this.email, this.nombre, 
                          this.avatar, this.fechaRegistro, this.ultimoAcceso, this.activo);
    }
    
    /**
     * Actualiza el último acceso del usuario.
     */
    public Usuario actualizarUltimoAcceso() {
        return new Usuario(this.id, this.googleId, this.email, this.nombre,
                          this.avatar, this.fechaRegistro, LocalDateTime.now(), this.activo);
    }
    
    /**
     * Actualiza la información del perfil del usuario.
     */
    public Usuario actualizarPerfil(String nuevoNombre, String nuevoAvatar) {
        return new Usuario(this.id, this.googleId, this.email, nuevoNombre,
                          nuevoAvatar, this.fechaRegistro, this.ultimoAcceso, this.activo);
    }
    
    /**
     * Actualiza solo el avatar del usuario.
     */
    public Usuario actualizarAvatar(String nuevoAvatar) {
        return new Usuario(this.id, this.googleId, this.email, this.nombre,
                          nuevoAvatar, this.fechaRegistro, this.ultimoAcceso, this.activo);
    }
    
    /**
     * Crea un nuevo usuario OAuth2 con información básica.
     * Este método es usado cuando un usuario se registra por primera vez con OAuth2.
     */
    public static Usuario crearUsuarioOAuth2(String email, String nombre, String avatar) {
        return new Usuario(
            null,              // ID será asignado por la base de datos
            null,              // googleId será asignado después
            email,
            nombre,
            avatar,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );
    }
    
    /**
     * Crea un nuevo usuario OAuth2 con Google ID.
     */
    public static Usuario crearUsuarioOAuth2ConGoogleId(String googleId, String email, String nombre, String avatar) {
        return new Usuario(
            null,              // ID será asignado por la base de datos
            googleId,
            email,
            nombre,
            avatar,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );
    }
    
    /**
     * Valida que el usuario tenga los datos mínimos requeridos.
     * Para usuarios OAuth2, el googleId puede ser null inicialmente.
     */
    public void validar() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        if (fechaRegistro == null) {
            throw new IllegalArgumentException("La fecha de registro no puede ser nula");
        }
        
        if (ultimoAcceso == null) {
            throw new IllegalArgumentException("El último acceso no puede ser nulo");
        }
        
        // Validar formato de email básico
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
    }
    
    /**
     * Obtiene el nombre de usuario para mostrar (nombre o email).
     */
    public String getNombreParaMostrar() {
        return nombre != null && !nombre.trim().isEmpty() ? nombre : email;
    }
    
    /**
     * Verifica si el usuario está activo y puede usar el sistema.
     */
    public boolean puedeUsarSistema() {
        return activo;
    }
}
