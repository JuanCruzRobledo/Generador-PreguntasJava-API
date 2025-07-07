package org.jcr.generadorpreguntasjava.port.in.web.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.mapper.PreguntaMapper;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.port.in.GestionarUsuarioPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.request.ActualizarPerfilRequest;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.UsuarioResponse;
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de usuarios.
 * 
 * Adaptador primario que traduce peticiones HTTP en llamadas a los puertos de entrada
 * para operaciones relacionadas con usuarios.
 */
@Slf4j
@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UsuarioController {
    
    private final GestionarUsuarioPort usuarioService;
    private final PreguntaMapper preguntaMapper;
    
    /**
     * Obtiene el perfil del usuario actual.
     * 
     * GET /api/v1/usuarios/perfil
     */
    @GetMapping("/perfil")
    public ApiResponse<UsuarioResponse> obtenerPerfilUsuario() {
        log.info("Solicitud para obtener perfil de usuario");
        
        try {
            Usuario usuario = usuarioService.obtenerUsuarioActual();
            UsuarioResponse response = preguntaMapper.toUsuarioResponse(usuario);
            
            log.info("Perfil de usuario obtenido exitosamente: {}", usuario.email());
            return ApiResponse.exito(response, "Perfil de usuario obtenido exitosamente");
            
        } catch (Exception e) {
            log.error("Error al obtener perfil de usuario: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener perfil de usuario", e.getMessage());
        }
    }
    
    /**
     * Obtiene información del usuario por ID.
     * 
     * GET /api/v1/usuarios/{usuarioId}
     */
    @GetMapping("/{usuarioId}")
    public ApiResponse<UsuarioResponse> obtenerUsuarioPorId(@PathVariable Long usuarioId) {
        log.info("Solicitud para obtener usuario por ID: {}", usuarioId);
        
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId).orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
            UsuarioResponse response = preguntaMapper.toUsuarioResponse(usuario);
            
            log.info("Usuario obtenido exitosamente: {}", usuario.email());
            return ApiResponse.exito(response, "Usuario obtenido exitosamente");
            
        } catch (IllegalArgumentException e) {
            log.warn("Usuario no encontrado: {}", e.getMessage());
            return ApiResponse.error("Usuario no encontrado: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener usuario: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener usuario", e.getMessage());
        }
    }
    
    /**
     * Endpoint para desarrollo que simula la creación de un usuario anónimo.
     * 
     * POST /api/v1/usuarios/anonimo
     */
    @PostMapping("/anonimo")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UsuarioResponse> crearUsuarioAnonimo() {
        log.info("Solicitud para crear usuario anónimo");
        
        try {
            Usuario usuario = usuarioService.crearUsuarioAnonimo();
            UsuarioResponse response = preguntaMapper.toUsuarioResponse(usuario);
            
            log.info("Usuario anónimo creado exitosamente: {}", usuario.id());
            return ApiResponse.exito(response, "Usuario anónimo creado exitosamente");
            
        } catch (Exception e) {
            log.error("Error al crear usuario anónimo: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al crear usuario anónimo", e.getMessage());
        }
    }

    /**
     * Actualiza el perfil del usuario.
     *
     * PUT /v1/usuarios/{usuarioId}/perfil
     *
     * @param usuarioId ID del usuario a actualizar
     * @param request DTO con nuevo nombre y avatar
     * @return Usuario actualizado
     */
    @PutMapping("/{usuarioId}/perfil")
    public ApiResponse<UsuarioResponse> actualizarPerfil(
            @PathVariable Long usuarioId,
            @RequestBody ActualizarPerfilRequest request
    ) {
        log.info("Actualizando perfil usuario {} con nuevo nombre '{}' y avatar '{}'", usuarioId, request.nombre(), request.avatar());
        try {
            Usuario usuarioActualizado = usuarioService.actualizarPerfil(usuarioId, request.nombre(), request.avatar());
            UsuarioResponse response = preguntaMapper.toUsuarioResponse(usuarioActualizado);
            return ApiResponse.exito(response, "Perfil actualizado correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar perfil usuario {}: {}", usuarioId, e.getMessage(), e);
            return ApiResponse.error("No se pudo actualizar perfil: " + e.getMessage());
        }
    }

    /**
     * Verifica si un usuario puede usar el sistema.
     *
     * GET /v1/usuarios/{usuarioId}/puede-usar
     *
     * @param usuarioId ID del usuario
     * @return true si puede usar el sistema
     */
    @GetMapping("/{usuarioId}/puede-usar")
    public ApiResponse<Boolean> puedeUsarSistema(@PathVariable Long usuarioId) {
        log.info("Verificando si usuario {} puede usar el sistema", usuarioId);
        try {
            boolean puedeUsar = usuarioService.puedeUsarSistema(usuarioId);
            return ApiResponse.exito(puedeUsar, "Consulta realizada correctamente");
        } catch (Exception e) {
            log.error("Error al verificar permiso usuario {}: {}", usuarioId, e.getMessage(), e);
            return ApiResponse.error("No se pudo verificar permiso: " + e.getMessage());
        }
    }
}
