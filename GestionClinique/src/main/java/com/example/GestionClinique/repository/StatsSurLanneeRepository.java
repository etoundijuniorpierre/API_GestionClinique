package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.stats.StatsSurLannee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatsSurLanneeRepository extends JpaRepository<StatsSurLannee, Long> {
    Optional<StatsSurLannee> findByAnnee(String annee);
}