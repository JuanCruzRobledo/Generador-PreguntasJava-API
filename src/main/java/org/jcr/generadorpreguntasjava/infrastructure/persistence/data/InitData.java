package org.jcr.generadorpreguntasjava.infrastructure.persistence.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitData {

    private final LenguajeData lenguajeData;
    private final CategoriaTematicaData categoriaTematicaData;
    private final TagTematicaData tagTematicaData;

    public void initData(){
        tagTematicaData.init();
        categoriaTematicaData.init();
        lenguajeData.init();
    }
}
