package org.jcr.generadorpreguntasjava.infrastructure.persistence.data;

import lombok.RequiredArgsConstructor;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TagTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataTematicaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagTematicaData {

    private final SpringDataTematicaRepository springDataTematicaRepository;

    public void init() {
        if (springDataTematicaRepository.count() == 0) {
            List<String> nombresTags = List.of(
                    // Fundamentos de Programación
                    "variables", "tipos de datos", "operadores aritméticos", "operadores lógicos", "estructuras de control", "bucles",

                    // POO
                    "clases", "objetos", "herencia", "polimorfismo", "encapsulamiento", "abstracción",

                    // Collections
                    "ArrayList", "HashMap", "LinkedList", "Set", "Map", "iteradores",

                    // Excepciones
                    "try-catch", "throws", "finally", "excepciones personalizadas", "stack trace",

                    // Concurrencia
                    "threads", "synchronization", "runnable", "deadlock", "mutex", "executors",

                    // Streams
                    "lambda", "map", "filter", "collect", "programación funcional",

                    // Entrada/Salida
                    "File", "InputStream", "OutputStream", "BufferedReader", "serialización",

                    // Bases de Datos
                    "JDBC", "PreparedStatement", "ResultSet", "ORM", "SQL básico"
            );

            List<TagTematicaEntity> tags = nombresTags.stream()
                    .map(nombre -> TagTematicaEntity.builder().nombre(nombre).contadorUsos(0).build())
                    .toList();

            springDataTematicaRepository.saveAll(tags);
        }else {
            System.out.println("Datos de Tags ya inicializados");
        }
    }
}
