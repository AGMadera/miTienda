package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Enum.TipoPago;
import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.exceptions.DescuentoNoValidoException;
import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.exceptions.StockInsuficienteException;
import com.agmadera.mitienda.facade.*;
import com.agmadera.mitienda.models.*;
import com.agmadera.mitienda.models.request.VentaRequest;
import com.agmadera.mitienda.models.response.ValeResponse;
import com.agmadera.mitienda.populator.VentaPopulator;
import com.agmadera.mitienda.services.VentaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
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

    @Autowired
    private ValeCobroFacade garantiaFacade;

    @Value("${mensaje.venta}")
    private String mensaje;

    Logger logger = LoggerFactory.getLogger(VentaFacadeImpl.class);

    private final String LOGGER_GUARDANDO_VENTA_PRODUCTOS = "Guardando venta de productos; #productos totales = ";
    private final String LOGGER_DESCUENTO_NO_VALIDO = "Decuento no valido, descuentos en total gen > al total gen";
    private final String LOGGER_GUARDANDO_VENTA_PRODUCTO = "Guardando venta de producto ";
    private final String LOGGER_NO_HAY_EXISTENCIA = "No hay unidades en existencia";
    private final String LOGGER_EXISTENCIA_NO_SUFICIENTE = "No hay suficientes unidades";
    private final String LOGGER_DESCUENTO_MAYOR = "Descuento mayor al precio";
    private final String ERROR_GARANTIA_CANTIDAD_MAYOR = "PAGO CON VALE SOLO ADMITE 1 PRODUCTO";
    private final String ERROR_GARANTIA_SIN_SALDO = "SALDO DE VALE ES 0";


    @Override
    @Transactional
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        List<ProductoVentaDTO> productoVentaDTOS = ventaDTO.getProductoVentaDTOS();

        int totalProductos = productoVentaDTOS.size();
        logger.info(LOGGER_GUARDANDO_VENTA_PRODUCTOS +totalProductos);

        float totalGenrealAntesDesc = ventaDTO.getTotalGenrealAntesDesc();
        float descuentosEnTotalGen = ventaDTO.getDescuentosEnTotalGen();
        float descuentoGarantia = 0;

        if(ventaDTO.getTipoPago().equals(TipoPago.VALE) && !ventaDTO.getFolioGarantia().isEmpty()){
            if(ventaDTO.getProductoVentaDTOS().size() > 1){
                throw new GarantiaNoValidaException(ERROR_GARANTIA_CANTIDAD_MAYOR);
            }
            //Se busca folio de la garantia
            ValeResponse valeResponse = garantiaFacade.buscarVale(ventaDTO.getFolioGarantia());
            //Se obtiene el saldo actual
            float saldoActual = valeResponse.getSaldoActual();
            //Se valida que tenga saldo
            if (saldoActual <= 0 ){
                throw new GarantiaNoValidaException(ERROR_GARANTIA_SIN_SALDO);
            }
            //Se recupera el historial de los vales
            HistorialGarantiaValeDto historialGarantiaValeDto = new HistorialGarantiaValeDto();
            //se utiliza descuentosEnTotalGen para cargar el saldo
            descuentosEnTotalGen = saldoActual;

            historialGarantiaValeDto.setSaldo(saldoActual-totalGenrealAntesDesc);
            valeResponse.setSaldoActual(saldoActual-totalGenrealAntesDesc);
            historialGarantiaValeDto.setIdProductoVenta(productoVentaDTOS.get(0).getIdProductoRef());
            if (totalGenrealAntesDesc > saldoActual || saldoActual == totalGenrealAntesDesc){
                historialGarantiaValeDto.setSaldo(0);
                valeResponse.setSaldoActual(0);
            }

            if(descuentosEnTotalGen > totalGenrealAntesDesc){
                descuentosEnTotalGen = totalGenrealAntesDesc;

            }
            ventaDTO.setDescuentosEnTotalGen(descuentosEnTotalGen);
            historialGarantiaValeDto.setFecha(new Date());

            valeResponse.getHistorialGarantiaValesDtos().add(historialGarantiaValeDto);
            descuentoGarantia = descuentosEnTotalGen;
            garantiaFacade.cobroVale(valeResponse);

        }

        if(descuentosEnTotalGen > totalGenrealAntesDesc){
            logger.error(LOGGER_DESCUENTO_NO_VALIDO );
            throw new DescuentoNoValidoException();
        }

        int i = 0;
        while (productoVentaDTOS.iterator().hasNext()){
            if (totalProductos == i){
                break;
            }
            int tmp = i+1;
            logger.info(LOGGER_GUARDANDO_VENTA_PRODUCTO +tmp+"/"+totalProductos);
            ProductoVentaDTO productoVentaDTO = productoVentaDTOS.get(i);
            ProductoDTO productoDTO = productoFacade.buscarId(productoVentaDTO.getIdProductoRef());

            int unidadesExistencia = productoDTO.getStockDTO().getUnidadesExistencia();
            int unidadesAVender = productoVentaDTO.getCantidad();

            //Se prepara productoDTO para actualizacion de stock (unidades en existencia y unidades vendidas)
            productoDTO.getStockDTO().setUnidadesExistencia(unidadesExistencia-unidadesAVender);
            productoDTO.getStockDTO().setUnidadesVendidas(productoDTO.getStockDTO().getUnidadesVendidas() + unidadesAVender);

            //Se manda llamar facade para guardar stock actualizado
            productoFacade.actualizarStockVenta(productoDTO);
            i++;
        }

        ventaDTO.setTotalGenreal(totalGenrealAntesDesc - descuentosEnTotalGen);

        //Se guarda la venta
        VentaEntity ventaGuardada = ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO));

        VentaDTO ventaDTOResponse = ventaPopulator.entity2Dto(ventaGuardada);
        ventaDTOResponse.setMensaje(mensaje);
        if (ventaDTO.getTipoPago().equals(TipoPago.VALE)){
            ventaDTOResponse.getProductoVentaDTOS().get(0).setDescuento(descuentoGarantia);
            ventaDTOResponse.setFolioGarantia(ventaDTO.getFolioGarantia());
        }
        gananciaFacade.guardarGanacia(ventaDTOResponse);
        return ventaDTOResponse;

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
            if(productoVentaDTO.getDescuento()>productoDTO.getCostoReferencia()){
                //lanzar excepcion
                logger.error(LOGGER_DESCUENTO_MAYOR );
                throw new DescuentoNoValidoException();
            }


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

    @Override
    public VentaDTO actualizarVentaGarantia(VentaDTO ventaDTO) {

        //Metodo solo utilizado para las garantias
        VentaEntity ventaEntityDb = ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO));

        return ventaPopulator.entity2Dto(ventaEntityDb);
    }

}
