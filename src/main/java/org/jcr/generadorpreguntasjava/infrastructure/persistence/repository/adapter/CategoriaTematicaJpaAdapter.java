package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;


import lombok.RequiredArgsConstructor;
import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.CategoriaTematicaPersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataCategoriaTematicaRepository;
import org.jcr.generadorpreguntasjava.port.out.CategoriaTematicaRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Adaptador JPA para manejar Categorias.
 */

@Component
@RequiredArgsConstructor
@Transactional
public class CategoriaTematicaJpaAdapter implements CategoriaTematicaRepositoryPort {

    private final SpringDataCategoriaTematicaRepository categoriaTematicaRepository;
    private final CategoriaTematicaPersistenceMapper categoriaTematicaPersistenceMapper;

    @Override
    public CategoriaTematica guardar(CategoriaTematica categoriaTematica) {
        return null;
    }
}
