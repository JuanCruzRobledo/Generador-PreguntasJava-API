package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "lenguaje")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LenguajeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @OneToMany(mappedBy = "lenguaje")
    private List<PreguntaEntity> preguntas;

    @ManyToMany
    @JoinTable(
            name = "lenguaje_categoria",
            joinColumns = @JoinColumn(name = "lenguaje_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<CategoriaTematicaEntity> categoriasPrincipales;
}
