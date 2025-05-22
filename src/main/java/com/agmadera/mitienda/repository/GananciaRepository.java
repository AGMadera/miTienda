package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.GananciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GananciaRepository extends JpaRepository<GananciaEntity, Long> {
    Optional<List<GananciaEntity>> findByIdVentaRefAndIdProductoRef(Long idProdRef, Long idVentaRef);
}
