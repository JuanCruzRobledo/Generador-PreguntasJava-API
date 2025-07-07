package org.jcr.generadorpreguntasjava.domain.model;

import java.time.Duration;

/**
 * Record que representa las estadísticas de un usuario para una temática específica.
 */
public record EstadisticasPorTematica(
    String tematica,
    int totalPreguntas,
    int respuestasCorrectas,
    double porcentajeAciertos,
    Duration tiempoPromedio
) {
    
    /**
     * Constructor que calcula automáticamente el porcentaje.
     */
    public EstadisticasPorTematica(String tematica, int totalPreguntas, 
                                 int respuestasCorrectas, Duration tiempoPromedio) {
        this(tematica, totalPreguntas, respuestasCorrectas,
             totalPreguntas > 0 ? (double) respuestasCorrectas / totalPreguntas * 100.0 : 0.0,
             tiempoPromedio);
    }
    
    /**
     * Crea estadísticas vacías para una temática.
     */
    public static EstadisticasPorTematica vacia(String tematica) {
        return new EstadisticasPorTematica(tematica, 0, 0, 0.0, Duration.ZERO);
    }
    
    /**
     * Verifica si hay datos para esta temática.
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
     * Verifica si el rendimiento en esta temática es bueno (>= 70%).
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
    
    /**
     * Compara dos estadísticas por temática por porcentaje de aciertos.
     */
    public int compararPorRendimiento(EstadisticasPorTematica otra) {
        return Double.compare(this.porcentajeAciertos, otra.porcentajeAciertos);
    }
    
    /**
     * Verifica si esta temática es la favorita del usuario (más de 5 preguntas y buen rendimiento).
     */
    public boolean esTematicaFavorita() {
        return totalPreguntas >= 5 && esBuenRendimiento();
    }
}
