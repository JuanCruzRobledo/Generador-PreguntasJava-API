package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Opcion;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.OpcionResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OpcionMapper {
    // === MAPPING DE OPCION ===

    OpcionResponse toResponse(Opcion opcion);

    List<OpcionResponse> toOpcionResponseList(List<Opcion> opciones);

}
