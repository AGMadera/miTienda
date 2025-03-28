package com.agmadera.mitienda.models;


import com.agmadera.mitienda.Enum.TipoPago;

import java.util.Date;
import java.util.List;

public class VentaDTO {

    private Long id;
    private List<ProductoVentaDTO> productoVentaDTOS;
    private float totalGenrealAntesDesc;
    private float descuentosEnTotalGen;
    private float totalGenreal;
    private Date fechaVenta;
    private TipoPago tipoPago;
    private String mensaje;
    private boolean tecnico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProductoVentaDTO> getProductoVentaDTOS() {
        return productoVentaDTOS;
    }

    public void setProductoVentaDTOS(List<ProductoVentaDTO> productoVentaDTOS) {
        this.productoVentaDTOS = productoVentaDTOS;
    }

    public float getTotalGenrealAntesDesc() {
        return totalGenrealAntesDesc;
    }

    public void setTotalGenrealAntesDesc(float totalGenrealAntesDesc) {
        this.totalGenrealAntesDesc = totalGenrealAntesDesc;
    }

    public float getDescuentosEnTotalGen() {
        return descuentosEnTotalGen;
    }

    public void setDescuentosEnTotalGen(float descuentosEnTotalGen) {
        this.descuentosEnTotalGen = descuentosEnTotalGen;
    }

    public float getTotalGenreal() {
        return totalGenreal;
    }

    public void setTotalGenreal(float totalGenreal) {
        this.totalGenreal = totalGenreal;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isTecnico() {
        return tecnico;
    }

    public void setTecnico(boolean tecnico) {
        this.tecnico = tecnico;
    }
}
