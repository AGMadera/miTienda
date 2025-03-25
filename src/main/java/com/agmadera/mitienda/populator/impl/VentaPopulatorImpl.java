package com.agmadera.mitienda.populator.impl;

import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.populator.VentaPopulator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VentaPopulatorImpl implements VentaPopulator {

    @Autowired
    private ModelMapper mapper;

    @Override
    public VentaDTO entity2Dto(VentaEntity entity) {
        VentaDTO dto = mapper.map(entity, VentaDTO.class);
        return dto;
    }

    @Override
    public VentaEntity dto2Entity(VentaDTO dto) {
        VentaEntity entity = mapper.map(dto, VentaEntity.class);
        return entity;
    }
}
