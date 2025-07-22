package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.stats.StatMoisEncours;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatMoisEncoursRepository extends JpaRepository<StatMoisEncours, Long> {
    Optional<StatMoisEncours> findByMoisEncours(String moisEncours);
}