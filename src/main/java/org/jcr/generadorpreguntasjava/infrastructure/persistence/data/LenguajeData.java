package org.jcr.generadorpreguntasjava.infrastructure.persistence.data;

import lombok.RequiredArgsConstructor;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.CategoriaTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.LenguajeEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataCategoriaTematicaRepository;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataLenguajeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class LenguajeData {

    private final SpringDataLenguajeRepository lenguajeRepository;
    private final SpringDataCategoriaTematicaRepository tematicaRepository;

    public void init() {
        if (lenguajeRepository.count() == 0) {
            // Cargamos todas las categorías temáticas desde la base
            List<CategoriaTematicaEntity> todasLasCategorias = tematicaRepository.findAll();

            // Helper para buscar por nombre
            Function<String, CategoriaTematicaEntity> buscarCategoria = nombre ->
                    todasLasCategorias.stream()
                            .filter(cat -> cat.getNombre().equalsIgnoreCase(nombre))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + nombre));

            List<LenguajeEntity> lenguajes = List.of(
                    LenguajeEntity.builder()
                            .nombre("Java")
                            .descripcion("Lenguaje de programación orientado a objetos y ampliamente utilizado en aplicaciones empresariales.")
                            .categoriasPrincipales(List.of(
                                    buscarCategoria.apply("Fundamentos de Programación"),
                                    buscarCategoria.apply("Programación Orientada a Objetos"),
                                    buscarCategoria.apply("Collections Framework"),
                                    buscarCategoria.apply("Manejo de Excepciones"),
                                    buscarCategoria.apply("Concurrencia y Threads"),
                                    buscarCategoria.apply("Streams API"),
                                    buscarCategoria.apply("Entrada/Salida (I/O)"),
                                    buscarCategoria.apply("Conectividad con Bases de Datos")
                            ))
                            .preguntas(List.of())
                            .build(),

                    LenguajeEntity.builder()
                            .nombre("Python")
                            .descripcion("Lenguaje de programación de alto nivel, con una sintaxis sencilla y muy usado en ciencia de datos, scripting y web.")
                            .categoriasPrincipales(List.of(
                                    buscarCategoria.apply("Fundamentos de Programación"),
                                    buscarCategoria.apply("Programación Orientada a Objetos"),
                                    buscarCategoria.apply("Manejo de Excepciones"),
                                    buscarCategoria.apply("Conectividad con Bases de Datos"),
                                    buscarCategoria.apply("Entrada/Salida (I/O)"),
                                    buscarCategoria.apply("Concurrencia y Threads"),
                                    buscarCategoria.apply("Streams API") // como programación funcional
                            ))
                            .preguntas(List.of())
                            .build(),

                    LenguajeEntity.builder()
                            .nombre("JavaScript")
                            .descripcion("Lenguaje de programación del lado del cliente más usado para el desarrollo web.")
                            .categoriasPrincipales(List.of(
                                    buscarCategoria.apply("Fundamentos de Programación"),
                                    buscarCategoria.apply("Programación Orientada a Objetos"),
                                    buscarCategoria.apply("Manejo de Excepciones"),
                                    buscarCategoria.apply("Streams API")
                            ))
                            .preguntas(List.of())
                            .build(),

                    LenguajeEntity.builder()
                            .nombre("C#")
                            .descripcion("Lenguaje de programación desarrollado por Microsoft, utilizado en aplicaciones de escritorio, juegos y sistemas empresariales.")
                            .categoriasPrincipales(List.of(
                                    buscarCategoria.apply("Fundamentos de Programación"),
                                    buscarCategoria.apply("Programación Orientada a Objetos"),
                                    buscarCategoria.apply("Collections Framework"),
                                    buscarCategoria.apply("Manejo de Excepciones"),
                                    buscarCategoria.apply("Conectividad con Bases de Datos"),
                                    buscarCategoria.apply("Entrada/Salida (I/O)"),
                                    buscarCategoria.apply("Concurrencia y Threads")
                            ))
                            .preguntas(List.of())
                            .build(),

                    LenguajeEntity.builder()
                            .nombre("Kotlin")
                            .descripcion("Lenguaje moderno que se ejecuta en la JVM, 100% interoperable con Java y oficial para desarrollo Android.")
                            .categoriasPrincipales(List.of(
                                    buscarCategoria.apply("Fundamentos de Programación"),
                                    buscarCategoria.apply("Programación Orientada a Objetos"),
                                    buscarCategoria.apply("Manejo de Excepciones"),
                                    buscarCategoria.apply("Streams API"),
                                    buscarCategoria.apply("Conectividad con Bases de Datos"),
                                    buscarCategoria.apply("Concurrencia y Threads")
                            ))
                            .preguntas(List.of())
                            .build()
            );

            lenguajeRepository.saveAll(lenguajes);
        } else {
            System.out.println("Datos de Lenguaje ya inicializados");
        }
    }
}
