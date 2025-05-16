package com.agmadera.mitienda.facade.impl.helper;

import com.agmadera.mitienda.models.ProductoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoActualizacionBuilder {
    private final ProductoHelper productoHelper;
    private UpdateType updateType;


    public boolean isCompatiblesOnlyUpdate(ProductoDTO dto) {
        return productoHelper.esActualizacionCompatibles(dto);
    }

    public ProductoDTO buildCompatiblesUpdate(ProductoDTO dto, ProductoDTO dbDto) {
        return productoHelper.guardarSoloCompatibles(dto, dbDto);
    }

    public ProductoDTO buildFullUpdate(ProductoDTO dto, ProductoDTO dbDto) {
        return productoHelper.actualizarProductoCompleto(dto, dbDto);
    }

    public ProductoActualizacionBuilder determinarTipoActualizacion(ProductoDTO dto) {
        this.updateType = productoHelper.esActualizacionCompatibles(dto)
                ? UpdateType.COMPATIBLES_ONLY
                : UpdateType.FULL_UPDATE;
        return this;
    }

    public ProductoDTO buildUpdate(ProductoDTO dto, ProductoDTO dbDto) {
        return switch(updateType) {
            case COMPATIBLES_ONLY -> productoHelper.guardarSoloCompatibles(dto, dbDto);
            case FULL_UPDATE -> productoHelper.actualizarProductoCompleto(dto, dbDto);
        };
    }



    private enum UpdateType {
        COMPATIBLES_ONLY, FULL_UPDATE
    }

}
