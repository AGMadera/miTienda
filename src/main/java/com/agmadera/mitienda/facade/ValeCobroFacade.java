package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.models.response.ValeResponse;

public interface ValeCobroFacade {
    ValeResponse cobroVale(ValeResponse valeResponse);
    ValeResponse buscarVale(String folio);
}
