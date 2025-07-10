package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.Examen;
import org.jcr.generadorpreguntasjava.domain.model.Pregunta;
import org.jcr.generadorpreguntasjava.domain.model.Tematica;
import org.jcr.generadorpreguntasjava.port.in.ConsultarExamenesPort;
import org.jcr.generadorpreguntasjava.port.in.GenerarExamenPort;
import org.jcr.generadorpreguntasjava.port.out.ExamenRepositoryPort;
import org.jcr.generadorpreguntasjava.port.out.GeneradorDePreguntaServicePort;
import org.jcr.generadorpreguntasjava.port.out.TematicaRepositoryPort;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ExamenService implements GenerarExamenPort, ConsultarExamenesPort {

    private final PromptBuilderService promptBuilder;
    private final Environment environment;
    private final GeneradorDePreguntaServicePort generadorService;
    private final TematicaRepositoryPort tematicaRepositoryPort;
    private final ExamenRepositoryPort examenRepository;

    @Override
    public Examen generarExamen(String titulo, String descripcion, Dificultad dificultad,
                                int cantidadPreguntas, List<String> tematicasDeseadas,
                                List<String> tematicasExcluidas) {
        log.info("Iniciando generación de examen con {} preguntas", cantidadPreguntas);

        try {
            // 1. Construir prompt específico para exámenes
            String prompt = promptBuilder.construirPromptExamen(
                    dificultad,
                    cantidadPreguntas,
                    tematicasDeseadas,
                    tematicasExcluidas
            );

            // 2. Generar el examen completo (en producción o test)
            GeneradorDePreguntaServicePort.RespuestaExamen respuesta;
            if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
                log.info("Usando respuesta simulada de examen (perfil test)");
                respuesta = generadorService.simularRespuestaExamen();
            } else {
                respuesta = generadorService.generarExamenCompleto(prompt);
            }

            // 3. Construir las preguntas del dominio a partir de la respuesta externa
            List<Pregunta> preguntasGeneradas = respuesta.preguntas().stream()
                    .map(Pregunta::fromRespuestaGeneracion)
                    .toList();

            // 4. Extraer y persistir las temáticas (crear nuevas o actualizar existentes)
            List<Tematica> tematicasPersistidas = persistirTematicasExamen(preguntasGeneradas);

            // 5. Crear el objeto Examen del dominio
            Examen examen = new Examen(
                    titulo,
                    descripcion,
                    Examen.calcularDificultadPromedio(preguntasGeneradas),
                    preguntasGeneradas,
                    tematicasPersistidas
            );

            // 6. Validar el examen (estructura, cantidad de preguntas, consistencia)
            examen.validar();

            // 7. Persistir el examen completo (preguntas y temáticas asociadas)
            Examen examenPersistido = examenRepository.guardar(examen);

            log.info("Examen generado exitosamente con ID: {}", examenPersistido.id());
            return examenPersistido;

        } catch (IllegalArgumentException e) {
            // Errores de validación (estructura, cantidad de preguntas, etc.)
            log.error("Error de validación al generar examen: {}", e.getMessage());
            throw new RuntimeException("Error al generar examen: " + e.getMessage(), e);
        } catch (Exception e) {
            // Errores inesperados durante la generación
            log.error("Error inesperado generando examen: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno al generar examen", e);
        }
    }

    /**
     * Persiste todas las temáticas involucradas en el examen.
     */
    private List<Tematica> persistirTematicasExamen(List<Pregunta> preguntas) {
        return preguntas.stream()
                .flatMap(p -> p.tematicas().stream())
                .distinct()
                .map(tematicaRepositoryPort::persistirConIntegridad)
                .toList();
    }

    @Override
    public List<Examen> obtenerTodosLosExamenes() {
        return List.of();
    }

    @Override
    public Optional<Examen> obtenerExamenPorId(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Examen> obtenerExamenesPorTematica(String nombreTematica) {
        return List.of();
    }

    @Override
    public List<Examen> obtenerExamenesPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return List.of();
    }
}
