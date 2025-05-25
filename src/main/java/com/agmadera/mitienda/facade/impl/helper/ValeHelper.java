package com.agmadera.mitienda.facade.impl.helper;

import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.facade.ValeCobroFacade;
import com.agmadera.mitienda.models.HistorialGarantiaValeDto;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.response.ValeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValeHelper {
    private static final String ERROR_GARANTIA_SALDO = "Saldo de vale es 0 o insuficiente";

    private final ValeCobroFacade valeCobroFacade;

    public float procesarDescuentoPorVale(VentaDTO ventaDTO) {
        if (ventaDTO.getProductoVentaDTOS().size() > 1) {
            throw new GarantiaNoValidaException("Pago con vale solo admite 1 producto");
        }

        ValeResponse vale = valeCobroFacade.buscarVale(ventaDTO.getFolioGarantia());
        validarSaldoVale(vale.getSaldoActual());

        float descuentoVale = Math.min(vale.getSaldoActual(), ventaDTO.getTotalGenrealAntesDesc());

        // Actualizar vale
        vale.setSaldoActual(vale.getSaldoActual() - descuentoVale);
        vale.getHistorialGarantiaValesDtos().add(crearHistorialGarantia(vale, ventaDTO, descuentoVale));
        valeCobroFacade.cobroVale(vale, ventaDTO);

        return descuentoVale;
    }
    private void validarSaldoVale(float saldo) {
        if (saldo <= 0) {
            throw new GarantiaNoValidaException(ERROR_GARANTIA_SALDO);
        }
    }
    private HistorialGarantiaValeDto crearHistorialGarantia(ValeResponse vale, VentaDTO ventaDTO, float descuento) {
        return HistorialGarantiaValeDto.builder()
                .saldo(vale.getSaldoActual())
                .idProductoVenta(ventaDTO.getProductoVentaDTOS().get(0).getIdProductoRef())
                .fecha(new Date())
                .build();
    }
}
