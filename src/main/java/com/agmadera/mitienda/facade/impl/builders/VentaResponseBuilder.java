package com.agmadera.mitienda.facade.impl.builders;

import com.agmadera.mitienda.Enum.TipoPago;
import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.populator.VentaPopulator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VentaResponseBuilder {

    private final VentaPopulator ventaPopulator;

    @Value("${mensaje.venta}")
    private String mensajeVenta;

    public VentaDTO construirRespuestaVenta(VentaEntity ventaGuardada, VentaDTO ventaOriginal, float descuentoTotal) {
        VentaDTO respuesta = ventaPopulator.entity2Dto(ventaGuardada);
        respuesta.setMensaje(mensajeVenta);
        respuesta.setDescuentosEnTotalGen(descuentoTotal);

        if (ventaOriginal.getTipoPago().equals(TipoPago.VALE)) {
            aplicarDescuentoVale(respuesta, ventaOriginal, descuentoTotal);
            respuesta.setFolioGarantia(ventaOriginal.getFolioGarantia());
        }

        return respuesta;
    }

    private void aplicarDescuentoVale(VentaDTO respuesta, VentaDTO original, float descuentoTotal) {
        if (!respuesta.getProductoVentaDTOS().isEmpty()) {
            ProductoVentaDTO primerProducto = respuesta.getProductoVentaDTOS().get(0);
            primerProducto.setDescuento(
                    primerProducto.getDescuento() +
                            (descuentoTotal - original.getDescuentosEnTotalGen())
            );
        }
    }
}