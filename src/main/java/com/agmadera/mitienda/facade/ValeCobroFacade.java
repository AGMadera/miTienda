package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.response.ValeResponse;

public interface ValeCobroFacade {
    ValeResponse cobroVale(ValeResponse valeResponse, VentaDTO dto);
    ValeResponse buscarVale(String folio);
}
