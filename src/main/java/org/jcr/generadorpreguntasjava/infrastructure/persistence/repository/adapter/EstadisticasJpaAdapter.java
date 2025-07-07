package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasUsuario;
import org.jcr.generadorpreguntasjava.domain.model.SesionRespuesta;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.EstadisticasUsuarioEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.SesionRespuestaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.EstadisticasPersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataEstadisticasRepository;
import org.jcr.generadorpreguntasjava.port.in.ConsultarEstadisticasPort;
import org.jcr.generadorpreguntasjava.port.in.GuardarEstadisticasPort;
import org.jcr.generadorpreguntasjava.port.out.EstadisticasRepositoryPort;
import org.jcr.generadorpreguntasjava.port.out.SesionRespuestaRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA para manejar estadísticas de usuario.
 */

@Component
@RequiredArgsConstructor
@Transactional
public class EstadisticasJpaAdapter implements EstadisticasRepositoryPort {

    private final SpringDataEstadisticasRepository estadisticasRepository;
    private final EstadisticasPersistenceMapper estadisticasPersistenceMapper;

    @Override
    public EstadisticasUsuario guardar(EstadisticasUsuario estadisticas) {
        var entidad = estadisticasPersistenceMapper.toEntity(estadisticas);
        var guardado = estadisticasRepository.save(entidad);
        return estadisticasPersistenceMapper.toDomain(guardado);
    }

    @Override
    public Optional<EstadisticasUsuario> buscarPorUsuario(Long usuarioId) {
        return estadisticasRepository.findByUsuarioId(usuarioId)
                .map(estadisticasPersistenceMapper::toDomain);
    }

    @Override
    public List<EstadisticasUsuario> obtenerTodas() {
        return estadisticasPersistenceMapper.toDomainList(
                estadisticasRepository.findAll()
        );
    }

    @Override
    public List<EstadisticasUsuario> obtenerDeUsuariosActivos() {
        return estadisticasPersistenceMapper.toDomainList(
                estadisticasRepository.findDeUsuariosActivos()
        );
    }

    @Override
    public boolean eliminarPorUsuario(Long usuarioId) {
        return estadisticasRepository.deleteByUsuarioId(usuarioId) > 0;
    }

    @Override
    public boolean existenPorUsuario(Long usuarioId) {
        return estadisticasRepository.existsByUsuarioId(usuarioId);
    }

    @Override
    public long contarUsuariosConEstadisticas() {
        return estadisticasRepository.count();
    }

    @Override
    public List<EstadisticasUsuario> obtenerRankingPorAciertos(int limite) {
        var resultados = estadisticasRepository
                .findTopByOrderByPorcentajeAciertosDesc(PageRequest.of(0, limite));
        return estadisticasPersistenceMapper.toDomainList(resultados);
    }

    @Override
    public List<EstadisticasUsuario> obtenerRankingPorVolumen(int limite) {
        var resultados = estadisticasRepository
                .findTopByOrderByTotalPreguntasDesc(PageRequest.of(0, limite));
        return estadisticasPersistenceMapper.toDomainList(resultados);
    }

    @Override
    public List<EstadisticasUsuario> obtenerRankingPorTiempo(int limite) {
        var resultados = estadisticasRepository
                .findTopByOrderByTiempoPromedioAsc(PageRequest.of(0, limite));
        return estadisticasPersistenceMapper.toDomainList(resultados);
    }


    /*
    private final SpringDataEstadisticasRepository estadisticasRepository;
    private final SesionRespuestaRepositoryPort sesionRepository;
    private final EstadisticasPersistenceMapper mapper;
    
    @Override
    public void actualizarEstadisticas(String idUsuario, EstadisticasUsuario estadisticas) {
        EstadisticasUsuarioEntity entity = estadisticasRepository
            .findByIdUsuario(idUsuario)
            .orElse(new EstadisticasUsuarioEntity());
            
        entity.setIdUsuario(idUsuario);
        entity.setTotalPreguntas(estadisticas.totalPreguntas());
        entity.setRespuestasCorrectas(estadisticas.respuestasCorrectas());
        entity.setPorcentajeAciertos(estadisticas.porcentajeAciertos());
        entity.setTiempoPromedioSegundos(estadisticas.tiempoPromedioSegundos());
        entity.setEstadisticasJson(mapper.toJson(estadisticas));
        entity.setFechaActualizacion(LocalDateTime.now());
        
        estadisticasRepository.save(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<EstadisticasUsuario> obtenerEstadisticasPorUsuario(String idUsuario) {
        return estadisticasRepository.findByIdUsuario(idUsuario)
            .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerSesionesPorUsuario(String idUsuario, int limite) {
        List<SesionRespuestaEntity> sesiones = sesionRepository
            .findByIdUsuarioOrderByFechaRespuestaDesc(idUsuario, 
                org.springframework.data.domain.PageRequest.of(0, limite));
        return sesiones.stream()
            .map(mapper::toDomain)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerSesionesPorUsuarioYPeriodo(
            String idUsuario, 
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin) {
        List<SesionRespuestaEntity> sesiones = sesionRepository
            .findByIdUsuarioAndFechaRespuestaBetween(idUsuario, fechaInicio, fechaFin);
        return sesiones.stream()
            .map(mapper::toDomain)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasGlobales obtenerEstadisticasGlobales() {
        long totalUsuarios = estadisticasRepository.count();
        
        if (totalUsuarios == 0) {
            return new EstadisticasGlobales(0, 0.0, 0);
        }
        
        Double promedioAciertos = estadisticasRepository.findPromedioPorcentajeAciertos();
        Long totalSesiones = sesionRepository.count();
        
        return new EstadisticasGlobales(
            totalUsuarios,
            promedioAciertos != null ? promedioAciertos : 0.0,
            totalSesiones != null ? totalSesiones : 0
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public RankingUsuarios obtenerRankingUsuarios(int limite) {
        List<EstadisticasUsuarioEntity> mejoresUsuarios = estadisticasRepository
            .findTopByOrderByPorcentajeAciertosDescTotalPreguntasDesc(
                org.springframework.data.domain.PageRequest.of(0, limite));
                
        List<RankingUsuarios.UsuarioRanking> ranking = mejoresUsuarios.stream()
            .map(entity -> new RankingUsuarios.UsuarioRanking(
                entity.getIdUsuario(),
                entity.getTotalPreguntas(),
                entity.getRespuestasCorrectas(),
                entity.getPorcentajeAciertos()
            ))
            .toList();
            
        return new RankingUsuarios(ranking);
    }*/
}
