package com.example.GestionClinique.repository;


import aj.org.objectweb.asm.commons.Remapper;
import com.example.GestionClinique.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface PatientRepository extends JpaRepository<Patient, Integer> {


    Optional<Patient> findPatientByInfoPersonnel_Email(String email);

    Collection<Patient> findPatientByInfoPersonnel_Nom(String nom);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.infoPersonnel.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.infoPersonnel.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.infoPersonnel.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Collection<Patient> findPatientBySearchTerm(@Param("searchTerm") String searchTerm);
}
