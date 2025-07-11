package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Lenguaje;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.LenguajeResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LenguajeMapper {
    List<LenguajeResponse> toResponseList(List<Lenguaje> lenguajes);
}
