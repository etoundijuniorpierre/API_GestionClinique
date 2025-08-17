package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;


public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByRendezVousId(Long rendezVousId);


    // Correction pour countByDateConsultation
    @Query("SELECT COUNT(c) FROM Consultation c WHERE DATE(c.creationDate) = :date")
    Long countByDateConsultation(@Param("date") LocalDate date);

    // Ces méthodes sont déjà OK
    @Query("SELECT COUNT(c) FROM Consultation c WHERE YEAR(c.creationDate) = :year AND MONTH(c.creationDate) = :month")
    Long countByMonthConsultation(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE YEAR(c.creationDate) = :year")
    Long countByYearConsultation(@Param("year") int year);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE MONTH(c.creationDate) = :month")
    long countByMonth(@Param("month") int month);



}


