package com.agmadera.mitienda.populator;

import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.models.VentaDTO;

public interface VentaPopulator {
    VentaDTO entity2Dto(VentaEntity entity);
    VentaEntity dto2Entity(VentaDTO dto);
}
