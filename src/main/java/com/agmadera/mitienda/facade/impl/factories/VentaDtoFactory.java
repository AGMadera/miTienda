package com.agmadera.mitienda.facade.impl.factories;

import com.agmadera.mitienda.facade.impl.handlers.ProductoVentaHandler;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.VentaDTOBuilder;
import com.agmadera.mitienda.models.request.VentaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VentaDtoFactory {
    private final ProductoVentaHandler productoVentaHandler;

    public VentaDTO crearDesdeRequest(VentaRequest ventaRequest) {
        return VentaDTOBuilder.create()
                .withProductos(procesarProductos(ventaRequest))
                .withTecnico(ventaRequest.isTecnico())
                .build();
    }

    public List<ProductoVentaDTO> procesarProductos(VentaRequest ventaRequest) {
        return ventaRequest.getProductoVentaDTOS().stream()
                .map(p -> productoVentaHandler.procesarProductoOrden(p, ventaRequest.isTecnico()))
                .collect(Collectors.toList());
    }
}
