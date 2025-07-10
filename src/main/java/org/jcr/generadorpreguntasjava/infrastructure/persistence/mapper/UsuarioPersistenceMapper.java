package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    // === MAPPING DE USUARIO ===

    Usuario toDomain(UsuarioEntity entity);

    UsuarioEntity toEntity(Usuario domain);

    List<Usuario> toDomainUsuarioList(List<UsuarioEntity> entities);
}
