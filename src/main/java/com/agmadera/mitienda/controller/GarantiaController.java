package com.agmadera.mitienda.controller;

import com.agmadera.mitienda.facade.GarantiaFacade;
import com.agmadera.mitienda.models.GarantiaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garantia")
public class GarantiaController {
    @Autowired
    private GarantiaFacade reembolsoFacade;

    @PostMapping("/reembolso/guardar")
    ResponseEntity<?> guardarReembolso(@RequestBody GarantiaDTO garantiaReembolso){
        /*GarantiaDTO garantiaReembolso = new GarantiaDTO();
        //garantiaReembolso.setId(01L);
        garantiaReembolso.setIdVenta(1352L);
        garantiaReembolso.setIdProductoVenta(69L);
        garantiaReembolso.setFecha(new Date());
        garantiaReembolso.setRazonGarantia(RazonGarantia.displayMalEstado);*/
        return ResponseEntity.status(HttpStatus.CREATED).body( reembolsoFacade.guardarReembolso(garantiaReembolso));
        //return null;
    }
    @PostMapping("/vale/guardar")
    ResponseEntity<?> crearVale(@RequestBody GarantiaDTO garantiaReembolso){
        /*GarantiaDTO garantiaReembolso = new GarantiaDTO();
        //garantiaReembolso.setId(01L);
        garantiaReembolso.setIdVenta(1352L);
        garantiaReembolso.setIdProductoVenta(69L);
        garantiaReembolso.setFecha(new Date());
        garantiaReembolso.setRazonGarantia(RazonGarantia.displayMalEstado);*/
        return ResponseEntity.status(HttpStatus.CREATED).body( reembolsoFacade.crearVale(garantiaReembolso));
        //return null;
    }
}
