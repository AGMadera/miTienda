package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.Enum.TipoGarantia;
import com.agmadera.mitienda.models.VentaDTO;

public interface GananciaFacade {
    void guardarGanacia(VentaDTO ventaDTO);
    void actualizarGanacia(VentaDTO ventaDTO, Long idProdRef, TipoGarantia tipoGarantia);


}
