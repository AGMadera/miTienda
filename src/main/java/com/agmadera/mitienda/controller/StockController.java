package com.agmadera.mitienda.controller;


import com.agmadera.mitienda.facade.StockFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StockController {

    @Autowired
    private StockFacade stockFacade;

    /*@PostMapping("/stock")
    ResponseEntity<?> guardarStock(@RequestBody StockEntity stock){
        return ResponseEntity.status(HttpStatus.CREATED).body( service.guardarStock(stock));
    }
    @GetMapping("/buscar_existencias/{id}")
    ResponseEntity<?> buscarStock(@PathVariable Long idProducto){
       // return ResponseEntity.status(HttpStatus.CREATED).body( service.);

        return null;
    }
     */
    @PutMapping("/stock/actualizar/{id}")
    ResponseEntity<?> actualizarProducto(@RequestBody ProductoDTO productoDTO, @PathVariable Long id ){
        ProductoDTO actualizarStockProducto = stockFacade.actualizarStockProducto(productoDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizarStockProducto);
    }
}
