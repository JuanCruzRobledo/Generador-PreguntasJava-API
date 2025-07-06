package org.jcr.generadorpreguntasjava.domain.model;

/**
 * Enum que representa los niveles de dificultad de las preguntas.
 * 
 * Entidad del dominio - sin anotaciones de frameworks externos.
 */
public enum Dificultad {
    FACIL("Fácil"),
    MEDIA("Media"),
    DIFICIL("Difícil");
    
    private final String descripcion;
    
    Dificultad(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public static Dificultad fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (Dificultad dificultad : Dificultad.values()) {
            if (dificultad.name().equalsIgnoreCase(value) || 
                dificultad.descripcion.equalsIgnoreCase(value)) {
                return dificultad;
            }
        }
        
        throw new IllegalArgumentException("Dificultad no válida: " + value);
    }
}
