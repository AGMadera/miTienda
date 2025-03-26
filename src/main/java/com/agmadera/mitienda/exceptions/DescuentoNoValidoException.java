package com.agmadera.mitienda.exceptions;

public class DescuentoNoValidoException extends RuntimeException{


    public DescuentoNoValidoException() {
        super("Decuento no valido");
    }
    /*public DescuentoNoValidoException(String msg) {
        super(msg);
        this.message= msg;
    }*/
}
