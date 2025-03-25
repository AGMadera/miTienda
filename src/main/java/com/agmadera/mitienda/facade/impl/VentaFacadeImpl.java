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
import com.agmadera.mitienda.populator.VentaPopulator;
import com.agmadera.mitienda.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VentaFacadeImpl implements VentaFacade {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoFacade productoFacade;

    @Autowired
    private VentaPopulator ventaPopulator;

    @Value("${mensaje.venta}")
    private String mensaje;


    @Override
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        List<ProductoVentaDTO> productoVentaDTOS = ventaDTO.getProductoVentaDTOS();
        int i = 0;
        while (productoVentaDTOS.iterator().hasNext()){
            if (productoVentaDTOS.size() == i){
                break;
            }
            ProductoVentaDTO productoVentaDTO = productoVentaDTOS.get(i);
            ProductoDTO productoDTO = productoFacade.buscarId(productoVentaDTO.getIdProductoRef());

            int unidadesExistencia = productoDTO.getStockDTO().getUnidadesExistencia();
            int unidadesAVender = productoVentaDTO.getCantidad();

            //Se prepara productoDTO para actualizacion de stock (unidades en existencia y unidades vendidas)
            productoDTO.getStockDTO().setUnidadesExistencia(unidadesExistencia-unidadesAVender);
            productoDTO.getStockDTO().setUnidadesVendidas(productoDTO.getStockDTO().getUnidadesVendidas() + unidadesAVender);

            //Se manda llamar facade para guardar stock actualizado
            productoFacade.actualizarStock(productoDTO);
            i++;
        }

        float totalGenrealAntesDesc = ventaDTO.getTotalGenrealAntesDesc();
        float descuentosEnTotalGen = ventaDTO.getDescuentosEnTotalGen();
        if(descuentosEnTotalGen>totalGenrealAntesDesc){
            //TODO lanzar exception

        }
        ventaDTO.setTotalGenreal(totalGenrealAntesDesc - descuentosEnTotalGen);

        VentaEntity ventaGuardada = ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO));
        VentaDTO ventaDTOResponse = ventaPopulator.entity2Dto(ventaGuardada);
        ventaDTOResponse.setMensaje(mensaje);
        return ventaDTOResponse;

        //return ventaDTO;
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
                //TODO exception no hay suficientes unidades del producto para cumplir la orden
            }

            productoVentaDTO.setIdProductoRef(idProductoBuscar);// se agrega id
            productoVentaDTO.setNombre(productoDTO.getNombre() +" "+ productoDTO.getCalidad() + (productoDTO.isMarco()? " con marco": "")); //se crea nombre

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
            if(productoVentaDTO.getDescuento()>productoVentaDTO.getPrecio()){
                //TODO lanzar excepcion
            }

            //Se calcula el total
            productoVentaDTO.setTotal((productoVentaDTO.getPrecio() - productoVentaDTO.getDescuento()) * productoVentaDTO.getCantidad());
            ventaDTO.setTotalGenrealAntesDesc(productoVentaDTO.getTotal()+ventaDTO.getTotalGenrealAntesDesc());
            i++;
        }
        ventaDTO.setProductoVentaDTOS(productoVentaDTOS);
        return ventaDTO;
    }

}
