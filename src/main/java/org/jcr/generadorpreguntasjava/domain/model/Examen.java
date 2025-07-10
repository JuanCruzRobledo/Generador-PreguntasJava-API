package org.jcr.generadorpreguntasjava.domain.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entidad principal que representa un examen compuesto por múltiples preguntas.
 *
 * Objeto inmutable del dominio - sin anotaciones de frameworks externos.
 */
public record Examen(
        Long id,
        String titulo,
        String descripcion,
        Dificultad dificultadPromedio,
        LocalDateTime fechaCreacion,
        List<Pregunta> preguntas,
        List<Tematica> tematicas
) {

    /**
     * Constructor para crear un nuevo examen sin ID (para persistir).
     */
    public Examen(String titulo, String descripcion, Dificultad dificultadPromedio,
                  List<Pregunta> preguntas, List<Tematica> tematicas) {
        this(null, titulo, descripcion, dificultadPromedio, LocalDateTime.now(),
                preguntas, tematicas);
    }

    /**
     * Crea una copia del examen con un nuevo ID.
     */
    public Examen withId(Long nuevoId) {
        return new Examen(nuevoId, this.titulo, this.descripcion, this.dificultadPromedio,
                this.fechaCreacion, this.preguntas, this.tematicas);
    }

    /**
     * Obtiene la cantidad de preguntas en el examen.
     */
    public int getCantidadPreguntas() {
        return preguntas != null ? preguntas.size() : 0;
    }

    /**
     * Obtiene la temática principal (la más frecuente en las preguntas).
     */
    public Tematica getTematicaPrincipal() {
        if (tematicas == null || tematicas.isEmpty()) {
            return null;
        }

        return tematicas.stream()
                .max(Comparator.comparingInt(t -> Collections.frequency(tematicas, t)))
                .orElse(tematicas.get(0));
    }

    /**
     * Validación completa del examen.
     */
    public void validar() {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }

        if (titulo.length() > 200) {
            throw new IllegalArgumentException("El título no puede exceder los 200 caracteres");
        }

        if (descripcion != null && descripcion.length() > 1000) {
            throw new IllegalArgumentException("La descripción no puede exceder los 1000 caracteres");
        }

        if (dificultadPromedio == null) {
            throw new IllegalArgumentException("La dificultad promedio no puede ser nula");
        }

        if (preguntas == null || preguntas.isEmpty()) {
            throw new IllegalArgumentException("El examen debe contener al menos una pregunta");
        }

        if (tematicas == null || tematicas.isEmpty()) {
            throw new IllegalArgumentException("El examen debe tener al menos una temática");
        }

        // Validar que todas las preguntas sean válidas
        preguntas.forEach(Pregunta::validar);

        // Validar que las temáticas sean consistentes con las preguntas
        Set<Tematica> tematicasEnPreguntas = preguntas.stream()
                .flatMap(p -> p.tematicas().stream())
                .collect(Collectors.toSet());

        if (!tematicasEnPreguntas.containsAll(tematicas)) {
            throw new IllegalArgumentException("Las temáticas del examen deben coincidir con las de las preguntas");
        }

        // Validar que la dificultad promedio sea consistente
        double promedioCalculado = preguntas.stream()
                .mapToInt(p -> p.dificultad().ordinal())
                .average()
                .orElse(0);

        Dificultad dificultadCalculada = Dificultad.values()[(int) Math.round(promedioCalculado)];

        if (dificultadCalculada != dificultadPromedio) {
            throw new IllegalArgumentException("La dificultad promedio no coincide con las preguntas");
        }
    }

    /**
     * Calcula la duración estimada del examen en minutos.
     * Basado en la dificultad promedio y cantidad de preguntas.
     */
    public int calcularDuracionEstimada() {
        int minutosBasePorPregunta = switch (dificultadPromedio) {
            case FACIL -> 1;
            case MEDIA -> 2;
            case DIFICIL -> 3;
        };

        return getCantidadPreguntas() * minutosBasePorPregunta;
    }

    /**
     * Obtiene un resumen estadístico del examen.
     */
    public String getResumenEstadistico() {
        Map<Dificultad, Long> conteoPorDificultad = preguntas.stream()
                .collect(Collectors.groupingBy(Pregunta::dificultad, Collectors.counting()));

        return String.format(
                "Examen '%s' - %d preguntas\n" +
                        "Dificultad promedio: %s\n" +
                        "Distribución: Fácil (%d), Media (%d), Difícil (%d)\n" +
                        "Temáticas principales: %s",
                titulo,
                getCantidadPreguntas(),
                dificultadPromedio,
                conteoPorDificultad.getOrDefault(Dificultad.FACIL, 0L),
                conteoPorDificultad.getOrDefault(Dificultad.MEDIA, 0L),
                conteoPorDificultad.getOrDefault(Dificultad.DIFICIL, 0L),
                tematicas.stream()
                        .limit(3)
                        .map(Tematica::nombre)
                        .collect(Collectors.joining(", "))
        );
    }

    /**
     * Calcula la dificultad promedio de una lista de preguntas.
     * Este metodo podría moverse al dominio si prefieres que sea responsabilidad de Examen.
     */
    public static Dificultad calcularDificultadPromedio(List<Pregunta> preguntas) {
        double promedio = preguntas.stream()
                .mapToInt(p -> p.dificultad().ordinal())
                .average()
                .orElse(0);

        return Dificultad.values()[(int) Math.round(promedio)];
    }
}