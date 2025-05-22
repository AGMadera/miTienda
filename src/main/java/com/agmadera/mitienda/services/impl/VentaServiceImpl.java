package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.exceptions.VentaNoEncontradaException;
import com.agmadera.mitienda.exceptions.mensajes.VentaMensajesError;
import com.agmadera.mitienda.repository.VentaRepository;
import com.agmadera.mitienda.services.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final static String BUSCANDO_VENTA = "Buscando venta con id:  {}";
    private final static String  GUARDANDO_VENTA = "Guardando Venta";

    private final VentaRepository repository;

    @Override
    @Transactional
    public VentaEntity guardarVenta(@Valid VentaEntity productoVentaEntity) {
        validarVentaEntity(productoVentaEntity);
        try {
            log.info(GUARDANDO_VENTA);
            return repository.save(productoVentaEntity);
        }catch (Exception ex){
            log.error(VentaMensajesError.VENTA_NO_GUARDADA);
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VentaEntity buscarVenta(Long id) {
        log.info(BUSCANDO_VENTA,id);
        return repository.findById(id).orElseThrow(VentaNoEncontradaException::new);

    }

    private void validarVentaEntity(VentaEntity ventaEntity){
        Objects.requireNonNull(ventaEntity, VentaMensajesError.VENTA_NO_ENCONTRADA);
    }

}
