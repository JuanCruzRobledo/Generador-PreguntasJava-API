package org.jcr.generadorpreguntasjava.infrastructure.persistence.data;

import lombok.RequiredArgsConstructor;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.CategoriaTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TagTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataCategoriaTematicaRepository;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataTematicaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CategoriaTematicaData {

    private final SpringDataCategoriaTematicaRepository tematicaRepository;
    private final SpringDataTematicaRepository tagRepository;

    public void init() {
        if (tematicaRepository.count() == 0) {
            // Función para buscar tag persistido por nombre
            Function<String, TagTematicaEntity> buscarTag = nombre ->
                    tagRepository.findByNombre(nombre)
                            .orElseThrow(() -> new RuntimeException("Tag no encontrado: " + nombre));

            List<CategoriaTematicaEntity> categorias = List.of(

                    CategoriaTematicaEntity.builder()
                            .nombre("Fundamentos de Programación")
                            .descripcion("Variables, tipos de datos, operadores y estructuras básicas.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("variables"),
                                    buscarTag.apply("tipos de datos"),
                                    buscarTag.apply("operadores aritméticos"),
                                    buscarTag.apply("operadores lógicos"),
                                    buscarTag.apply("estructuras de control"),
                                    buscarTag.apply("bucles")
                            ))
                            .build(),

                    CategoriaTematicaEntity.builder()
                            .nombre("Programación Orientada a Objetos")
                            .descripcion("Conceptos clave de la POO.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("clases"),
                                    buscarTag.apply("objetos"),
                                    buscarTag.apply("herencia"),
                                    buscarTag.apply("polimorfismo"),
                                    buscarTag.apply("encapsulamiento"),
                                    buscarTag.apply("abstracción")
                            ))
                            .build(),

                    CategoriaTematicaEntity.builder()
                            .nombre("Collections Framework")
                            .descripcion("Colecciones de datos y sus operaciones.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("ArrayList"),
                                    buscarTag.apply("HashMap"),
                                    buscarTag.apply("LinkedList"),
                                    buscarTag.apply("Set"),
                                    buscarTag.apply("Map"),
                                    buscarTag.apply("iteradores")
                            ))
                            .build(),

                    CategoriaTematicaEntity.builder()
                            .nombre("Manejo de Excepciones")
                            .descripcion("Captura y manejo de errores.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("try-catch"),
                                    buscarTag.apply("throws"),
                                    buscarTag.apply("finally"),
                                    buscarTag.apply("excepciones personalizadas"),
                                    buscarTag.apply("stack trace")
                            ))
                            .build(),

                    CategoriaTematicaEntity.builder()
                            .nombre("Concurrencia y Threads")
                            .descripcion("Ejecución paralela y sincronización.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("threads"),
                                    buscarTag.apply("synchronization"),
                                    buscarTag.apply("runnable"),
                                    buscarTag.apply("deadlock"),
                                    buscarTag.apply("mutex"),
                                    buscarTag.apply("executors")
                            ))
                            .build(),

                    CategoriaTematicaEntity.builder()
                            .nombre("Streams API")
                            .descripcion("Procesamiento funcional de datos.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("lambda"),
                                    buscarTag.apply("map"),
                                    buscarTag.apply("filter"),
                                    buscarTag.apply("collect"),
                                    buscarTag.apply("programación funcional")
                            ))
                            .build(),

                    CategoriaTematicaEntity.builder()
                            .nombre("Entrada/Salida (I/O)")
                            .descripcion("Lectura y escritura de archivos y flujos.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("File"),
                                    buscarTag.apply("InputStream"),
                                    buscarTag.apply("OutputStream"),
                                    buscarTag.apply("BufferedReader"),
                                    buscarTag.apply("serialización")
                            ))
                            .build(),

                    CategoriaTematicaEntity.builder()
                            .nombre("Conectividad con Bases de Datos")
                            .descripcion("Acceso a bases de datos con JDBC y otras tecnologías.")
                            .tagsTematicas(List.of(
                                    buscarTag.apply("JDBC"),
                                    buscarTag.apply("PreparedStatement"),
                                    buscarTag.apply("ResultSet"),
                                    buscarTag.apply("ORM"),
                                    buscarTag.apply("SQL básico")
                            ))
                            .build()
            );

            tematicaRepository.saveAll(categorias);
        } else {
            System.out.println("Datos de Categoria ya inicializados");
        }
    }
}
