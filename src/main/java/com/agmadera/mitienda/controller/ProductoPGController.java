package com.agmadera.mitienda.controller;

import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pg")
public class ProductoPGController {
    @Autowired
    ProductoFacade productoFacade;

    @GetMapping("/buscar/{nombre}")
    ResponseEntity<?> buscarNombre(@PathVariable("nombre") String nombre){
        System.out.println("entro"+nombre);
        List<ProductoPGResponse> productoPGRespons = productoFacade.buscarNombrePG(nombre);

        return ResponseEntity.ok(productoPGRespons);
    }
}
