package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Pregunta;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.PreguntaEntity;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring", uses = {OpcionPersistenceMapper.class, TematicaPersistenceMapper.class})
public interface PreguntaPersistenceMapper {
    // === MAPPING DE PREGUNTA ===
    Pregunta toDomain(PreguntaEntity entity);

    PreguntaEntity toEntity(Pregunta domain);

    List<Pregunta> toDomainList(List<PreguntaEntity> entities);



}
