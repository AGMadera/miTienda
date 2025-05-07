package com.agmadera.mitienda.populator;

import com.agmadera.mitienda.entities.GarantiaReembolso;
import com.agmadera.mitienda.entities.GarantiaVale;
import com.agmadera.mitienda.models.GarantiaDTO;
import com.agmadera.mitienda.models.response.ReembolsoResponse;
import com.agmadera.mitienda.models.response.ValeResponse;

public interface GarantiaPopulator {
    GarantiaReembolso dto2Entity(GarantiaDTO dto);
    ReembolsoResponse entity2Response(GarantiaReembolso entity);
    GarantiaDTO entity2DTO(GarantiaReembolso entity);

    GarantiaVale dtoVale2Entity(GarantiaDTO dto);
    ValeResponse valeEntityToResponse(GarantiaVale enity);
    GarantiaVale responseToValeEntity(ValeResponse response);

}
