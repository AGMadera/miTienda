package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.GarantiaVale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GarantiaValeRespository extends JpaRepository<GarantiaVale, Long> {
    Optional<GarantiaVale>findByFolio(String folio);
}
