package com.agmadera.mitienda.services.impl;


import com.agmadera.mitienda.entities.GanaciaEntity;
import com.agmadera.mitienda.repository.GanaciaRepository;
import com.agmadera.mitienda.services.GanaciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GanaciaServiceImpl implements GanaciaService {

    @Autowired
    private GanaciaRepository ganaciaRepository;

    @Override
    public void guardarGanacia(GanaciaEntity ganacia) {
        ganaciaRepository.save(ganacia);
    }

    @Override
    public List<GanaciaEntity> mostrarGanancias() {
        return ganaciaRepository.findAll();
    }

    @Override
    public void guardarGanacias(List<GanaciaEntity> ganaciaEntities) {
        ganaciaRepository.saveAll(ganaciaEntities);

    }

    @Override
    public List<GanaciaEntity> buscarGanaciaVentas(Long idVenta, Long idProdRef) {
        Optional<List<GanaciaEntity>> ganaciaEntityList = ganaciaRepository.findByIdVentaRefAndIdProductoRef(idVenta,idProdRef);

        return ganaciaEntityList.get();
        //return null;
    }
}
