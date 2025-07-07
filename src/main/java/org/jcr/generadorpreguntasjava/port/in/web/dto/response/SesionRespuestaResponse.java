package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

/**
 * DTO para representar la respuesta al iniciar o completar una sesión.
 */
public record SesionRespuestaResponse(
        Long sesionId,
        Long usuarioId,
        Long preguntaId,
        String respuestaSeleccionada,
        boolean esCorrecta,
        String inicioRespuesta,
        String finRespuesta,
        long tiempoRespuestaMs
) {}