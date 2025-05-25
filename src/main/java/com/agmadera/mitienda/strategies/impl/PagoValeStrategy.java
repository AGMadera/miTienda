package com.agmadera.mitienda.strategies.impl;

import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.facade.impl.helper.ValeHelper;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.strategies.PagoStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagoValeStrategy implements PagoStrategy {
    private final ValeHelper valeHelper;

    @Override
    public float calcularDescuento(VentaDTO ventaDTO) {
        return valeHelper.procesarDescuentoPorVale(ventaDTO);
    }

    @Override
    public void validar(VentaDTO ventaDTO) {
        if (ventaDTO.getProductoVentaDTOS().size() > 1) {
            throw new GarantiaNoValidaException("Pago con vale solo admite 1 producto");
        }

        if (ventaDTO.getFolioGarantia() == null || ventaDTO.getFolioGarantia().isEmpty()) {
            throw new GarantiaNoValidaException("Folio de vale requerido");
        }
    }
}
