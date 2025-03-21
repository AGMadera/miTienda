package com.agmadera.mitienda.models;



import java.util.List;

public class ProductoDTO {

    private Long id;
    private String nombre;
    private String calidad;
    private boolean marco;
    private String marca;
    private String color;
    private float costoReferencia;

    private boolean usarCostoReferencia;
    private List<CompatibleDTO> compatibles;
    private StockDTO stockDTO;
    private List<HistorialStockDTO> historialStockDTOS;
    private List<CompraVentaDTO> compraVentaDTOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public float getCostoReferencia() {
        return costoReferencia;
    }

    public void setCostoReferencia(float costoReferencia) {
        this.costoReferencia = costoReferencia;
    }

    public boolean isUsarCostoReferencia() {
        return usarCostoReferencia;
    }

    public void setUsarCostoReferencia(boolean usarCostoReferencia) {
        this.usarCostoReferencia = usarCostoReferencia;
    }

    public List<CompatibleDTO> getCompatibles() {
        return compatibles;
    }

    public void setCompatibles(List<CompatibleDTO> compatibles) {
        this.compatibles = compatibles;
    }

    public StockDTO getStockDTO() {
        return stockDTO;
    }

    public void setStockDTO(StockDTO stockDTO) {
        this.stockDTO = stockDTO;
    }

    public List<HistorialStockDTO> getHistorialStockDTOS() {
        return historialStockDTOS;
    }

    public void setHistorialStockDTOS(List<HistorialStockDTO> historialStockDTOS) {
        this.historialStockDTOS = historialStockDTOS;
    }

    public List<CompraVentaDTO> getCompraVentaDTOS() {
        return compraVentaDTOS;
    }

    public void setCompraVentaDTOS(List<CompraVentaDTO> compraVentaDTOS) {
        this.compraVentaDTOS = compraVentaDTOS;
    }
}
