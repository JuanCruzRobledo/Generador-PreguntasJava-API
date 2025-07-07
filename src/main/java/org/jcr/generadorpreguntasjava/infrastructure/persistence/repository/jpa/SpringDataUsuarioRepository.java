package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para usuarios.
 */
@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    
    /**
     * Busca un usuario por su Google ID.
     */
    Optional<UsuarioEntity> findByGoogleId(String googleId);
    
    /**
     * Busca un usuario por su email.
     */
    Optional<UsuarioEntity> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el Google ID dado.
     */
    boolean existsByGoogleId(String googleId);
    
    /**
     * Verifica si existe un usuario con el email dado.
     */
    boolean existsByEmail(String email);
    
    /**
     * Obtiene todos los usuarios activos.
     */
    List<UsuarioEntity> findByActivoTrue();
    
    /**
     * Obtiene todos los usuarios inactivos.
     */
    List<UsuarioEntity> findByActivoFalse();
    
    /**
     * Cuenta el total de usuarios activos.
     */
    long countByActivoTrue();
    
    /**
     * Cuenta el total de usuarios inactivos.
     */
    long countByActivoFalse();
    
    /**
     * Desactiva un usuario por ID.
     */
    @Modifying
    @Query("UPDATE UsuarioEntity u SET u.activo = false WHERE u.id = :id")
    int desactivarUsuario(@Param("id") Long id);
    
    /**
     * Activa un usuario por ID.
     */
    @Modifying
    @Query("UPDATE UsuarioEntity u SET u.activo = true WHERE u.id = :id")
    int activarUsuario(@Param("id") Long id);
    
    /**
     * Busca usuarios por parte del nombre o email.
     */
    @Query("SELECT u FROM UsuarioEntity u WHERE " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<UsuarioEntity> buscarPorNombreOEmail(@Param("termino") String termino);
    
    /**
     * Obtiene usuarios ordenados por fecha de registro (más recientes primero).
     */
    List<UsuarioEntity> findByActivoTrueOrderByFechaRegistroDesc();
    
    /**
     * Obtiene usuarios ordenados por último acceso (más recientes primero).
     */
    List<UsuarioEntity> findByActivoTrueOrderByUltimoAccesoDesc();
}
