package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.SesionRespuesta;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.SesionRespuestaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SesionRespuestaPersistenceMapper {

    // === MAPPING DE SESION RESPUESTA ===

    //@Mapping(target = "tiempoRespuesta", source = ".", qualifiedByName = "mapTiempoRespuestaFromEntity")
    SesionRespuesta toDomain(SesionRespuestaEntity entity);

    //@Mapping(target = "tiempoRespuestaMs", source = ".", qualifiedByName = "mapTiempoRespuestaToEntity")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "pregunta", ignore = true)
    SesionRespuestaEntity toEntity(SesionRespuesta domain);

    List<SesionRespuesta> toDomainSesionList(List<SesionRespuestaEntity> entities);

}
