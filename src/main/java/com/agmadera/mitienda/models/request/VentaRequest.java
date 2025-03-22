package com.agmadera.mitienda.models.request;

import com.agmadera.mitienda.models.ProductoVentaDTO;

import java.util.List;

public class VentaRequest {

    private List<ProductoVentaDTO> productoVentaDTOS;
    private boolean tecnico;



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
}
