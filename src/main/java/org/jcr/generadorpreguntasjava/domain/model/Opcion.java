package org.jcr.generadorpreguntasjava.domain.model;

/**
 * Entidad que representa una opción de respuesta de una pregunta.
 * 
 * Objeto inmutable del dominio - sin anotaciones de frameworks externos.
 */
public record Opcion(
    Long id,
    String contenido
) {
    
    /**
     * Constructor para crear una nueva opción sin ID (para persistir).
     */
    public Opcion(String contenido) {
        this(null, contenido);
    }
    
    /**
     * Crea una copia de la opción con un nuevo ID.
     */
    public Opcion withId(Long nuevoId) {
        return new Opcion(nuevoId, this.contenido);
    }
    
    /**
     * Validación de la opción.
     */
    public void validar() {
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido de la opción no puede estar vacío");
        }
    }
}
