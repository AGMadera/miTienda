package com.agmadera.mitienda.exceptions;

public class DescuentoNoValidoException extends RuntimeException{

    private String message;
    public DescuentoNoValidoException() {
        super("Decuento no valido");
    }
    public DescuentoNoValidoException(String msg) {
        super(msg);
        this.message= msg;
    }
}
