package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.CategoriaTematicaResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaTematicaMapper {

    CategoriaTematicaResponse toResponse(CategoriaTematica categoriaTematica);

    List<CategoriaTematicaResponse> toCategoriaTematicaResponseList(List<CategoriaTematica> categoriasTematicas);
}
