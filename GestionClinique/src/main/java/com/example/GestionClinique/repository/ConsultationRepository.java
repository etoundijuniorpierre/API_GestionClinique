package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByRendezVousId(Long rendezVousId);
}
