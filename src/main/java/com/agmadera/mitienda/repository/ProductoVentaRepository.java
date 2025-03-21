package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.ProductoVentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoVentaRepository extends JpaRepository<ProductoVentaEntity, Long> {
}
