package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Enum.RazonGarantia;
import com.agmadera.mitienda.Enum.TipoGarantia;
import com.agmadera.mitienda.entities.GarantiaReembolso;
import com.agmadera.mitienda.entities.GarantiaVale;
import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.exceptions.VentaNoEncontradaException;
import com.agmadera.mitienda.facade.GananciaFacade;
import com.agmadera.mitienda.facade.GarantiaFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.facade.VentaFacade;
import com.agmadera.mitienda.models.*;
import com.agmadera.mitienda.models.response.ReembolsoResponse;
import com.agmadera.mitienda.models.response.ValeResponse;
import com.agmadera.mitienda.populator.GarantiaPopulator;
import com.agmadera.mitienda.services.GarantiaReembolsoService;
import com.agmadera.mitienda.services.GarantiaValeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class GarantiaFacadeImpl implements GarantiaFacade {

    @Autowired
    private GarantiaReembolsoService reembolsoService;

    @Autowired
    private GarantiaValeService valeService;

    @Autowired
    private VentaFacade ventaFacade;

    @Autowired
    private ProductoFacade productoFacade;

    @Autowired
    private GarantiaPopulator garantiaPopulator;

    @Autowired
    private GananciaFacade gananciaFacade;

    @Value("${garantia.dias}")
    private float GARANTIA_DIAS_VALIDOS;

    @Override
    @Transactional
    public ReembolsoResponse guardarReembolso(GarantiaDTO reembolso) {
        validarGarantia(reembolso);
        VentaDTO ventaDTO = ventaFacade.buscarVenta(reembolso.getIdVenta());
        float cantidadReembolso = modificarCompra(ventaDTO, reembolso.getIdProductoVenta(), reembolso.getRazonGarantia());
        reembolso.setCantidadReembolso(cantidadReembolso);
        GarantiaReembolso garantiaReembolso = garantiaPopulator.dto2Entity(reembolso);
        ReembolsoResponse reembolsoResponse = garantiaPopulator.entity2Response(reembolsoService.guardar(garantiaReembolso));
        reembolsoResponse.setProducto(nombreProducto(reembolso.getIdProductoVenta()));
        gananciaFacade.actualizarGanacia(ventaDTO, reembolso.getIdProductoVenta(), TipoGarantia.REEMBOLSO);
        return reembolsoResponse;
    }

    @Override
    @Transactional
    public ValeResponse crearVale(GarantiaDTO garantiaDTO) {
        VentaDTO ventaDTO = validarGarantia(garantiaDTO);

        float cantidadReembolso = modificarCompra(ventaDTO, garantiaDTO.getIdProductoVenta(), garantiaDTO.getRazonGarantia());

        GarantiaVale garantiaVale = garantiaPopulator.dtoVale2Entity(garantiaDTO);
        garantiaVale.setSaldoInicial(cantidadReembolso);
        garantiaVale.setSaldoActual(cantidadReembolso);
        GarantiaVale garantiaValeDb = valeService.guardar(garantiaVale);

        garantiaVale.setFolio(garantiaVale.getIdVenta()+"-"+garantiaVale.getIdProductoVenta()+"-"+ garantiaValeDb.getId());
        gananciaFacade.actualizarGanacia(ventaDTO, garantiaDTO.getIdProductoVenta(), garantiaDTO.getTipoGarantia());
        return garantiaPopulator.valeEntityToResponse(valeService.guardar(garantiaValeDb));

    }


    private VentaDTO validarGarantia(GarantiaDTO garantiaReembolso){

        try{
            VentaDTO ventaDTO = ventaFacade.buscarVenta(garantiaReembolso.getIdVenta());
            Date fechaVenta = ventaDTO.getFechaVenta();

            long diasTranscurridos = ChronoUnit.DAYS.between(fechaVenta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    garantiaReembolso.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            //System.out.println(diasTranscurridos);

            if (diasTranscurridos <= GARANTIA_DIAS_VALIDOS){
                //TODO LOG
                System.out.println("DIAS transcurridos: "+diasTranscurridos);
                return ventaDTO;
            }
        }catch (VentaNoEncontradaException exception){
            throw new VentaNoEncontradaException();
        }
        System.out.println("Llego aqui");
        //TODO EXCEPTION
        throw new GarantiaNoValidaException();
        //return null;
    }

    private float modificarCompra(VentaDTO ventaDTO, Long idProductoReembolso, RazonGarantia razonGarantia){

        List<ProductoVentaDTO> productoVentaDTOSList = ventaDTO.getProductoVentaDTOS();

        int i = 0;
        float precio = 0;
        while (productoVentaDTOSList.iterator().hasNext()) {
            if (productoVentaDTOSList.size() == i) {
                //TODO EXCEPTION
                throw new VentaNoEncontradaException("Producto no encontrado en venta");
            }
            ProductoVentaDTO productoVentaDTO = productoVentaDTOSList.get(i);
            if (productoVentaDTO.getIdProductoRef().equals( idProductoReembolso)){
                int cantidad = productoVentaDTO.getCantidad();
                float total = productoVentaDTO.getTotal();
                if(cantidad <= 0){
                    //TODO EXCEPTION
                    System.out.println("Cantidad menor a cero");
                    throw new GarantiaNoValidaException("Garantia ya usada");
                }
                productoVentaDTO.setCantidad(cantidad - 1);
                productoVentaDTO.setTotal(total - productoVentaDTO.getPrecio());
                productoVentaDTO.setDescuento(productoVentaDTO.getPrecio());
                precio = productoVentaDTO.getPrecio();

                break;
            }

            i++;
        }
        ventaDTO.setDescuentosEnTotalGen(precio);
        ventaDTO.setTotalGenreal(ventaDTO.getTotalGenreal() - precio);
        ventaFacade.actualizarVentaGarantia(ventaDTO);



        if (razonGarantia.ordinal() != RazonGarantia.displayMalEstado.ordinal()){
            //TODO LOG
            System.out.println("display sirve");
            ProductoDTO productoDTO = productoFacade.buscarId(idProductoReembolso);
            int unidadesVendidas = productoDTO.getStockDTO().getUnidadesVendidas();
            productoDTO.getStockDTO().setUnidadesVendidas(unidadesVendidas - 1);
            productoDTO.getStockDTO().setUnidadesExistencia(productoDTO.getStockDTO().getUnidadesExistencia() + 1);
            HistorialStockDTO historialStockDTO = new HistorialStockDTO();
            historialStockDTO.setUnidadesIngresadas(1);
            historialStockDTO.setFecha( new Date());
            productoDTO.getHistorialStockDTOS().add(historialStockDTO);
            productoFacade.actualizarStockVenta(productoDTO);
        }


        return precio;

    }

    private String nombreProducto(Long id){
        ProductoDTO productoDTO = productoFacade.buscarId(id);
        return productoDTO.getNombre() +" "+ productoDTO.getCalidad() + (productoDTO.isMarco()? " con marco": "");
    }
}
