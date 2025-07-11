package org.jcr.generadorpreguntasjava.port.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.mapper.CategoriaTematicaMapper;
import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;
import org.jcr.generadorpreguntasjava.port.in.ConsultarCategoriaTematicaPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.CategoriaTematicaResponse;
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Categorias principales.
 *
 * Adaptador primario que traduce peticiones HTTP en llamadas a los puertos de entrada.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoriaTematicaController {

    private final ConsultarCategoriaTematicaPort consultarCategoriaTematicaPort;
    private final CategoriaTematicaMapper categoriaTematicaMapper;

    /**
     * Obtiene todos los tags de una categoria.
     *
     * GET /api/v1/categorias
     */
    @GetMapping("/categorias")
    public ApiResponse<List<CategoriaTematicaResponse>> obtenerTodasLasCategorias() {
        log.info("Solicitud para obtener todas las temáticas");

        try {
            List<CategoriaTematica> categorias = consultarCategoriaTematicaPort.obtenerTodasLasCategorias();
            List<CategoriaTematicaResponse> response = categoriaTematicaMapper.toCategoriaTematicaResponseList(categorias);

            log.info("Se retornaron {} categorias", categorias.size());
            return ApiResponse.exito(response, String.format("Se encontraron %d categorias", categorias.size()));

        } catch (Exception e) {
            log.error("Error al obtener temáticas: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener temáticas", e.getMessage());
        }
    }

    /**
     * Obtiene todos los tags de una categoria.
     *
     * GET /api/v1/categorias
     */
    @GetMapping("/lenguaje/{id}/categorias")
    public ApiResponse<List<CategoriaTematicaResponse>> obtenerTodasLasCategoriasDeUnLenguaje(@PathVariable Long id) {
        log.info("Solicitud para obtener todas las temáticas");

        try {
            List<CategoriaTematica> categorias = consultarCategoriaTematicaPort.obtenerTodasLasCategoriasDeUnLenguaje(id);
            List<CategoriaTematicaResponse> response = categoriaTematicaMapper.toCategoriaTematicaResponseList(categorias);

            log.info("Se retornaron {} categorias", categorias.size());
            return ApiResponse.exito(response, String.format("Se encontraron %d categorias", categorias.size()));

        } catch (Exception e) {
            log.error("Error al obtener temáticas: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener temáticas", e.getMessage());
        }
    }
}
