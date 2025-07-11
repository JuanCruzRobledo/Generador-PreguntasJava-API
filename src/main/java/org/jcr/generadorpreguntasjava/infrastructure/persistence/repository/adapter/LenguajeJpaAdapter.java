package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Lenguaje;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.LenguajeEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.LenguajePersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataLenguajeRepository;
import org.jcr.generadorpreguntasjava.port.out.LenguajeRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Adaptador secundario que implementa el puerto de salida para persistencia de Lenguajes.
 *
 * Convierte entre objetos del dominio y entidades JPA.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class LenguajeJpaAdapter implements LenguajeRepositoryPort {

    private final SpringDataLenguajeRepository lenguajeRepository;
    private final LenguajePersistenceMapper lenguajePersistenceMapper;

    @Override
    public Lenguaje guardar(Lenguaje pregunta) {
        return null;
    }

    @Override
    public List<Lenguaje> obtenerLenguajes() {
        List<LenguajeEntity> lenguajes = lenguajeRepository.findAll();
        return lenguajePersistenceMapper.toDomainList(lenguajes);
    }
}
