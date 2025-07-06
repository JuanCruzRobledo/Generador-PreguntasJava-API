package org.jcr.generadorpreguntasjava.domain.model;

import java.time.LocalDateTime;
import java.text.Normalizer;

/**
 * Entidad que representa una temática de preguntas.
 * 
 * Objeto inmutable del dominio - sin anotaciones de frameworks externos.
 */
public record Tematica(
    Long id,
    String nombre,
    Integer contadorUsos,
    LocalDateTime timestampUltimoUso
) {
    
    /**
     * Constructor para crear una nueva temática sin ID (para persistir).
     */
    public Tematica(String nombre) {
        this(null, normalizarNombre(nombre), 0, null);
    }
    
    /**
     * Constructor para crear una nueva temática con contador inicial.
     */
    public Tematica(String nombre, Integer contadorUsos, LocalDateTime timestampUltimoUso) {
        this(null, normalizarNombre(nombre), contadorUsos, timestampUltimoUso);
    }
    
    /**
     * Crea una copia de la temática con un nuevo ID.
     */
    public Tematica withId(Long nuevoId) {
        return new Tematica(nuevoId, this.nombre, this.contadorUsos, this.timestampUltimoUso);
    }
    
    /**
     * Incrementa el contador de usos y actualiza el timestamp.
     */
    public Tematica incrementarUso() {
        return new Tematica(
            this.id,
            this.nombre,
            this.contadorUsos + 1,
            LocalDateTime.now()
        );
    }
    
    /**
     * Normaliza el nombre de la temática removiendo tildes y convirtiendo a minúsculas.
     */
    public static String normalizarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la temática no puede estar vacío");
        }
        
        String nombreNormalizado = Normalizer.normalize(nombre.trim(), Normalizer.Form.NFD);
        nombreNormalizado = nombreNormalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return nombreNormalizado.toLowerCase();
    }
    
    /**
     * Validación de la temática.
     */
    public void validar() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la temática no puede estar vacío");
        }
        
        if (contadorUsos < 0) {
            throw new IllegalArgumentException("El contador de usos no puede ser negativo");
        }
    }
}
