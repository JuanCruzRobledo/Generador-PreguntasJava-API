package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidad JPA para temáticas.
 */
@Entity
@Table(name = "tematicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TematicaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;
    
    @Column(name = "contador_usos", nullable = false)
    private Integer contadorUsos = 0;
    
    @Column(name = "timestamp_ultimo_uso")
    private LocalDateTime timestampUltimoUso;
    
    @ManyToMany(mappedBy = "tematicas", fetch = FetchType.LAZY)
    private Set<PreguntaEntity> preguntas;
}
