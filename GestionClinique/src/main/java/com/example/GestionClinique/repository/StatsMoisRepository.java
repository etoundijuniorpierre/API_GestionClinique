package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.stats.StatsMois;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatsMoisRepository extends JpaRepository<StatsMois, Long> {
    Optional<StatsMois> findByMois(String mois);
}