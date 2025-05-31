package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.facade.AnalisisDatosFacade;
import com.agmadera.mitienda.models.response.ProductoStockResponse;
import com.agmadera.mitienda.populator.ProductoPopulator;
import com.agmadera.mitienda.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnalisisDatosFacadeImpl implements AnalisisDatosFacade {
    private final ProductoPopulator populator;
    private final ProductoService productoService;

    @Value("${app.unidades.vendidas.analisis}")
    private int unidadesVendidasAnalisis;


    @Override
    public List<ProductoStockResponse> sinStockYDemanda() {
        List<ProductoEntity> productoEntityList = obtenerTodosProductos();
        return populator.listEntity2PSResponses(productoEntityList.stream()
                .filter(producto -> producto.getStockEntity().getUnidadesVendidas() > unidadesVendidasAnalisis
                        && producto.getStockEntity().getUnidadesExistencia() == 0).collect(Collectors.toList()));

    }

    @Override
    public List<ProductoStockResponse> sinStock() {
        return populator.listEntity2PSResponses(obtenerTodosProductos().stream()
                .filter(producto ->
                        producto.getStockEntity().getUnidadesExistencia()==0).collect(Collectors.toList()));

    }

    @Override
    public List<ProductoStockResponse> masVendidos() {

        return populator.listEntity2PSResponses(obtenerTodosProductos().stream().filter(productoEntity ->
                productoEntity.getStockEntity().getUnidadesVendidas()>unidadesVendidasAnalisis).collect(Collectors.toList()));
    }

    private List<ProductoEntity> obtenerTodosProductos(){
        return productoService.mostrarTodos();
    }
}
