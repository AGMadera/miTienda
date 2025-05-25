package com.agmadera.mitienda.services.impl;


import com.agmadera.mitienda.entities.GananciaEntity;
import com.agmadera.mitienda.repositories.GananciaRepository;
import com.agmadera.mitienda.services.GananciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GananciaServiceImpl implements GananciaService {

    @Autowired
    private GananciaRepository gananciaRepository;

    @Override
    public void guardarGanacia(GananciaEntity ganacia) {
        gananciaRepository.save(ganacia);
    }

    @Override
    public List<GananciaEntity> mostrarGanancias() {
        return gananciaRepository.findAll();
    }

    @Override
    public void guardarGanacias(List<GananciaEntity> ganaciaEntities) {
        gananciaRepository.saveAll(ganaciaEntities);

    }

    @Override
    public List<GananciaEntity> buscarGanaciaVentas(Long idVenta, Long idProdRef) {
        Optional<List<GananciaEntity>> ganaciaEntityList = gananciaRepository.findByIdVentaRefAndIdProductoRef(idVenta,idProdRef);

        return ganaciaEntityList.get();
        //return null;
    }
}
