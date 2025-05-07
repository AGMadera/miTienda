package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.GarantiaVale;
import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.repository.GarantiaValeRespository;
import com.agmadera.mitienda.services.GarantiaValeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GarantiaValeServiceImpl implements GarantiaValeService {
    @Autowired
    private GarantiaValeRespository valeRespository;

    @Override
    public GarantiaVale guardar(GarantiaVale vale) {
        return valeRespository.save(vale);
    }

    @Override
    public GarantiaVale buscarValeFolio(String folio) {
        Optional<GarantiaVale> valeOpt = valeRespository.findByFolio(folio);
        if(valeOpt.isEmpty()){
            //TODO EXCEPTION
            throw new GarantiaNoValidaException("No existe folio!!");
        }
        return valeOpt.get();
    }


}
