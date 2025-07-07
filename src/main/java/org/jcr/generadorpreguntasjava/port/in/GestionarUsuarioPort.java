package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para casos de uso de gestión de usuarios.
 * Define las operaciones disponibles para gestionar usuarios en el sistema.
 */
public interface GestionarUsuarioPort {
    
    /**
     * Crea un nuevo usuario o actualiza uno existente basado en datos de OAuth2.
     * 
     * @param googleId ID único de Google OAuth2
     * @param email Email del usuario
     * @param nombre Nombre del usuario
     * @param avatar URL del avatar del usuario
     * @return Usuario creado o actualizado
     * @throws RuntimeException si hay errores en la creación/actualización
     */
    Usuario crearOActualizarUsuario(String googleId, String email, String nombre, String avatar);
    
    /**
     * Busca un usuario por su Google ID.
     * 
     * @param googleId ID de Google OAuth2
     * @return Optional con el usuario si existe
     * @throws IllegalArgumentException si el googleId es null o vacío
     */
    Optional<Usuario> buscarPorGoogleId(String googleId);
    
    /**
     * Busca un usuario por su ID interno.
     * 
     * @param id ID interno del usuario
     * @return Optional con el usuario si existe
     * @throws IllegalArgumentException si el id es null
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Actualiza el último acceso de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Usuario con último acceso actualizado
     * @throws RuntimeException si el usuario no existe
     */
    Usuario actualizarUltimoAcceso(Long usuarioId);
    
    /**
     * Actualiza la información del perfil de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @param nuevoNombre Nuevo nombre del usuario
     * @param nuevoAvatar Nueva URL del avatar
     * @return Usuario con perfil actualizado
     * @throws RuntimeException si el usuario no existe
     */
    Usuario actualizarPerfil(Long usuarioId, String nuevoNombre, String nuevoAvatar);
    
    /**
     * Obtiene todos los usuarios activos del sistema.
     * 
     * @return Lista de usuarios activos
     */
    List<Usuario> obtenerUsuariosActivos();
    
    /**
     * Desactiva un usuario del sistema.
     * 
     * @param usuarioId ID del usuario a desactivar
     * @return true si se desactivó correctamente
     * @throws RuntimeException si el usuario no existe
     */
    boolean desactivarUsuario(Long usuarioId);
    
    /**
     * Activa un usuario previamente desactivado.
     * 
     * @param usuarioId ID del usuario a activar
     * @return true si se activó correctamente
     * @throws RuntimeException si el usuario no existe
     */
    boolean activarUsuario(Long usuarioId);
    
    /**
     * Verifica si un usuario puede usar el sistema.
     * 
     * @param usuarioId ID del usuario
     * @return true si el usuario puede usar el sistema
     * @throws RuntimeException si el usuario no existe
     */
    boolean puedeUsarSistema(Long usuarioId);
    
    /**
     * Obtiene estadísticas básicas de usuarios.
     */
    record EstadisticasUsuarios(
        long totalUsuarios,
        long usuariosActivos,
        long usuariosInactivos
    ) {}
    
    /**
     * Obtiene estadísticas generales de usuarios.
     * 
     * @return Estadísticas de usuarios
     */
    EstadisticasUsuarios obtenerEstadisticasUsuarios();

    /**
     * Obtiene el usuario actualmente autenticado.
     *
     * @return Usuario autenticado
     */
    Usuario obtenerUsuarioActual();

    /**
     * Obtiene un usuario Anonimo.
     *
     * @return Usuario autenticado
     */
    Usuario crearUsuarioAnonimo();
}
