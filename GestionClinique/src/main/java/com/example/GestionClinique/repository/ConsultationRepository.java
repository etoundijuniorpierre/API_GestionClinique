package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {

}
