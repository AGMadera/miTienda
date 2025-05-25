package com.agmadera.mitienda.repositories;

import com.agmadera.mitienda.entities.GarantiaVale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GarantiaValeRespository extends JpaRepository<GarantiaVale, Long> {
    Optional<GarantiaVale>findByFolio(String folio);
}
