package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de usuarios.
 * Define el contrato para operaciones de persistencia de usuarios.
 */
public interface UsuarioRepositoryPort {
    
    /**
     * Guarda un usuario nuevo o actualiza uno existente.
     * 
     * @param usuario Usuario a guardar
     * @return Usuario guardado con ID asignado
     */
    Usuario guardar(Usuario usuario);
    
    /**
     * Busca un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Busca un usuario por su Google ID.
     * 
     * @param googleId ID de Google OAuth2
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorGoogleId(String googleId);
    
    /**
     * Busca un usuario por su email.
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorEmail(String email);
    
    /**
     * Obtiene todos los usuarios activos.
     * 
     * @return Lista de usuarios activos
     */
    List<Usuario> obtenerUsuariosActivos();
    
    /**
     * Obtiene todos los usuarios.
     * 
     * @return Lista de todos los usuarios
     */
    List<Usuario> obtenerTodos();
    
    /**
     * Verifica si existe un usuario con el Google ID dado.
     * 
     * @param googleId ID de Google OAuth2
     * @return true si existe el usuario
     */
    boolean existePorGoogleId(String googleId);
    
    /**
     * Verifica si existe un usuario con el email dado.
     * 
     * @param email Email del usuario
     * @return true si existe el usuario
     */
    boolean existePorEmail(String email);
    
    /**
     * Cuenta el total de usuarios registrados.
     * 
     * @return Número total de usuarios
     */
    long contarUsuarios();
    
    /**
     * Cuenta el total de usuarios activos.
     * 
     * @return Número de usuarios activos
     */
    long contarUsuariosActivos();
    
    /**
     * Desactiva un usuario (soft delete).
     * 
     * @param id ID del usuario a desactivar
     * @return true si se desactivó correctamente
     */
    boolean desactivarUsuario(Long id);
    
    /**
     * Activa un usuario previamente desactivado.
     * 
     * @param id ID del usuario a activar
     * @return true si se activó correctamente
     */
    boolean activarUsuario(Long id);
}
