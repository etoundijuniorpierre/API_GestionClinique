package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByStatutPaiement(StatutPaiement statutPaiement);
    List<Facture> findByModePaiement(ModePaiement modePaiement);
    Optional<Facture> findByRendezVousId(Long id);


    @Query("SELECT COALESCE(SUM(f.montant), 0.0) FROM Facture f WHERE f.dateEmission = :date")
    Double sumMontantTotalByDateFacture(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(f.montant), 0.0) FROM Facture f WHERE YEAR(f.dateEmission) = :year AND MONTH(f.dateEmission) = :month")
    Double sumMontantTotalByMonthFacture(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(f.montant), 0.0) FROM Facture f WHERE YEAR(f.dateEmission) = :year")
    Double sumMontantTotalByYearFacture(@Param("year") int year);

    Optional<Object> findByConsultationId(Long consultationId);
}
