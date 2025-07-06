package org.jcr.generadorpreguntasjava.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Respuesta estándar para todos los endpoints de la API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean exitoso,
    String mensaje,
    T datos,
    
    @JsonProperty("timestamp")
    LocalDateTime timestamp,
    
    String error
) {
    
    /**
     * Crea una respuesta exitosa con datos.
     */
    public static <T> ApiResponse<T> exito(T datos, String mensaje) {
        return new ApiResponse<>(true, mensaje, datos, LocalDateTime.now(), null);
    }
    
    /**
     * Crea una respuesta exitosa sin datos.
     */
    public static <T> ApiResponse<T> exito(String mensaje) {
        return new ApiResponse<>(true, mensaje, null, LocalDateTime.now(), null);
    }
    
    /**
     * Crea una respuesta de error.
     */
    public static <T> ApiResponse<T> error(String mensaje, String detalleError) {
        return new ApiResponse<>(false, mensaje, null, LocalDateTime.now(), detalleError);
    }
    
    /**
     * Crea una respuesta de error simple.
     */
    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(false, mensaje, null, LocalDateTime.now(), null);
    }
}
