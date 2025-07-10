package org.jcr.generadorpreguntasjava.domain.model;

import org.jcr.generadorpreguntasjava.port.out.GeneradorDePreguntaServicePort;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Entidad principal que representa una pregunta de opción múltiple.
 * 
 * Objeto inmutable del dominio - sin anotaciones de frameworks externos.
 */
public record Pregunta(
    Long id,
    String codigoJava,
    String enunciado,
    Dificultad dificultad,
    String respuestaCorrecta,
    String explicacion,
    List<Opcion> opciones,
    List<Tematica> tematicas
) {
    
    /**
     * Constructor para crear una nueva pregunta sin ID (para persistir).
     */
    public Pregunta(String codigoJava, String enunciado, Dificultad dificultad, 
                   String respuestaCorrecta, String explicacion, 
                   List<Opcion> opciones, List<Tematica> tematicas) {
        this(null, codigoJava, enunciado, dificultad, respuestaCorrecta, 
             explicacion, opciones, tematicas);
    }
    
    /**
     * Crea una copia de la pregunta con un nuevo ID.
     */
    public Pregunta withId(Long nuevoId) {
        return new Pregunta(nuevoId, this.codigoJava, this.enunciado, 
                           this.dificultad, this.respuestaCorrecta, 
                           this.explicacion, this.opciones, this.tematicas);
    }
    
    /**
     * Valida si una respuesta proporcionada es correcta.
     */
    public boolean validarRespuesta(String respuestaDada) {
        if (respuestaDada == null || respuestaDada.trim().isEmpty()) {
            return false;
        }
        return Objects.equals(respuestaCorrecta.trim(), respuestaDada.trim());
    }
    
    /**
     * Obtiene la temática principal (primera en la lista).
     */
    public Tematica getTematicaPrincipal() {
        if (tematicas == null || tematicas.isEmpty()) {
            return null;
        }
        return tematicas.get(0);
    }
    
    /**
     * Obtiene las temáticas secundarias (todas excepto la primera).
     */
    public List<Tematica> getTematicasSecundarias() {
        if (tematicas == null || tematicas.size() <= 1) {
            return List.of();
        }
        return tematicas.subList(1, tematicas.size());
    }
    
    /**
     * Validación completa de la pregunta.
     */
    public void validar() {
        if (codigoJava == null || codigoJava.trim().isEmpty()) {
            throw new IllegalArgumentException("El código Java no puede estar vacío");
        }
        
        if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }
        
        if (dificultad == null) {
            throw new IllegalArgumentException("La dificultad no puede ser nula");
        }
        
        if (respuestaCorrecta == null || respuestaCorrecta.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta correcta no puede estar vacía");
        }
        
        if (explicacion == null || explicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La explicación no puede estar vacía");
        }
        
        if (opciones == null || opciones.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos una opción");
        }
        
        if (opciones.size() != 4) {
            throw new IllegalArgumentException("Debe haber exactamente 4 opciones");
        }
        
        if (tematicas == null || tematicas.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos una temática");
        }
        
        // Validar que la respuesta correcta esté entre las opciones
        boolean respuestaEncontrada = opciones.stream()
            .anyMatch(opcion -> Objects.equals(opcion.contenido(), respuestaCorrecta));
        
        if (!respuestaEncontrada) {
            throw new IllegalArgumentException("La respuesta correcta debe estar entre las opciones disponibles");
        }
        
        // Validar cada opción
        opciones.forEach(Opcion::validar);
        
        // Validar cada temática
        tematicas.forEach(Tematica::validar);
    }

    /**
     * Construye una pregunta del dominio a partir de la respuesta del servicio externo.
     */
    public static Pregunta fromRespuestaGeneracion(GeneradorDePreguntaServicePort.RespuestaGeneracion respuesta) {
        return new Pregunta(
                respuesta.codigoJava(),
                respuesta.enunciado(),
                Dificultad.fromString(respuesta.dificultad()),
                respuesta.respuestaCorrecta(),
                respuesta.explicacion(),
                Arrays.stream(respuesta.opciones()).map(Opcion::new).toList(),
                List.of(
                        new Tematica(respuesta.tematicaPrincipal()),
                        new Tematica(respuesta.tematicaSecundaria())
                )
        );
    }

    /**
     * Verifica que las temáticas no hayan sido utilizadas previamente en esta sesion de preguntas.
     */
    public void verificarTematicasNoUtilizadas(List<String> tematicasYaUtilizadas) {
        if (tematicasYaUtilizadas.contains(this.getTematicaPrincipal().nombre())) {
            throw new IllegalArgumentException("Temática principal ya utilizada");
        }

        this.getTematicasSecundarias().stream()
                .filter(t -> tematicasYaUtilizadas.contains(t.nombre()))
                .findFirst()
                .ifPresent(t -> {
                    throw new IllegalArgumentException("Temática secundaria ya utilizada: " + t.nombre());
                });
    }
}
