package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.port.in.ConsultarEstadisticasPort;
import org.jcr.generadorpreguntasjava.port.in.RegistrarRespuestaPort;
import org.jcr.generadorpreguntasjava.port.in.ValidarRespuestaPort;
import org.jcr.generadorpreguntasjava.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestión de estadísticas y sesiones de respuesta.
 * Implementa los casos de uso relacionados con estadísticas, timing y respuestas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EstadisticasService implements 
    RegistrarRespuestaPort, ConsultarEstadisticasPort {
    
    private final SesionRespuestaRepositoryPort sesionRepositoryPort;
    private final EstadisticasRepositoryPort estadisticasRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PreguntaRepositoryPort preguntaRepositoryPort;
    private final ValidarRespuestaPort validarRespuestaPort;
    
    // ===== IMPLEMENTACIÓN DE RegistrarRespuestaPort =====
    
    @Override
    public SesionRespuesta iniciarRespuesta(Long usuarioId, Long preguntaId) {
        log.info("Iniciando sesión de respuesta para usuario {} y pregunta {}", usuarioId, preguntaId);
        
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        if (preguntaId == null) {
            throw new IllegalArgumentException("El ID de la pregunta no puede ser nulo");
        }
        
        try {
            // Verificar que el usuario existe
            usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            // Verificar que la pregunta existe
            preguntaRepositoryPort.buscarPorId(preguntaId)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con ID: " + preguntaId));
            
            // Verificar que no hay una sesión en progreso
            Optional<SesionRespuesta> sesionExistente = sesionRepositoryPort.buscarSesionEnProgreso(usuarioId, preguntaId);
            if (sesionExistente.isPresent()) {
                throw new RuntimeException("Ya existe una sesión en progreso para esta pregunta");
            }
            
            // Crear nueva sesión
            SesionRespuesta nuevaSesion = new SesionRespuesta(usuarioId, preguntaId);
            nuevaSesion.validar();
            
            SesionRespuesta sesionGuardada = sesionRepositoryPort.guardar(nuevaSesion);
            log.info("Sesión iniciada exitosamente con ID: {}", sesionGuardada.id());
            
            return sesionGuardada;
            
        } catch (Exception e) {
            log.error("Error al iniciar sesión de respuesta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al iniciar sesión de respuesta", e);
        }
    }
    
    @Override
    public SesionRespuesta completarRespuesta(Long sesionId, String respuesta) {
        log.info("Completando sesión de respuesta: {}", sesionId);
        
        if (sesionId == null) {
            throw new IllegalArgumentException("El ID de la sesión no puede ser nulo");
        }
        
        if (respuesta == null || respuesta.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta no puede estar vacía");
        }
        
        try {
            // Buscar la sesión
            SesionRespuesta sesion = sesionRepositoryPort.buscarPorId(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con ID: " + sesionId));
            
            if (sesion.estaCompleta()) {
                throw new RuntimeException("La sesión ya está completada");
            }
            
            // Validar la respuesta
            ValidarRespuestaPort.ResultadoValidacion resultado = 
                validarRespuestaPort.validarRespuesta(sesion.preguntaId(), respuesta);
            
            // Completar la sesión
            SesionRespuesta sesionCompletada = sesion.completarRespuesta(respuesta, resultado.esCorrecta());
            sesionCompletada.validar();
            
            // Guardar sesión completada
            SesionRespuesta sesionGuardada = sesionRepositoryPort.guardar(sesionCompletada);
            
            // Recalcular estadísticas del usuario de forma asíncrona
            recalcularEstadisticasAsync(sesion.usuarioId());
            
            log.info("Sesión completada exitosamente: {} - Respuesta: {}", 
                    sesionId, resultado.esCorrecta() ? "CORRECTA" : "INCORRECTA");
            
            return sesionGuardada;
            
        } catch (Exception e) {
            log.error("Error al completar sesión de respuesta {}: {}", sesionId, e.getMessage(), e);
            throw new RuntimeException("Error al completar sesión de respuesta", e);
        }
    }
    
    @Override
    public ResultadoRespuesta responderPregunta(Long usuarioId, Long preguntaId, String respuesta) {
        log.info("Respondiendo pregunta {} para usuario {}", preguntaId, usuarioId);
        
        try {
            // Buscar sesión en progreso
            Optional<SesionRespuesta> sesionOpt = buscarSesionEnProgreso(usuarioId, preguntaId);
            
            SesionRespuesta sesion;
            if (sesionOpt.isPresent()) {
                sesion = sesionOpt.get();
            } else {
                // Iniciar nueva sesión si no existe
                sesion = iniciarRespuesta(usuarioId, preguntaId);
            }
            
            // Completar la respuesta
            SesionRespuesta sesionCompletada = completarRespuesta(sesion.id(), respuesta);
            
            // Obtener información adicional para el resultado
            ValidarRespuestaPort.ResultadoValidacion validacion = 
                validarRespuestaPort.validarRespuesta(preguntaId, respuesta);
            
            return new ResultadoRespuesta(
                sesionCompletada.id(),
                usuarioId,
                preguntaId,
                respuesta,
                validacion.esCorrecta(),
                sesionCompletada.getTiempoRespuestaMillis(),
                validacion.explicacion(),
                validacion.respuestaCorrecta()
            );
            
        } catch (Exception e) {
            log.error("Error al responder pregunta {}: {}", preguntaId, e.getMessage(), e);
            throw new RuntimeException("Error al responder pregunta", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SesionRespuesta> buscarSesionEnProgreso(Long usuarioId, Long preguntaId) {
        return sesionRepositoryPort.buscarSesionEnProgreso(usuarioId, preguntaId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerSesionesPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        return sesionRepositoryPort.obtenerPorUsuario(usuarioId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerSesionesCompletadas(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        return sesionRepositoryPort.obtenerCompletadasPorUsuario(usuarioId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerUltimasSesiones(Long usuarioId, int limite) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor a 0");
        }
        
        return sesionRepositoryPort.obtenerUltimasSesiones(usuarioId, limite);
    }
    
    @Override
    public int cancelarSesionesAbandonadas() {
        log.info("Cancelando sesiones abandonadas");
        return sesionRepositoryPort.eliminarSesionesAbandonadas();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean tieneSesionesEnProgreso(Long usuarioId) {
        List<SesionRespuesta> sesionesEnProgreso = sesionRepositoryPort.obtenerEnProgresoPorUsuario(usuarioId);
        return !sesionesEnProgreso.isEmpty();
    }
    
    // ===== IMPLEMENTACIÓN DE ConsultarEstadisticasPort =====
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasUsuario obtenerEstadisticas(Long usuarioId) {
        log.debug("Obteniendo estadísticas para usuario: {}", usuarioId);
        
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        try {
            // Verificar que el usuario existe
            usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            // Buscar estadísticas existentes
            Optional<EstadisticasUsuario> estadisticasOpt = estadisticasRepositoryPort.buscarPorUsuario(usuarioId);
            
            if (estadisticasOpt.isPresent() && estadisticasOpt.get().estanActualizadas()) {
                return estadisticasOpt.get();
            }
            
            // Recalcular si no existen o están desactualizadas
            return recalcularEstadisticas(usuarioId);
            
        } catch (Exception e) {
            log.error("Error al obtener estadísticas del usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener estadísticas", e);
        }
    }
    
    @Override
    public EstadisticasUsuario recalcularEstadisticas(Long usuarioId) {
        log.info("Recalculando estadísticas para usuario: {}", usuarioId);
        
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        try {
            // Verificar que el usuario existe
            usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            // Obtener todas las sesiones completadas
            List<SesionRespuesta> sesionesCompletadas = sesionRepositoryPort.obtenerCompletadasPorUsuario(usuarioId);
            
            if (sesionesCompletadas.isEmpty()) {
                // Usuario sin sesiones - crear estadísticas vacías
                EstadisticasUsuario estadisticasVacias = new EstadisticasUsuario(usuarioId);
                return estadisticasRepositoryPort.guardar(estadisticasVacias);
            }
            
            // Calcular estadísticas generales
            int totalPreguntas = sesionesCompletadas.size();
            int respuestasCorrectas = (int) sesionesCompletadas.stream()
                .filter(SesionRespuesta::esCorrecta)
                .count();
            double porcentajeAciertos = (double) respuestasCorrectas / totalPreguntas * 100.0;
            
            // Calcular tiempo promedio
            Duration tiempoPromedio = calcularTiempoPromedio(sesionesCompletadas);
            
            // Calcular estadísticas por dificultad
            Map<Dificultad, EstadisticasPorDificultad> estadisticasPorDificultad = 
                calcularEstadisticasPorDificultad(sesionesCompletadas);
            
            // Calcular estadísticas por temática
            Map<String, EstadisticasPorTematica> estadisticasPorTematica = 
                calcularEstadisticasPorTematica(sesionesCompletadas);
            
            // Crear objeto de estadísticas
            EstadisticasUsuario estadisticas = new EstadisticasUsuario(
                usuarioId, totalPreguntas, respuestasCorrectas, porcentajeAciertos,
                tiempoPromedio, estadisticasPorDificultad, estadisticasPorTematica
            );
            
            estadisticas.validar();
            
            // Guardar estadísticas
            EstadisticasUsuario estadisticasGuardadas = estadisticasRepositoryPort.guardar(estadisticas);
            
            log.info("Estadísticas recalculadas para usuario {}: {} preguntas, {}% aciertos", 
                    usuarioId, totalPreguntas, String.format("%.1f", porcentajeAciertos));
            
            return estadisticasGuardadas;
            
        } catch (Exception e) {
            log.error("Error al recalcular estadísticas del usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al recalcular estadísticas", e);
        }
    }
    
    // ===== MÉTODOS AUXILIARES PRIVADOS =====
    
    private void recalcularEstadisticasAsync(Long usuarioId) {
        // En una implementación real, esto se haría de forma asíncrona
        // Por ahora lo hacemos sincrónicamente
        try {
            recalcularEstadisticas(usuarioId);
        } catch (Exception e) {
            log.warn("Error al recalcular estadísticas de forma asíncrona para usuario {}: {}", 
                    usuarioId, e.getMessage());
        }
    }
    
    private Duration calcularTiempoPromedio(List<SesionRespuesta> sesiones) {
        if (sesiones.isEmpty()) {
            return Duration.ZERO;
        }
        
        // Filtrar solo respuestas válidas (tiempo razonable)
        List<SesionRespuesta> sesionesValidas = sesiones.stream()
            .filter(SesionRespuesta::esRespuestaValida)
            .collect(Collectors.toList());
        
        if (sesionesValidas.isEmpty()) {
            return Duration.ZERO;
        }
        
        long tiempoTotalMs = sesionesValidas.stream()
            .mapToLong(SesionRespuesta::getTiempoRespuestaMillis)
            .sum();
        
        long tiempoPromedioMs = tiempoTotalMs / sesionesValidas.size();
        return Duration.ofMillis(tiempoPromedioMs);
    }
    
    private Map<Dificultad, EstadisticasPorDificultad> calcularEstadisticasPorDificultad(
            List<SesionRespuesta> sesiones) {
        
        Map<Dificultad, EstadisticasPorDificultad> resultado = new HashMap<>();
        
        // Agrupar sesiones por dificultad
        Map<Dificultad, List<SesionRespuesta>> sesionesPorDificultad = sesiones.stream()
            .collect(Collectors.groupingBy(this::obtenerDificultadDeSesion));
        
        for (Map.Entry<Dificultad, List<SesionRespuesta>> entry : sesionesPorDificultad.entrySet()) {
            Dificultad dificultad = entry.getKey();
            List<SesionRespuesta> sesionesDeEsaDificultad = entry.getValue();
            
            int total = sesionesDeEsaDificultad.size();
            int correctas = (int) sesionesDeEsaDificultad.stream()
                .filter(SesionRespuesta::esCorrecta)
                .count();
            
            Duration tiempoPromedio = calcularTiempoPromedio(sesionesDeEsaDificultad);
            
            EstadisticasPorDificultad estadisticas = new EstadisticasPorDificultad(
                dificultad, total, correctas, tiempoPromedio
            );
            
            resultado.put(dificultad, estadisticas);
        }
        
        return resultado;
    }
    
    private Map<String, EstadisticasPorTematica> calcularEstadisticasPorTematica(
            List<SesionRespuesta> sesiones) {
        
        Map<String, EstadisticasPorTematica> resultado = new HashMap<>();
        
        // Agrupar sesiones por temática
        Map<String, List<SesionRespuesta>> sesionesPorTematica = sesiones.stream()
            .collect(Collectors.groupingBy(this::obtenerTematicaDeSesion));
        
        for (Map.Entry<String, List<SesionRespuesta>> entry : sesionesPorTematica.entrySet()) {
            String tematica = entry.getKey();
            List<SesionRespuesta> sesionesDeEsaTematica = entry.getValue();
            
            int total = sesionesDeEsaTematica.size();
            int correctas = (int) sesionesDeEsaTematica.stream()
                .filter(SesionRespuesta::esCorrecta)
                .count();
            
            Duration tiempoPromedio = calcularTiempoPromedio(sesionesDeEsaTematica);
            
            EstadisticasPorTematica estadisticas = new EstadisticasPorTematica(
                tematica, total, correctas, tiempoPromedio
            );
            
            resultado.put(tematica, estadisticas);
        }
        
        return resultado;
    }
    
    private Dificultad obtenerDificultadDeSesion(SesionRespuesta sesion) {
        // Obtener la dificultad de la pregunta asociada a la sesión
        return preguntaRepositoryPort.buscarPorId(sesion.preguntaId())
            .map(Pregunta::dificultad)
            .orElse(Dificultad.FACIL); // Valor por defecto
    }
    
    private String obtenerTematicaDeSesion(SesionRespuesta sesion) {
        // Obtener la temática principal de la pregunta asociada a la sesión
        return preguntaRepositoryPort.buscarPorId(sesion.preguntaId())
            .map(pregunta -> {
                Tematica tematicaPrincipal = pregunta.getTematicaPrincipal();
                return tematicaPrincipal != null ? tematicaPrincipal.nombre() : "Sin temática";
            })
            .orElse("Sin temática");
    }
    
    // ===== MÉTODOS RESTANTES DE ConsultarEstadisticasPort =====
    
    @Override
    @Transactional(readOnly = true)
    public List<EstadisticasPorDificultad> obtenerEstadisticasPorDificultad(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        EstadisticasUsuario estadisticas = obtenerEstadisticas(usuarioId);
        return new ArrayList<>(estadisticas.porDificultad().values());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EstadisticasPorTematica> obtenerEstadisticasPorTematica(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        EstadisticasUsuario estadisticas = obtenerEstadisticas(usuarioId);
        return new ArrayList<>(estadisticas.porTematica().values());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EstadisticasPorTematica> obtenerRankingTematicas(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        return obtenerEstadisticasPorTematica(usuarioId).stream()
            .filter(EstadisticasPorTematica::tieneDatos)
            .sorted((e1, e2) -> Double.compare(e2.porcentajeAciertos(), e1.porcentajeAciertos()))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasPorDificultad obtenerEstadisticasPorDificultad(Long usuarioId, Dificultad dificultad) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (dificultad == null) {
            throw new IllegalArgumentException("La dificultad no puede ser nula");
        }
        
        EstadisticasUsuario estadisticas = obtenerEstadisticas(usuarioId);
        return estadisticas.porDificultad().getOrDefault(dificultad, 
            EstadisticasPorDificultad.vacia(dificultad));
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasPorTematica obtenerEstadisticasPorTematica(Long usuarioId, String tematica) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (tematica == null || tematica.trim().isEmpty()) {
            throw new IllegalArgumentException("La temática no puede estar vacía");
        }
        
        EstadisticasUsuario estadisticas = obtenerEstadisticas(usuarioId);
        return estadisticas.porTematica().getOrDefault(tematica, 
            EstadisticasPorTematica.vacia(tematica));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean tieneEstadisticas(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        return estadisticasRepositoryPort.existenPorUsuario(usuarioId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResumenProgreso obtenerResumenProgreso(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        EstadisticasUsuario estadisticas = obtenerEstadisticas(usuarioId);
        
        // Determinar nivel del usuario
        String nivelUsuario;
        if (estadisticas.esPrincipiante()) {
            nivelUsuario = "Principiante";
        } else if (estadisticas.esExperimentado()) {
            nivelUsuario = "Avanzado";
        } else {
            nivelUsuario = "Intermedio";
        }
        
        // Obtener favoritos
        Dificultad dificultadFavorita = estadisticas.getDificultadConMejorRendimiento();
        String tematicaFavorita = estadisticas.getTematicaConMejorRendimiento();
        
        return new ResumenProgreso(
            estadisticas.totalPreguntas(),
            estadisticas.respuestasCorrectas(),
            estadisticas.porcentajeAciertos(),
            estadisticas.getTiempoPromedioFormateado(),
            nivelUsuario,
            dificultadFavorita != null ? dificultadFavorita.name().toLowerCase() : "ninguna",
            tematicaFavorita != null ? tematicaFavorita : "ninguna",
            estadisticas.tieneBuenRendimiento()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasComparativas obtenerEstadisticasComparativas(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        EstadisticasUsuario estadisticasUsuario = obtenerEstadisticas(usuarioId);
        List<EstadisticasUsuario> todasLasEstadisticas = estadisticasRepositoryPort.obtenerDeUsuariosActivos();
        
        if (todasLasEstadisticas.isEmpty()) {
            return new EstadisticasComparativas(0.0, 0L, 1, 1, false);
        }
        
        // Calcular promedios globales
        double porcentajePromedioGlobal = todasLasEstadisticas.stream()
            .mapToDouble(EstadisticasUsuario::porcentajeAciertos)
            .average()
            .orElse(0.0);
        
        long tiempoPromedioGlobal = (long) todasLasEstadisticas.stream()
            .mapToLong(EstadisticasUsuario::getTiempoPromedioSegundos)
            .average()
            .orElse(0.0);
        
        // Calcular posición en ranking
        List<EstadisticasUsuario> rankingPorAciertos = todasLasEstadisticas.stream()
            .sorted((e1, e2) -> Double.compare(e2.porcentajeAciertos(), e1.porcentajeAciertos()))
            .collect(Collectors.toList());
        
        int posicion = -1;
        for (int i = 0; i < rankingPorAciertos.size(); i++) {
            if (rankingPorAciertos.get(i).usuarioId().equals(usuarioId)) {
                posicion = i + 1;
                break;
            }
        }
        
        boolean superaPromedio = estadisticasUsuario.porcentajeAciertos() > porcentajePromedioGlobal;
        
        return new EstadisticasComparativas(
            porcentajePromedioGlobal,
            tiempoPromedioGlobal,
            posicion > 0 ? posicion : todasLasEstadisticas.size(),
            todasLasEstadisticas.size(),
            superaPromedio
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public RankingGlobal obtenerRankingsGlobales(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor a 0");
        }
        
        List<EstadisticasUsuario> topPorAciertos = estadisticasRepositoryPort.obtenerRankingPorAciertos(limite);
        List<EstadisticasUsuario> topPorVolumen = estadisticasRepositoryPort.obtenerRankingPorVolumen(limite);
        List<EstadisticasUsuario> topPorTiempo = estadisticasRepositoryPort.obtenerRankingPorTiempo(limite);
        
        return new RankingGlobal(topPorAciertos, topPorVolumen, topPorTiempo);
    }
}
