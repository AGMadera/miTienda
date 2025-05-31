package com.agmadera.mitienda.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock")
@Setter
@Getter
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private ProductoEntity producto;

    @Column(nullable = false)
    private int unidadesVendidas;
    @Column(nullable = false)
    private int unidadesExistencia;
    @Column(nullable = true, columnDefinition = "integer default 0")
    private int merma;

}
