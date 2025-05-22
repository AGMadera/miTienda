package com.agmadera.mitienda.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class GananciaEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    private Long idVentaRef;
    private Long idProductoRef;
    private Date fecha;
    private float compra;
    private float venta;
    private float ganacia;
    //@Column(nullable=true)
    //private int cantidad;
    @Column(nullable=true, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean garantia;

    public GananciaEntity(Long idVentaRef, Long idProductoRef, Date fecha) {
        this.idVentaRef = idVentaRef;
        this.idProductoRef = idProductoRef;
        this.fecha = fecha;
    }

    public GananciaEntity() {

    }

  }
