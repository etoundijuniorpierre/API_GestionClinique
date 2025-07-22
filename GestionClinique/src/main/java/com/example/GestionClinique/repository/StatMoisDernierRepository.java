package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.stats.StatMoisDernier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatMoisDernierRepository extends JpaRepository<StatMoisDernier, Long> {
    Optional<StatMoisDernier> findByMoisDernier(String moisDernier);
}