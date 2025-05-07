package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.GarantiaReembolso;
import com.agmadera.mitienda.repository.GarantiaReembolsoRepository;
import com.agmadera.mitienda.services.GarantiaReembolsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GarantiaReembolsoServiceImpl implements GarantiaReembolsoService {


    @Autowired
    private GarantiaReembolsoRepository repository;


    @Override
    public GarantiaReembolso guardar(GarantiaReembolso reembolso) {
        return repository.save(reembolso);
    }
}
