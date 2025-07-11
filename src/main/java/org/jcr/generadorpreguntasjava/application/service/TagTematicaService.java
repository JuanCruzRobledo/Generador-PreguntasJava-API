package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.TagTematica;
import org.jcr.generadorpreguntasjava.port.in.ConsultarTagTematicaPort;
import org.jcr.generadorpreguntasjava.port.out.TematicaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicación principal para la gestión de tags.
 *
 * Implementa todos los puertos de entrada y orquesta los casos de uso principales:
 * -
 * -
 * -
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TagTematicaService implements ConsultarTagTematicaPort {

    private final TematicaRepositoryPort tematicaRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<TagTematica> obtenerTodasLasTematicas() {
        log.info("Obteniendo todas las temáticas");
        List<TagTematica> tagTematicas = tematicaRepositoryPort.obtenerTodas();
        log.info("Se encontraron {} temáticas", tagTematicas.size());
        return tagTematicas;
    }

    @Override
    public List<TagTematica> obtenerTodasLasTematicas(Long id) {
        log.info("Obteniendo todas las temáticas de categoria");
        List<TagTematica> tagTematicas = tematicaRepositoryPort.obtenerTodosDeCategoria(id);
        log.info("Se encontraron {} temáticas", tagTematicas.size());
        return tagTematicas;
    }
}
