package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Enum.TipoPago;
import com.agmadera.mitienda.exceptions.DescuentoCalculationException;
import com.agmadera.mitienda.facade.DescuentoFacade;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.strategies.PagoStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DescuentoFacadeImpl implements DescuentoFacade {
    private final Map<TipoPago, PagoStrategy> estrategiasPago;


    @Override
    public float calcularDescuento(VentaDTO ventaDTO) {
        Objects.requireNonNull(ventaDTO, "El DTO de venta no puede ser nulo");

        try {
            PagoStrategy estrategia = estrategiasPago.get(ventaDTO.getTipoPago());
            if (estrategia == null) {
                throw new IllegalArgumentException("Tipo de pago no soportado: " + ventaDTO.getTipoPago());
            }
            return estrategia.calcularDescuento(ventaDTO);
        } catch (Exception e) {
            log.error("Error calculando descuento para venta {}: {}", ventaDTO.getId(), e.getMessage());
            throw new DescuentoCalculationException("Error calculando descuento", e);
        }
    }
}
