package com.agmadera.mitienda.controller;


import com.agmadera.mitienda.facade.AnalisisDatosFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analisis")
public class StockController {

    @Autowired
    private AnalisisDatosFacade analisisDatosFacade;

    @GetMapping("/pedir")
    ResponseEntity<?> pedir(){
        return ResponseEntity.ok(analisisDatosFacade.sinStockYDemanda());
        // return ResponseEntity.status(HttpStatus.CREATED).body( service.);


    }
    @GetMapping("/sinStock")
    ResponseEntity<?> sinStock(){
        return ResponseEntity.ok(analisisDatosFacade.sinStock());
        // return ResponseEntity.status(HttpStatus.CREATED).body( service.);


    }

    @GetMapping("/masVendidos")
    ResponseEntity<?> masVendidos(){
        return ResponseEntity.ok(analisisDatosFacade.masVendidos());
        // return ResponseEntity.status(HttpStatus.CREATED).body( service.);


    }

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
    /*@PutMapping("/stock/actualizar/{id}")
    ResponseEntity<?> actualizarProducto(@RequestBody ProductoDTO productoDTO, @PathVariable Long id ){
        ProductoDTO actualizarStockProducto = stockFacade.actualizarStockProducto(productoDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizarStockProducto);
    }*/
}
