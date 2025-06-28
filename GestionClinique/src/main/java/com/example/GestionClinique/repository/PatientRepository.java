package com.example.GestionClinique.repository;



import com.example.GestionClinique.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PatientRepository extends JpaRepository<Patient, Long> {


    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Patient> searchByTerm(String searchTerm);

    List<Patient> findByNom(String nom);

    Optional<Patient> findByEmail(String email);
}
