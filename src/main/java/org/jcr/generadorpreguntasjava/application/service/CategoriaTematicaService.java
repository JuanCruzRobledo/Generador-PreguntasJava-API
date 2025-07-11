package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.CategoriaTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.CategoriaTematicaPersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataCategoriaTematicaRepository;
import org.jcr.generadorpreguntasjava.port.in.ConsultarCategoriaTematicaPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicación principal para la gestión de categorias principales.
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
public class CategoriaTematicaService implements ConsultarCategoriaTematicaPort {

    private final SpringDataCategoriaTematicaRepository categoriaTematicaRepository;
    private final CategoriaTematicaPersistenceMapper categoriaTematicaMapper;

    @Override
    public List<CategoriaTematica> obtenerTodasLasCategoriasTematicas() {
        List<CategoriaTematicaEntity> listaCategoriasEntity = categoriaTematicaRepository.findAll();
        return categoriaTematicaMapper.toDomainList(listaCategoriasEntity);
    }
}
