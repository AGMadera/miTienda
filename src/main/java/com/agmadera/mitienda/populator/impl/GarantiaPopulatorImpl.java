package com.agmadera.mitienda.populator.impl;

import com.agmadera.mitienda.entities.GarantiaReembolso;
import com.agmadera.mitienda.entities.GarantiaVale;
import com.agmadera.mitienda.models.GarantiaDTO;
import com.agmadera.mitienda.models.response.ReembolsoResponse;
import com.agmadera.mitienda.models.response.ValeResponse;
import com.agmadera.mitienda.populator.GarantiaPopulator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GarantiaPopulatorImpl implements GarantiaPopulator {

    @Autowired
    private ModelMapper mapper;

    @Override
    public GarantiaReembolso dto2Entity(GarantiaDTO dto) {
        GarantiaReembolso entity = mapper.map(dto, GarantiaReembolso.class);
        return entity;
    }

    @Override
    public ReembolsoResponse entity2Response(GarantiaReembolso entity) {
        ReembolsoResponse response = mapper.map(entity, ReembolsoResponse.class);
        return response;
    }

    @Override
    public GarantiaDTO entity2DTO(GarantiaReembolso entity) {
        GarantiaDTO dto = mapper.map(entity, GarantiaDTO.class);
        return dto;
    }

    @Override
    public GarantiaVale dtoVale2Entity(GarantiaDTO dto) {
        GarantiaVale entity = mapper.map(dto, GarantiaVale.class);
        return entity;
    }

    @Override
    public ValeResponse valeEntityToResponse(GarantiaVale entity) {
        ValeResponse response = mapper.map(entity, ValeResponse.class);
        return response;
    }

    @Override
    public GarantiaVale responseToValeEntity(ValeResponse response) {
        GarantiaVale entity = mapper.map(response, GarantiaVale.class);
        return entity;
    }
}
