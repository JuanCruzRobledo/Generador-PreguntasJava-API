package org.jcr.generadorpreguntasjava.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una sesión de respuesta a una pregunta.
 * Permite rastrear el tiempo de respuesta para estadísticas.
 */
public record SesionRespuesta(
    Long id,
    Long usuarioId,
    Long preguntaId,
    String respuestaSeleccionada,
    boolean esCorrecta,
    LocalDateTime inicioRespuesta,
    LocalDateTime finRespuesta,
    Duration tiempoRespuesta
) {
    
    /**
     * Constructor para iniciar una nueva sesión de respuesta.
     */
    public SesionRespuesta(Long usuarioId, Long preguntaId) {
        this(null, usuarioId, preguntaId, null, false, 
             LocalDateTime.now(), null, null);
    }
    
    /**
     * Constructor para crear una sesión sin ID (para persistir).
     */
    public SesionRespuesta(Long usuarioId, Long preguntaId, String respuestaSeleccionada,
                          boolean esCorrecta, LocalDateTime inicioRespuesta, 
                          LocalDateTime finRespuesta, Duration tiempoRespuesta) {
        this(null, usuarioId, preguntaId, respuestaSeleccionada, esCorrecta,
             inicioRespuesta, finRespuesta, tiempoRespuesta);
    }
    
    /**
     * Crea una copia de la sesión con un nuevo ID.
     */
    public SesionRespuesta withId(Long nuevoId) {
        return new SesionRespuesta(nuevoId, this.usuarioId, this.preguntaId,
                                  this.respuestaSeleccionada, this.esCorrecta,
                                  this.inicioRespuesta, this.finRespuesta, this.tiempoRespuesta);
    }
    
    /**
     * Completa la sesión de respuesta con la respuesta del usuario.
     */
    public SesionRespuesta completarRespuesta(String respuesta, boolean correcta) {
        LocalDateTime fin = LocalDateTime.now();
        Duration tiempo = Duration.between(this.inicioRespuesta, fin);
        
        return new SesionRespuesta(this.id, this.usuarioId, this.preguntaId,
                                  respuesta, correcta, this.inicioRespuesta, fin, tiempo);
    }
    
    /**
     * Verifica si la sesión está completa.
     */
    public boolean estaCompleta() {
        return respuestaSeleccionada != null && finRespuesta != null && tiempoRespuesta != null;
    }
    
    /**
     * Verifica si la sesión está en progreso.
     */
    public boolean estaEnProgreso() {
        return inicioRespuesta != null && finRespuesta == null;
    }
    
    /**
     * Obtiene el tiempo de respuesta en segundos.
     */
    public long getTiempoRespuestaSegundos() {
        return tiempoRespuesta != null ? tiempoRespuesta.getSeconds() : 0;
    }
    
    /**
     * Obtiene el tiempo de respuesta en milisegundos.
     */
    public long getTiempoRespuestaMillis() {
        return tiempoRespuesta != null ? tiempoRespuesta.toMillis() : 0;
    }
    
    /**
     * Valida que la sesión tenga los datos mínimos requeridos.
     */
    public void validar() {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        if (preguntaId == null) {
            throw new IllegalArgumentException("El ID de la pregunta no puede ser nulo");
        }
        
        if (inicioRespuesta == null) {
            throw new IllegalArgumentException("El tiempo de inicio no puede ser nulo");
        }
        
        if (estaCompleta()) {
            if (respuestaSeleccionada == null || respuestaSeleccionada.trim().isEmpty()) {
                throw new IllegalArgumentException("La respuesta seleccionada no puede estar vacía");
            }
            
            if (finRespuesta == null) {
                throw new IllegalArgumentException("El tiempo de fin no puede ser nulo para una sesión completa");
            }
            
            if (tiempoRespuesta == null || tiempoRespuesta.isNegative()) {
                throw new IllegalArgumentException("El tiempo de respuesta debe ser positivo");
            }
            
            if (finRespuesta.isBefore(inicioRespuesta)) {
                throw new IllegalArgumentException("El tiempo de fin no puede ser anterior al de inicio");
            }
        }
    }
    
    /**
     * Verifica si la respuesta fue dada dentro de un tiempo razonable.
     */
    public boolean esRespuestaValida() {
        if (!estaCompleta()) return false;
        
        // Tiempo mínimo: 5 segundos (evitar respuestas automáticas)
        // Tiempo máximo: 10 minutos (evitar sesiones abandonadas)
        long segundos = getTiempoRespuestaSegundos();
        return segundos >= 5 && segundos <= 600;
    }
}
