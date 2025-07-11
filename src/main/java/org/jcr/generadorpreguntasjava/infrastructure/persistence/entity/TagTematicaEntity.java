package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Entidad JPA para temáticas.
 */
@Entity
@Table(name = "tematicas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagTematicaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;
    
    @Column(name = "contador_usos", nullable = false)
    private Integer contadorUsos = 0;
    
    @Column(name = "timestamp_ultimo_uso")
    private LocalDateTime timestampUltimoUso;
    
    @ManyToMany(mappedBy = "tagsTematicas", fetch = FetchType.LAZY)
    private Set<PreguntaEntity> preguntas;

    @ManyToMany(mappedBy = "tagsTematicas")
    private List<CategoriaTematicaEntity> categoriasPrincipales;

    public TagTematicaEntity(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagTematicaEntity)) return false;
        TagTematicaEntity that = (TagTematicaEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}