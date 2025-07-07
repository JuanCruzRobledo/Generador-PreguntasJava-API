package org.jcr.generadorpreguntasjava.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad de dominio que representa las estadísticas agregadas de un usuario.
 * Estas estadísticas se calculan en base a las sesiones de respuesta.
 */
public record EstadisticasUsuario(
    Long usuarioId,
    int totalPreguntas,
    int respuestasCorrectas,
    double porcentajeAciertos,
    Duration tiempoPromedio,
    Map<Dificultad, EstadisticasPorDificultad> porDificultad,
    Map<String, EstadisticasPorTematica> porTematica,
    LocalDateTime ultimaActualizacion
) {
    public EstadisticasUsuario {
        if (porDificultad == null) porDificultad = Map.of();
        if (porTematica == null) porTematica = Map.of();
        if (tiempoPromedio == null) tiempoPromedio = Duration.ZERO;
        if (ultimaActualizacion == null) ultimaActualizacion = LocalDateTime.now();
    }

    /**
     * Constructor para crear estadísticas vacías para un usuario nuevo.
     */
    public EstadisticasUsuario(Long usuarioId) {
        this(usuarioId, 0, 0, 0.0, Duration.ZERO, 
             Map.of(), Map.of(), LocalDateTime.now());
    }
    
    /**
     * Constructor sin ID (para persistir).
     */
    public EstadisticasUsuario(Long usuarioId, int totalPreguntas, int respuestasCorrectas,
                              double porcentajeAciertos, Duration tiempoPromedio,
                              Map<Dificultad, EstadisticasPorDificultad> porDificultad,
                              Map<String, EstadisticasPorTematica> porTematica) {
        this(usuarioId, totalPreguntas, respuestasCorrectas, porcentajeAciertos,
             tiempoPromedio, porDificultad, porTematica, LocalDateTime.now());
    }
    
    /**
     * Calcula el porcentaje de aciertos.
     */
    public double calcularPorcentajeAciertos() {
        return totalPreguntas > 0 ? (double) respuestasCorrectas / totalPreguntas * 100.0 : 0.0;
    }
    
    /**
     * Verifica si el usuario tiene estadísticas registradas.
     */
    public boolean tieneEstadisticas() {
        return totalPreguntas > 0;
    }
    
    /**
     * Verifica si las estadísticas están actualizadas (menos de 1 hora).
     */
    public boolean estanActualizadas() {
        return ultimaActualizacion != null && 
               Duration.between(ultimaActualizacion, LocalDateTime.now()).toHours() < 1;
    }
    
    /**
     * Obtiene el tiempo promedio en segundos.
     */
    public long getTiempoPromedioSegundos() {
        return tiempoPromedio != null ? tiempoPromedio.getSeconds() : 0;
    }
    
    /**
     * Obtiene el tiempo promedio en milisegundos.
     */
    public long getTiempoPromedioMillis() {
        return tiempoPromedio != null ? tiempoPromedio.toMillis() : 0;
    }
    
    /**
     * Obtiene el tiempo promedio formateado como string.
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
     * Verifica si el usuario tiene un buen rendimiento (>= 70% de aciertos).
     */
    public boolean tieneBuenRendimiento() {
        return porcentajeAciertos >= 70.0;
    }
    
    /**
     * Verifica si el usuario es principiante (< 10 preguntas respondidas).
     */
    public boolean esPrincipiante() {
        return totalPreguntas < 10;
    }
    
    /**
     * Verifica si el usuario es experimentado (>= 50 preguntas respondidas).
     */
    public boolean esExperimentado() {
        return totalPreguntas >= 50;
    }
    
    /**
     * Obtiene la dificultad con mejor rendimiento.
     */
    public Dificultad getDificultadConMejorRendimiento() {
        return porDificultad.entrySet().stream()
                .filter(entry -> entry.getValue().totalPreguntas() > 0)
                .max((e1, e2) -> Double.compare(
                    e1.getValue().porcentajeAciertos(), 
                    e2.getValue().porcentajeAciertos()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    /**
     * Obtiene la temática con mejor rendimiento.
     */
    public String getTematicaConMejorRendimiento() {
        return porTematica.entrySet().stream()
                .filter(entry -> entry.getValue().totalPreguntas() > 0)
                .max((e1, e2) -> Double.compare(
                    e1.getValue().porcentajeAciertos(), 
                    e2.getValue().porcentajeAciertos()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    /**
     * Valida que las estadísticas sean consistentes.
     */
    public void validar() {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        if (totalPreguntas < 0) {
            throw new IllegalArgumentException("El total de preguntas no puede ser negativo");
        }
        
        if (respuestasCorrectas < 0) {
            throw new IllegalArgumentException("Las respuestas correctas no pueden ser negativas");
        }
        
        if (respuestasCorrectas > totalPreguntas) {
            throw new IllegalArgumentException("Las respuestas correctas no pueden superar al total");
        }
        
        if (porcentajeAciertos < 0.0 || porcentajeAciertos > 100.0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        
        if (tiempoPromedio != null && tiempoPromedio.isNegative()) {
            throw new IllegalArgumentException("El tiempo promedio no puede ser negativo");
        }
        
        if (ultimaActualizacion == null) {
            throw new IllegalArgumentException("La fecha de última actualización no puede ser nula");
        }
        
        // Validar consistencia del porcentaje calculado
        double porcentajeCalculado = calcularPorcentajeAciertos();
        if (Math.abs(porcentajeAciertos - porcentajeCalculado) > 0.01) {
            throw new IllegalArgumentException("El porcentaje de aciertos no es consistente");
        }
    }
}
