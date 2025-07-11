package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Lenguaje;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.LenguajeEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LenguajePersistenceMapper {

    LenguajeEntity toEntity(Lenguaje lenguajeDomain);
    Lenguaje toDomain(LenguajeEntity lenguajeEntity);
    List<Lenguaje> toDomainList(List<LenguajeEntity> lenguajesEntity);
}
