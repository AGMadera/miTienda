package com.agmadera.mitienda.config;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapperBean(){

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                        .setSkipNullEnabled(true);

        modelMapper.typeMap(ProductoEntity.class, ProductoDTO.class)
                .addMapping(src -> src.getStockEntity(), ProductoDTO::setStockDTO)
                .addMapping(src-> src.getHistorialStockEntity(), ProductoDTO::setHistorialStockDTOS)
                .addMapping(src -> src.getCompraVentaEntity(), ProductoDTO::setCompraVentaDTOS)
                .addMapping(src->src.getCompatibles(),ProductoDTO::setCompatibles);
        modelMapper.typeMap(ProductoDTO.class, ProductoEntity.class)
                .addMapping(src -> src.getStockDTO(), ProductoEntity::setStockEntity)
                .addMapping(src-> src.getHistorialStockDTOS(),ProductoEntity::setHistorialStockEntity)
                .addMapping(src-> src.getCompraVentaDTOS(), ProductoEntity::setCompraVentaEntity)
                .addMapping(src -> src.getCompatibles(), ProductoEntity::setCompatibles);
        modelMapper.typeMap(ProductoEntity.class, ProductoTecResponse.class)
                .addMapping(src -> src.getStockEntity().getUnidadesExistencia(), ProductoTecResponse::setUnidadesExistancia);
                //.addMapping(src -> src.getCompraVentaEntity().get(0).getVentaTecnico(), ProductoTecResponse::setPrecio);
        modelMapper.typeMap(ProductoEntity.class, ProductoPGResponse.class)
                .addMapping(src -> src.getStockEntity().getUnidadesExistencia(), ProductoPGResponse::setUnidadesExistancia);



        return modelMapper;

    }
}
