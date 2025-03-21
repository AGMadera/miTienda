package com.agmadera.mitienda.controller;

import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProductoController {
    @Autowired
    ProductoFacade productoFacade;

    @PostMapping("/guardar")
    ResponseEntity<?> guardarProducto(@RequestBody ProductoDTO producto){

        return ResponseEntity.status(HttpStatus.CREATED).body( productoFacade.guardarProducto(producto));
        //return null;
    }

    /*
    @PostMapping("/carga_masiva")
    ResponseEntity<?> cargaMasiva(@RequestParam(value = "file") MultipartFile file){
        String message;
        message = "archivo cargado con exito: " + file.getOriginalFilename();
        productoFacade.cargaMasivaProducto(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    */
    @PostMapping("/carga_masiva")
    ResponseEntity<?> cargaMasiva(@RequestBody List<ProductoDTO> dtoList){
        String message;
        message = "archivo cargado con exito: ";
        productoFacade.cargaMasivaProducto(dtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/buscar/{nombre}")
    ResponseEntity<?> buscarNombre(@PathVariable("nombre") String nombre){
        System.out.println("entro"+nombre);
        List<ProductoDTO> productoDTOS = productoFacade.buscarNombre(nombre);

        return ResponseEntity.ok(productoDTOS);
    }

    @GetMapping("/mostrar/todo")
    ResponseEntity<?> mostrarTodos(){
        System.out.println("entro");
        List<ProductoDTO> productosDTOS = productoFacade.mostrarTodos();
        return ResponseEntity.ok(productosDTOS);

    }


}
