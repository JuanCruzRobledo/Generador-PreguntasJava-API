package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Examen;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.ExamenResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExamenMapper {

    ExamenResponse toResponse(Examen examen);
    List<ExamenResponse> toResponseList(List<Examen> examen);
}
