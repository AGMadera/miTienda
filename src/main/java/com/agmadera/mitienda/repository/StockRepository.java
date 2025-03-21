package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.entities.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntity, Long> {
    StockEntity findByProducto(ProductoEntity entity);
}
