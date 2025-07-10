package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.application.mapper.PreguntaMapper;
import org.jcr.generadorpreguntasjava.domain.model.Examen;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.ExamenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PreguntaMapper.class})
public interface ExamenPersistenceMapper {

    ExamenEntity toEntity(Examen examen);

    Examen toDomain(ExamenEntity examenEntity);
}
