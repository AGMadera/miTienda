package com.agmadera.mitienda.models.response;

import com.agmadera.mitienda.models.CompatibleDTO;

import java.util.List;

public class ProductoPGResponse {
    private String nombre;
    private String calidad;
    private boolean marco;
    private String marca;
    private String color;
    private List<CompatibleDTO> compatibles;
    private int unidadesExistancia;
    private float precio;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCalidad() {
        return calidad;
    }

    public void setCalidad(String calidad) {
        this.calidad = calidad;
    }

    public boolean isMarco() {
        return marco;
    }

    public void setMarco(boolean marco) {
        this.marco = marco;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<CompatibleDTO> getCompatibles() {
        return compatibles;
    }

    public void setCompatibles(List<CompatibleDTO> compatibles) {
        this.compatibles = compatibles;
    }

    public int getUnidadesExistancia() {
        return unidadesExistancia;
    }

    public void setUnidadesExistancia(int unidadesExistancia) {
        this.unidadesExistancia = unidadesExistancia;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }
}
