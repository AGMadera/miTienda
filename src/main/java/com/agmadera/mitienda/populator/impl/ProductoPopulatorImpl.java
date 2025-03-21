package com.agmadera.mitienda.populator.impl;

import com.agmadera.mitienda.entities.CompraVentaEntity;
import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;
import com.agmadera.mitienda.populator.ProductoPopulator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoPopulatorImpl implements ProductoPopulator {

    @Autowired
    private ModelMapper mapper;

    @Override
    public ProductoEntity dto2Entity(ProductoDTO dto) {
        ProductoEntity productoEntity = mapper.map(dto, ProductoEntity.class);
        return productoEntity;
    }

    @Override
    public ProductoDTO entity2Dto(ProductoEntity entity) {
        ProductoDTO productoDTO = mapper.map(entity, ProductoDTO.class);
        return productoDTO;
    }

    @Override
    public List<ProductoDTO> listEntity2dto(List<ProductoEntity> entities) {
        List<ProductoDTO> productoDTOS = entities.stream().map(entity ->
                mapper.map(entity, ProductoDTO.class)).collect(Collectors.toList());
        return productoDTOS;

    }

    @Override
    public List<ProductoEntity> listDto2entities(List<ProductoDTO> dtoList) {
        return dtoList.stream().map(dto -> mapper.map(dto, ProductoEntity.class)).collect(Collectors.toList());
    }

    @Override
    public List<ProductoTecResponse> listEntity2ProductoTecResponses(List<ProductoEntity> entities) {
        List<ProductoTecResponse> productoTecRespons = entities.stream().map(entity ->
                mapper.map(entity, ProductoTecResponse.class)).collect(Collectors.toList());

        int i = 0;

        ArrayList<Float> precioTemp = new ArrayList<>();

        while (entities.iterator().hasNext()){
            if(i == entities.size()){
                break;
            }
            int sizeCV = entities.get(i).getCompraVentaEntity().size();
            List<CompraVentaEntity> compraVentaEntities = entities.get(i).getCompraVentaEntity();
            float ventaTecnico = compraVentaEntities.get(sizeCV - 1).getVentaTecnico();

            precioTemp.add(ventaTecnico);
            i++;
        }

        int cont=0;
        while (productoTecRespons.iterator().hasNext()){
            if(cont == productoTecRespons.size()){
                break;
            }
            productoTecRespons.get(cont).setPrecio(precioTemp.get(cont));
                cont++;
       }

        return productoTecRespons;
    }

    @Override
    public List<ProductoPGResponse> listEntity2ProductoPGResponses(List<ProductoEntity> entities) {
        List<ProductoPGResponse> productoPGRespons = entities.stream().map(entity ->
                mapper.map(entity, ProductoPGResponse.class)).collect(Collectors.toList());

        int i = 0;

        ArrayList<Float> precioTemp = new ArrayList<>();

        while (entities.iterator().hasNext()){
            if(i == entities.size()){
                break;
            }
            int sizeCV = entities.get(i).getCompraVentaEntity().size();
            List<CompraVentaEntity> compraVentaEntities = entities.get(i).getCompraVentaEntity();
            float ventaPG = compraVentaEntities.get(sizeCV - 1).getVentaPG();

            precioTemp.add(ventaPG);
            i++;
        }

        int cont=0;
        while (productoPGRespons.iterator().hasNext()){
            if(cont == productoPGRespons.size()){
                break;
            }
            productoPGRespons.get(cont).setPrecio(precioTemp.get(cont));
            cont++;
        }

        return productoPGRespons;
    }


}
