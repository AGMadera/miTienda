package com.agmadera.mitienda.models;

import com.agmadera.mitienda.Enum.TipoPago;

import java.util.List;


public class VentaDTOBuilder {
    private final VentaDTO ventaDTO = new VentaDTO();

    private VentaDTOBuilder() {}

    public static VentaDTOBuilder create() {
        return new VentaDTOBuilder();
    }

    public VentaDTOBuilder withProductos(List<ProductoVentaDTO> productos) {
        ventaDTO.setProductoVentaDTOS(productos);
        return this;
    }

    public VentaDTOBuilder withTecnico(boolean isTecnico) {
        ventaDTO.setTecnico(isTecnico);
        return this;
    }

    public VentaDTOBuilder withTipoPago(TipoPago tipoPago) {
        ventaDTO.setTipoPago(tipoPago);
        return this;
    }

    public VentaDTOBuilder withFolioGarantia(String folioGarantia) {
        ventaDTO.setFolioGarantia(folioGarantia);
        return this;
    }

    public VentaDTO build() {
        calcularTotales();
        validar();
        return ventaDTO;
    }

    private void calcularTotales() {
        if (ventaDTO.getProductoVentaDTOS() != null) {
            double total = ventaDTO.getProductoVentaDTOS().stream()
                    .mapToDouble(p -> (p.getPrecio() - p.getDescuento()) * p.getCantidad())
                    .sum();
            ventaDTO.setTotalGenrealAntesDesc((float) total);
        }
    }

    private void validar() {
        if (ventaDTO.getProductoVentaDTOS() == null || ventaDTO.getProductoVentaDTOS().isEmpty()) {
            throw new IllegalStateException("La venta debe contener productos");
        }
    }
}