package com.agmadera.mitienda.controller;

import com.agmadera.mitienda.facade.VentaFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.request.VentaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carrito")
public class VentaController {
    @Autowired
    private VentaFacade ventaFacade;

    @PostMapping("/orden")
    ResponseEntity<?> crearOrden(@RequestBody VentaRequest venta){

        return ResponseEntity.status(HttpStatus.CREATED).body( ventaFacade.orden(venta));
        //return null;
    }

    @PostMapping("/venta")
    ResponseEntity<?> venta(@RequestBody VentaDTO venta){

        return ResponseEntity.status(HttpStatus.CREATED).body( ventaFacade.guardarVenta(venta));
        //return null;
    }
}
