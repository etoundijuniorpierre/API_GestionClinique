package com.example.GestionClinique.repository;



import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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


    // Correction pour countByDateEnregistrement
    @Query("SELECT COUNT(p) FROM Patient p WHERE DATE(p.creationDate) = :date")
    Long countByDateEnregistrement(@Param("date") LocalDate date);

//    // Ces méthodes sont déjà OK
//    @Query("SELECT COUNT(p) FROM Patient p WHERE YEAR(p.creationDate) = :year AND MONTH(p.creationDate) = :month")
//    Long countByMonthEnregistrement(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(p) FROM Patient p WHERE YEAR(p.creationDate) = :year")
    Long countByYearEnregistrement(@Param("year") int year);

    @Query("SELECT COUNT(p) FROM Patient p WHERE MONTH(p.creationDate) = :month")
    long countByMonth(@Param("month") int month);
}
