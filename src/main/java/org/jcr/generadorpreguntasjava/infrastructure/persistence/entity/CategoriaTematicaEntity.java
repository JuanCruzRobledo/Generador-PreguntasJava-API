package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categoria_principal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaTematicaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
    private String descripcion;

    //Bidirecionalidad
    @ManyToMany(mappedBy = "categoriasPrincipales", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<LenguajeEntity> lenguajes;

    @ManyToMany
    @JoinTable(
            name = "categoria_tag",
            joinColumns = @JoinColumn(name = "categoria_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagTematicaEntity> tagsTematicas;
}
