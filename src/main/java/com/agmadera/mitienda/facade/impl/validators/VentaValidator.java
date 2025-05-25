package com.agmadera.mitienda.facade.impl.validators;

import com.agmadera.mitienda.Enum.TipoPago;
import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.models.VentaDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class VentaValidator {

    public void validarVenta(VentaDTO ventaDTO) {
        Objects.requireNonNull(ventaDTO, "DTO de venta no puede ser nulo");

        if (ventaDTO.getProductoVentaDTOS() == null || ventaDTO.getProductoVentaDTOS().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener productos");
        }

        if (ventaDTO.getTipoPago() == TipoPago.VALE &&
                (ventaDTO.getFolioGarantia() == null || ventaDTO.getFolioGarantia().isEmpty())) {
            throw new GarantiaNoValidaException("Se requiere folio para pago con vale");
        }
    }
}
