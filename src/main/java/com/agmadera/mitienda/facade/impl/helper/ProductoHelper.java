package com.agmadera.mitienda.facade.impl.helper;

import com.agmadera.mitienda.facade.PrecioFacade;
import com.agmadera.mitienda.facade.StockFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.populator.ProductoPopulator;
import org.springframework.stereotype.Component;

@Component
public class ProductoHelper {
    private final ProductoPopulator populator;
    private final StockFacade stockFacade;
    private final PrecioFacade precioFacade;

    public ProductoHelper(ProductoPopulator populator, StockFacade stockFacade, PrecioFacade precioFacade) {
        this.populator = populator;
        this.stockFacade = stockFacade;
        this.precioFacade = precioFacade;
    }

    //builder
    public ProductoDTO actualizarProductoCompleto(ProductoDTO dto, ProductoDTO productoDTODb) {
        actualizarHistorialStock(productoDTODb, dto);
        actualizarCompatiblesProducto(productoDTODb, dto);
        actualizarPrecio(productoDTODb, dto);

        productoDTODb.getStockDTO().setUnidadesExistencia(
                stockFacade.calcularStockExistencia(dto, productoDTODb)
        );

        return productoDTODb;
    }


    public void actualizarCompatiblesProducto(ProductoDTO productoDTODb, ProductoDTO dto) {
        if (dto.getCompatibles() != null && !dto.getCompatibles().isEmpty()) {
            productoDTODb.getCompatibles().addAll(dto.getCompatibles());
        }
    }

    public void actualizarHistorialStock(ProductoDTO productoDTODb, ProductoDTO dto) {
        if (dto.getHistorialStockDTOS() != null && !dto.getHistorialStockDTOS().isEmpty()) {
            productoDTODb.getHistorialStockDTOS().add(dto.getHistorialStockDTOS().get(0));
        }
    }

    public void actualizarPrecio(ProductoDTO productoDTODb, ProductoDTO dto) {
        float costo = precioFacade.obtenerCostoReferencia(dto, productoDTODb);
        if (dto.getCompraVentaDTOS() != null && !dto.getCompraVentaDTOS().isEmpty()) {
            precioFacade.configurarPreciosProducto(
                    productoDTODb,
                    costo,
                    dto.getCompraVentaDTOS().get(0)
            );
            productoDTODb.getCompraVentaDTOS().add(dto.getCompraVentaDTOS().get(0));
        }
    }

    //builder
    public ProductoDTO guardarSoloCompatibles(ProductoDTO dto, ProductoDTO productoDTODb) {
        actualizarCompatiblesProducto(productoDTODb, dto);
        return productoDTODb;
    }

    //builder
    public boolean esActualizacionCompatibles(ProductoDTO dto) {
        return dto.getHistorialStockDTOS() == null || dto.getHistorialStockDTOS().isEmpty();
    }



    public ProductoDTO inicializarProducto(ProductoDTO dto){
        stockFacade.inicializarStock(dto,null);
        dto.setCostoReferencia(precioFacade.obtenerUltimoCosto(dto));
        precioFacade.configurarPreciosProducto(dto,precioFacade.obtenerUltimoCosto(dto),dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size() - 1));

        return dto;
    }

    /*private void agregarCompatibles(ProductoDTO productoDTODb, ProductoDTO dto){
        if(dto.getCompatibles()!=null||!dto.getCompatibles().isEmpty()){
            productoDTODb.getCompatibles().addAll(dto.getCompatibles());
        }
    }*/

}
