package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Entidad JPA para sesiones de respuesta.
 * Rastrea el tiempo de respuesta y resultados de cada pregunta respondida por un usuario.
 */
@Entity
@Table(name = "sesiones_respuesta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionRespuestaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(name = "pregunta_id", nullable = false)
    private Long preguntaId;
    
    @Column(name = "respuesta_seleccionada", length = 500)
    private String respuestaSeleccionada;
    
    @Column(name = "es_correcta", nullable = false)
    private boolean esCorrecta;
    
    @Column(name = "inicio_respuesta", nullable = false)
    private LocalDateTime inicioRespuesta;
    
    @Column(name = "fin_respuesta")
    private LocalDateTime finRespuesta;
    
    @Column(name = "tiempo_respuesta_ms")
    private Long tiempoRespuestaMs;
    
    // Relaciones (lazy loading por defecto)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private UsuarioEntity usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", insertable = false, updatable = false)
    private PreguntaEntity pregunta;
    
    /**
     * Constructor para iniciar una sesión.
     */
    public SesionRespuestaEntity(Long usuarioId, Long preguntaId) {
        this.usuarioId = usuarioId;
        this.preguntaId = preguntaId;
        this.inicioRespuesta = LocalDateTime.now();
        this.esCorrecta = false;
    }
    
    /**
     * Constructor completo para sesión completada.
     */
    public SesionRespuestaEntity(Long usuarioId, Long preguntaId, String respuestaSeleccionada,
                               boolean esCorrecta, LocalDateTime inicioRespuesta, 
                               LocalDateTime finRespuesta, Long tiempoRespuestaMs) {
        this.usuarioId = usuarioId;
        this.preguntaId = preguntaId;
        this.respuestaSeleccionada = respuestaSeleccionada;
        this.esCorrecta = esCorrecta;
        this.inicioRespuesta = inicioRespuesta;
        this.finRespuesta = finRespuesta;
        this.tiempoRespuestaMs = tiempoRespuestaMs;
    }
    
    /**
     * Convierte el tiempo de respuesta de milisegundos a Duration.
     */
    public Duration getTiempoRespuestaDuration() {
        return tiempoRespuestaMs != null ? Duration.ofMillis(tiempoRespuestaMs) : null;
    }
    
    /**
     * Establece el tiempo de respuesta desde un Duration.
     */
    public void setTiempoRespuestaDuration(Duration tiempoRespuesta) {
        this.tiempoRespuestaMs = tiempoRespuesta != null ? tiempoRespuesta.toMillis() : null;
    }
    
    /**
     * Verifica si la sesión está completa.
     */
    public boolean estaCompleta() {
        return respuestaSeleccionada != null && finRespuesta != null && tiempoRespuestaMs != null;
    }
    
    /**
     * Verifica si la sesión está en progreso.
     */
    public boolean estaEnProgreso() {
        return inicioRespuesta != null && finRespuesta == null;
    }
    
    @PrePersist
    protected void onCreate() {
        if (inicioRespuesta == null) {
            inicioRespuesta = LocalDateTime.now();
        }
    }
}
