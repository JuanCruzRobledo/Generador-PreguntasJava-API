package org.jcr.generadorpreguntasjava.port.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.mapper.LenguajeMapper;
import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;
import org.jcr.generadorpreguntasjava.domain.model.Lenguaje;
import org.jcr.generadorpreguntasjava.port.in.ConsultarLenguajesPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.CategoriaTematicaResponse;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.LenguajeResponse;
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la gestión de Lenguajes.
 *
 * Adaptador primario que traduce peticiones HTTP en llamadas a los puertos de entrada.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LenguajesController {

    private final ConsultarLenguajesPort consultarLenguajesPort;
    private final LenguajeMapper lenguajeMapper;

    /**
     * Obtiene todos los lenguajes.
     *
     * GET /api/v1/lenguajes
     */
    @GetMapping("/lenguajes")
    public ApiResponse<List<LenguajeResponse>> obtenerTodasLasTagsDeCategoria() {
        log.info("Solicitud para obtener todas las temáticas");

        try {
            List<Lenguaje> categorias = consultarLenguajesPort.obtenerTodosLosLenguajes();
            List<LenguajeResponse> response = lenguajeMapper.toResponseList(categorias);

            log.info("Se retornaron {} categorias", categorias.size());
            return ApiResponse.exito(response, String.format("Se encontraron %d lenguajes", categorias.size()));

        } catch (Exception e) {
            log.error("Error al obtener temáticas: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener temáticas", e.getMessage());
        }
    }
}
