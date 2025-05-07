package com.agmadera.mitienda.models.request;

import com.agmadera.mitienda.Enum.TipoPago;
import com.agmadera.mitienda.models.ProductoVentaDTO;

import java.util.List;

public class VentaRequest {

    private List<ProductoVentaDTO> productoVentaDTOS;
    private boolean tecnico;

    private boolean garantia;

    //private Long folioGarantia;


    public boolean isTecnico() {
        return tecnico;
    }

    public void setTecnico(boolean tecnico) {
        this.tecnico = tecnico;
    }

    public List<ProductoVentaDTO> getProductoVentaDTOS() {
        return productoVentaDTOS;
    }

    public void setProductoVentaDTOS(List<ProductoVentaDTO> productoVentaDTOS) {
        this.productoVentaDTOS = productoVentaDTOS;
    }

    public boolean isGarantia() {
        return garantia;
    }

    public void setGarantia(boolean garantia) {
        this.garantia = garantia;
    }
}
