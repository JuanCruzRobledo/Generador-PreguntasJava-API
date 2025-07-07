package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para usuarios.
 * Preparada para integración con OAuth2 (Google).
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "google_id", nullable = false, unique = true, length = 100)
    private String googleId;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;
    
    @Column(name = "avatar", length = 500)
    private String avatar;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(name = "ultimo_acceso", nullable = false)
    private LocalDateTime ultimoAcceso;
    
    @Column(name = "activo", nullable = false)
    private boolean activo;
    
    /**
     * Constructor para crear un usuario con valores por defecto.
     */
    public UsuarioEntity(String googleId, String email, String nombre, String avatar) {
        this.googleId = googleId;
        this.email = email;
        this.nombre = nombre;
        this.avatar = avatar;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimoAcceso = LocalDateTime.now();
        this.activo = true;
    }
    
    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (ultimoAcceso == null) {
            ultimoAcceso = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        ultimoAcceso = LocalDateTime.now();
    }
}
