package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.GanaciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GanaciaRepository extends JpaRepository<GanaciaEntity, Long> {
    Optional<List<GanaciaEntity>> findByIdVentaRefAndIdProductoRef(Long idProdRef, Long idVentaRef);
}
