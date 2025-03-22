package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.ProductoVentaEntity;
import com.agmadera.mitienda.entities.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<VentaEntity, Long> {
}
