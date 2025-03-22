package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.ProductoVentaEntity;
import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.facade.VentaFacade;
import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.request.VentaRequest;
import com.agmadera.mitienda.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class VentaFacadeImpl implements VentaFacade {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoFacade productoFacade;



    @Override
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        List<ProductoVentaDTO> productoVentaDTOS = ventaDTO.getProductoVentaDTOS();
        int i = 0;
        while (productoVentaDTOS.iterator().hasNext()){
            ProductoVentaDTO productoVentaDTO = productoVentaDTOS.get(i);
            if (productoVentaDTOS.size() == i){
                break;
            }
            ProductoDTO productoDTO = productoFacade.buscarId(productoVentaDTO.getId());
            productoVentaDTO.setNombre(productoDTO.getNombre() + productoDTO.getCalidad());// se coloca nombre de venta
            int unidadesExistencia = productoDTO.getStockDTO().getUnidadesExistencia();
            int unidadesVendidas = productoVentaDTO.getCantidad();

            //Se prepara productoDTO para actualizacion de stock (unidades en existencia)
            productoDTO.getStockDTO().setUnidadesExistencia(unidadesExistencia-unidadesVendidas);
            //TODO se manda llamar facade o service para actualizar stock

            //Colocar precio y descuentos
            float precio = productoDTO.getCompraVentaDTOS().get(productoDTO.getCompraVentaDTOS().size()-1).getVentaPG();
            if (ventaDTO.isTecnico()){
                precio = productoDTO.getCompraVentaDTOS().get(productoDTO.getCompraVentaDTOS().size()-1) .getVentaTecnico();
            }
            productoVentaDTO.setPrecio(precio);//Se agrega el precio por unidad
            productoVentaDTO.getDescuento();



        }
        return null;
    }

    @Override
    public VentaDTO orden(VentaRequest ventaRequest) {

        VentaDTO ventaDTO = new VentaDTO();
        List<ProductoVentaDTO> productoVentaDTOS = ventaRequest.getProductoVentaDTOS();

        int i = 0;
        while (productoVentaDTOS.iterator().hasNext()){
            if (productoVentaDTOS.size() == i){
                break;
            }

            ProductoVentaDTO productoVentaDTO = productoVentaDTOS.get(i);

            Long idProductoBuscar = productoVentaDTO.getIdProductoRef(); //Obtenemos el id del producto a buscar


            ProductoDTO productoDTO = productoFacade.buscarId(idProductoBuscar);//Se busca el producto

            //Se valida stock en existencia
            if (productoDTO.getStockDTO().getUnidadesExistencia() == 0 || productoDTO.getStockDTO().getUnidadesExistencia()<productoVentaDTO.getCantidad()){
                //TODO exception no hay suficientes unidades del producto
            }

            productoVentaDTO.setIdProductoRef(idProductoBuscar);// se agrega id
            productoVentaDTO.setNombre(productoDTO.getNombre() + productoDTO.getCalidad() + (productoDTO.isMarco()? "con marco": "")); //se crea nombre

            //Se asigna el precio si es tecnico o PG
            /*
            float precio = productoDTO.getCompraVentaDTOS().get(productoDTO.getCompraVentaDTOS().size() - 1).getVentaPG();

            if (ventaRequest.isTecnico()){
                precio = productoDTO.getCompraVentaDTOS().get(productoDTO.getCompraVentaDTOS().size() - 1).getVentaTecnico();
            }
            */
            CompraVentaDTO compraVentaDTO = productoDTO.getCompraVentaDTOS().get(productoDTO.getCompraVentaDTOS().size() - 1);

            productoVentaDTO.setPrecio(ventaRequest.isTecnico()?compraVentaDTO.getVentaTecnico() : compraVentaDTO.getVentaPG());
            //int cantidad = productoVentaDTO.getCantidad();
            //float descuento = productoVentaDTO.getDescuento();
            productoVentaDTO.setTotal((productoVentaDTO.getPrecio() - productoVentaDTO.getDescuento()) * productoVentaDTO.getCantidad());//Se calcula el total
            ventaDTO.setTotalGenrealAntesDesc(ventaDTO.getTotalGenrealAntesDesc()+ productoVentaDTO.getTotal());
            i++;
        }
        ventaDTO.setProductoVentaDTOS(productoVentaDTOS);
        return ventaDTO;
    }

}
