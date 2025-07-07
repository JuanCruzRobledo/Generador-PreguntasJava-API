package org.jcr.generadorpreguntasjava.domain.model;

import java.time.Duration;

/**
 * Record que representa las estadísticas de un usuario para una dificultad específica.
 */
public record EstadisticasPorDificultad(
    Dificultad dificultad,
    int totalPreguntas,
    int respuestasCorrectas,
    double porcentajeAciertos,
    Duration tiempoPromedio
) {
    
    /**
     * Constructor que calcula automáticamente el porcentaje.
     */
    public EstadisticasPorDificultad(Dificultad dificultad, int totalPreguntas, 
                                   int respuestasCorrectas, Duration tiempoPromedio) {
        this(dificultad, totalPreguntas, respuestasCorrectas,
             totalPreguntas > 0 ? (double) respuestasCorrectas / totalPreguntas * 100.0 : 0.0,
             tiempoPromedio);
    }
    
    /**
     * Crea estadísticas vacías para una dificultad.
     */
    public static EstadisticasPorDificultad vacia(Dificultad dificultad) {
        return new EstadisticasPorDificultad(dificultad, 0, 0, 0.0, Duration.ZERO);
    }
    
    /**
     * Verifica si hay datos para esta dificultad.
     */
    public boolean tieneDatos() {
        return totalPreguntas > 0;
    }
    
    /**
     * Obtiene el número de respuestas incorrectas.
     */
    public int respuestasIncorrectas() {
        return totalPreguntas - respuestasCorrectas;
    }
    
    /**
     * Verifica si el rendimiento en esta dificultad es bueno (>= 70%).
     */
    public boolean esBuenRendimiento() {
        return porcentajeAciertos >= 70.0;
    }
    
    /**
     * Obtiene el tiempo promedio en segundos.
     */
    public long getTiempoPromedioSegundos() {
        return tiempoPromedio != null ? tiempoPromedio.getSeconds() : 0;
    }
    
    /**
     * Obtiene el tiempo promedio formateado.
     */
    public String getTiempoPromedioFormateado() {
        if (tiempoPromedio == null || tiempoPromedio.isZero()) {
            return "0s";
        }
        
        long segundos = tiempoPromedio.getSeconds();
        if (segundos < 60) {
            return segundos + "s";
        } else {
            long minutos = segundos / 60;
            long segRestantes = segundos % 60;
            return minutos + "m " + segRestantes + "s";
        }
    }
}
