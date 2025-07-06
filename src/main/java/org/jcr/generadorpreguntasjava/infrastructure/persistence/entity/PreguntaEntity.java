package org.jcr.generadorpreguntasjava.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jcr.generadorpreguntasjava.domain.model.Dificultad;

import java.util.List;
import java.util.Set;

/**
 * Entidad JPA para preguntas.
 */
@Entity
@Table(name = "preguntas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_java", nullable = false, columnDefinition = "TEXT")
    private String codigoJava;
    
    @Column(name = "enunciado", nullable = false, columnDefinition = "TEXT")
    private String enunciado;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "dificultad", nullable = false)
    private Dificultad dificultad;
    
    @Column(name = "respuesta_correcta", nullable = false, length = 500)
    private String respuestaCorrecta;
    
    @Column(name = "explicacion", nullable = false, columnDefinition = "TEXT")
    private String explicacion;
    
    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OpcionEntity> opciones;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "pregunta_tematica",
        joinColumns = @JoinColumn(name = "pregunta_id"),
        inverseJoinColumns = @JoinColumn(name = "tematica_id")
    )
    private Set<TematicaEntity> tematicas;
}
