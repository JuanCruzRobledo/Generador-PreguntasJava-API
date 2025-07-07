package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para estadísticas de usuario.
 * Almacena estadísticas agregadas y calculadas de cada usuario.
 */
@Entity
@Table(name = "estadisticas_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasUsuarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;
    
    @Column(name = "total_preguntas", nullable = false)
    private int totalPreguntas;
    
    @Column(name = "respuestas_correctas", nullable = false)
    private int respuestasCorrectas;
    
    @Column(name = "porcentaje_aciertos", nullable = false)
    private double porcentajeAciertos;
    
    @Column(name = "tiempo_promedio_ms")
    private Long tiempoPromedioMs;
    
    // Estadísticas por dificultad almacenadas como JSON
    @Column(name = "estadisticas_por_dificultad", columnDefinition = "TEXT")
    private String estadisticasPorDificultadJson;
    
    // Estadísticas por temática almacenadas como JSON
    @Column(name = "estadisticas_por_tematica", columnDefinition = "TEXT")
    private String estadisticasPorTematicaJson;
    
    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;
    
    // Relación con usuario (lazy loading)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private UsuarioEntity usuario;
    
    /**
     * Constructor para crear estadísticas básicas.
     */
    public EstadisticasUsuarioEntity(Long usuarioId, int totalPreguntas, int respuestasCorrectas,
                                   double porcentajeAciertos, Long tiempoPromedioMs) {
        this.usuarioId = usuarioId;
        this.totalPreguntas = totalPreguntas;
        this.respuestasCorrectas = respuestasCorrectas;
        this.porcentajeAciertos = porcentajeAciertos;
        this.tiempoPromedioMs = tiempoPromedioMs;
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Constructor completo con JSON.
     */
    public EstadisticasUsuarioEntity(Long usuarioId, int totalPreguntas, int respuestasCorrectas,
                                   double porcentajeAciertos, Long tiempoPromedioMs,
                                   String estadisticasPorDificultadJson, String estadisticasPorTematicaJson) {
        this.usuarioId = usuarioId;
        this.totalPreguntas = totalPreguntas;
        this.respuestasCorrectas = respuestasCorrectas;
        this.porcentajeAciertos = porcentajeAciertos;
        this.tiempoPromedioMs = tiempoPromedioMs;
        this.estadisticasPorDificultadJson = estadisticasPorDificultadJson;
        this.estadisticasPorTematicaJson = estadisticasPorTematicaJson;
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Verifica si las estadísticas están actualizadas (menos de 1 hora).
     */
    public boolean estanActualizadas() {
        if (ultimaActualizacion == null) return false;
        return ultimaActualizacion.isAfter(LocalDateTime.now().minusHours(1));
    }
    
    /**
     * Verifica si tiene estadísticas registradas.
     */
    public boolean tieneEstadisticas() {
        return totalPreguntas > 0;
    }
    
    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (ultimaActualizacion == null) {
            ultimaActualizacion = LocalDateTime.now();
        }
    }
}
