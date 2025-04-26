package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.exceptions.DescuentoNoValidoException;
import com.agmadera.mitienda.exceptions.StockInsuficienteException;
import com.agmadera.mitienda.facade.GananciaFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.facade.VentaFacade;
import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.request.VentaRequest;
import com.agmadera.mitienda.populator.VentaPopulator;
import com.agmadera.mitienda.services.VentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private GananciaFacade gananciaFacade;

    @Value("${mensaje.venta}")
    private String mensaje;

    Logger logger = LoggerFactory.getLogger(VentaFacadeImpl.class);

    private final String LOGGER_GUARDANDO_VENTA_PRODUCTOS = "Guardando venta de productos; #productos totales = ";
    private final String LOGGER_DESCUENTO_NO_VALIDO = "Decuento no valido, descuentos en total gen > al total gen";
    private final String LOGGER_GUARDANDO_VENTA_PRODUCTO = "Guardando venta de producto ";
    private final String LOGGER_NO_HAY_EXISTENCIA = "No hay unidades en existencia";
    private final String LOGGER_EXISTENCIA_NO_SUFICIENTE = "No hay suficientes unidades";
    private final String LOGGER_DESCUENTO_MAYOR = "Descuento mayor al precio";


    @Override
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        List<ProductoVentaDTO> productoVentaDTOS = ventaDTO.getProductoVentaDTOS();

        int totalProductos = productoVentaDTOS.size()-1;
        logger.info(LOGGER_GUARDANDO_VENTA_PRODUCTOS +totalProductos);

        float totalGenrealAntesDesc = ventaDTO.getTotalGenrealAntesDesc();
        float descuentosEnTotalGen = ventaDTO.getDescuentosEnTotalGen();
        if(descuentosEnTotalGen > totalGenrealAntesDesc){
            logger.error(LOGGER_DESCUENTO_NO_VALIDO );
            throw new DescuentoNoValidoException();
        }

        int i = 0;
        while (productoVentaDTOS.iterator().hasNext()){
            if (productoVentaDTOS.size() == i){
                break;
            }
            logger.info(LOGGER_GUARDANDO_VENTA_PRODUCTO +i+"/"+totalProductos);
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

        ventaDTO.setTotalGenreal(totalGenrealAntesDesc - descuentosEnTotalGen);

        VentaEntity ventaGuardada = ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO));
        VentaDTO ventaDTOResponse = ventaPopulator.entity2Dto(ventaGuardada);
        ventaDTOResponse.setMensaje(mensaje);
        gananciaFacade.guardarGanacia(ventaDTOResponse);
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
                //exception no hay suficientes unidades del producto para cumplir la orden
                if (productoDTO.getStockDTO().getUnidadesExistencia() == 0){
                    logger.error(LOGGER_NO_HAY_EXISTENCIA);
                    throw new StockInsuficienteException();
                }
                logger.error(LOGGER_EXISTENCIA_NO_SUFICIENTE);
                throw new StockInsuficienteException(idProductoBuscar, productoDTO.getStockDTO().getUnidadesExistencia());
            }
            if(productoVentaDTO.getDescuento()>productoVentaDTO.getPrecio()){
                //lanzar excepcion
                logger.error(LOGGER_DESCUENTO_MAYOR );
                throw new DescuentoNoValidoException();
            }

            /*
            if (ventaRequest.getGarantia().equals(null)||ventaRequest.getGarantia().equals(0)){

            }
            */

            productoVentaDTO.setIdProductoRef(idProductoBuscar);// se agrega id
            productoVentaDTO.setNombre(productoDTO.getNombre() +" "+ productoDTO.getCalidad() + (productoDTO.isMarco()? " con marco": "")); //se crea nombre

            //Se asigna el precio si es tecnico o PG
            CompraVentaDTO compraVentaDTO = productoDTO.getCompraVentaDTOS().get(productoDTO.getCompraVentaDTOS().size() - 1);

            productoVentaDTO.setPrecio(ventaRequest.isTecnico()?compraVentaDTO.getVentaTecnico() : compraVentaDTO.getVentaPG());

            //Se calcula el total
            productoVentaDTO.setTotal((productoVentaDTO.getPrecio() - productoVentaDTO.getDescuento()) * productoVentaDTO.getCantidad());
            ventaDTO.setTotalGenrealAntesDesc(productoVentaDTO.getTotal()+ventaDTO.getTotalGenrealAntesDesc());

            ventaDTO.setTecnico(ventaRequest.isTecnico());

            i++;
        }
        ventaDTO.setProductoVentaDTOS(productoVentaDTOS);
        return ventaDTO;
    }

    @Override
    public VentaDTO buscarVenta(Long id) {
        VentaEntity ventaEntity = ventaService.buscarVenta(id);
        return ventaPopulator.entity2Dto(ventaEntity);
    }

}
