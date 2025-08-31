package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.stats.StatDuJour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatDuJourRepository extends JpaRepository<StatDuJour, Long> {
    Optional<StatDuJour> findByJour(String jour);
}