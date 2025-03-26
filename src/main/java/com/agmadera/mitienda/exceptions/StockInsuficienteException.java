package com.agmadera.mitienda.exceptions;

import org.springframework.beans.factory.annotation.Value;

public class StockInsuficienteException extends RuntimeException{

    private String actual2;
    public StockInsuficienteException() {
        super("Stock insuficiente Producto sin Stock!!");
    }
    public StockInsuficienteException(Long id, int actual) {
        super("Stock del producto id: "+id+" no suficiente para cumplir la orden, unidades actuales: "+actual);
    }
}
