package com.agmadera.mitienda.populator;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;

import java.util.List;

public interface ProductoPopulator {
    ProductoEntity dto2Entity(ProductoDTO dto);
    ProductoDTO entity2Dto(ProductoEntity entity);
    List<ProductoDTO> listEntity2dto(List<ProductoEntity> entities);
    List<ProductoEntity> listDto2entities(List<ProductoDTO> dtoList);
    List<ProductoTecResponse> listEntity2ProductoTecResponses(List<ProductoEntity> entities);
    List<ProductoPGResponse> listEntity2ProductoPGResponses(List<ProductoEntity> entities);
}
