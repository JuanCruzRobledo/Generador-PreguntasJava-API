package org.jcr.generadorpreguntasjava.port.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.mapper.TematicaMapper;
import org.jcr.generadorpreguntasjava.domain.model.TagTematica;
import org.jcr.generadorpreguntasjava.port.in.ConsultarTagTematicaPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.TematicaResponse;
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Tags.
 *
 * Adaptador primario que traduce peticiones HTTP en llamadas a los puertos de entrada.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TagTematicaController{

    private final TematicaMapper tematicaMapper;
    private final ConsultarTagTematicaPort consultarTagTematicaPort;

    /**
     * Obtiene todas las tags disponibles.
     *
     * GET /api/v1/tags
     */
    @GetMapping("/tags")
    public ApiResponse<List<TematicaResponse>> obtenerTodasLasTags() {
        log.info("Solicitud para obtener todas las temáticas");

        try {
            List<TagTematica> tagTematicas = consultarTagTematicaPort.obtenerTodasLasTematicas();
            List<TematicaResponse> response = tematicaMapper.toTematicaResponseList(tagTematicas);

            log.info("Se retornaron {} temáticas", tagTematicas.size());
            return ApiResponse.exito(response, String.format("Se encontraron %d temáticas", tagTematicas.size()));

        } catch (Exception e) {
            log.error("Error al obtener temáticas: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener temáticas", e.getMessage());
        }
    }

    /**
     * Obtiene todos los tags de una categoria.
     *
     * GET /api/v1/tags
     */
    @GetMapping("/{id}/tags")
    public ApiResponse<List<TematicaResponse>> obtenerTodasLasTagsDeCategoria(@PathVariable Long id) {
        log.info("Solicitud para obtener todas las temáticas");

        try {
            List<TagTematica> tagTematicas = consultarTagTematicaPort.obtenerTodasLasTematicas(id);
            List<TematicaResponse> response = tematicaMapper.toTematicaResponseList(tagTematicas);

            log.info("Se retornaron {} temáticas", tagTematicas.size());
            return ApiResponse.exito(response, String.format("Se encontraron %d tags", tagTematicas.size()));

        } catch (Exception e) {
            log.error("Error al obtener temáticas: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener temáticas", e.getMessage());
        }
    }
}
