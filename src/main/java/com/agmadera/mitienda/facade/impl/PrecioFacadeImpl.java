package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.facade.PrecioFacade;
import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.strategy.PrecioStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PrecioFacadeImpl implements PrecioFacade {
    /*
    private final PrecioStrategy precioStrategy;
    @Autowired
    public PrecioFacadeImpl(@Qualifier("margenFijoStrategy") PrecioStrategy precioStrategy) {
        this.precioStrategy = precioStrategy;
    }
    */
    private final PrecioStrategy precioStrategy;

    @Autowired
    public PrecioFacadeImpl(
            @Value("${app.pricing.strategy.bean}") String strategyBeanName,
            @Qualifier("margenFijoStrategy") PrecioStrategy defaultStrategy,
            ApplicationContext context) {

        // Intenta obtener la estrategia configurada, usa default si no existe
        this.precioStrategy = context.containsBean(strategyBeanName)
                ? (PrecioStrategy) context.getBean(strategyBeanName)
                : defaultStrategy;
    }

    @Override
    public void configurarPreciosProducto(ProductoDTO producto, float costo, CompraVentaDTO compraVentaDTO) {
        float precioPG = precioStrategy.calcularPrecioPG(costo);
        float precioTecnico = precioStrategy.calcularPrecioTecnico(costo);
        compraVentaDTO.setVentaTecnico(precioTecnico);
        compraVentaDTO.setVentaPG(precioPG);
        producto.setCostoReferencia(producto.getCostoReferencia());

    }

    @Override
    public float obtenerUltimoCosto(ProductoDTO producto) {
        return producto.getCompraVentaDTOS().get(producto.getCompraVentaDTOS().size() - 1).getCosto();
    }

    public float obtenerCostoReferencia(ProductoDTO dto, ProductoDTO productoDTODb){
        float costo =productoDTODb.getCostoReferencia();
        if(!dto.isUsarCostoReferencia()){
            costo = dto.getCompraVentaDTOS().get(0).getCosto();
            productoDTODb.setCostoReferencia(costo);
            return costo;
        }
        return costo;
    }
}
