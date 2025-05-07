package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.models.GarantiaDTO;
import com.agmadera.mitienda.models.response.ReembolsoResponse;
import com.agmadera.mitienda.models.response.ValeResponse;

public interface GarantiaFacade {
    ReembolsoResponse guardarReembolso(GarantiaDTO reembolso);
    ValeResponse crearVale(GarantiaDTO garantiaDTO);
    /*ValeResponse actualizarVale(ValeResponse valeResponse);
    ValeResponse buscarVale(String folio);*/
}
