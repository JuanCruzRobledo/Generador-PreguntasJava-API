package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.TagTematica;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.TematicaResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TematicaMapper {
    // === MAPPING DE TEMATICA ===

    TematicaResponse toResponse(TagTematica tagTematica);

    List<TematicaResponse> toTematicaResponseList(List<TagTematica> tagTematicas);
}
