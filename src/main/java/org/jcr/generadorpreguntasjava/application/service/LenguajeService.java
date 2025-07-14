package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Lenguaje;
import org.jcr.generadorpreguntasjava.port.in.ConsultarLenguajesPort;
import org.jcr.generadorpreguntasjava.port.out.LenguajeRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 *
 *
 * -
 * -
 * -
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LenguajeService implements ConsultarLenguajesPort {

    private final LenguajeRepositoryPort lenguajeRepository;

    @Override
    public List<Lenguaje> obtenerTodosLosLenguajes() {
        return lenguajeRepository.obtenerLenguajes();
    }

    @Override
    public Lenguaje obtenerPorId(Long id) {
        return lenguajeRepository.obtenerLenguaje(id);
    }
}
