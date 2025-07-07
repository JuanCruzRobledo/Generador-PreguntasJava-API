package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.SesionRespuesta;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.SesionRespuestaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface SesionRespuestaMapper {

    SesionRespuestaMapper INSTANCE = Mappers.getMapper(SesionRespuestaMapper.class);

    @Mapping(source = "id", target = "sesionId")
    @Mapping(source = "inicioRespuesta", target = "inicioRespuesta", qualifiedByName = "formatDateTime")
    @Mapping(source = "finRespuesta", target = "finRespuesta", qualifiedByName = "formatDateTime")
    @Mapping(source = "tiempoRespuesta", target = "tiempoRespuestaMs", qualifiedByName = "durationToMillis")
    SesionRespuestaResponse toResponse(SesionRespuesta sesion);

    @Named("formatDateTime")
    static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    @Named("durationToMillis")
    static long durationToMillis(Duration duration) {
        return duration != null ? duration.toMillis() : 0;
    }
}