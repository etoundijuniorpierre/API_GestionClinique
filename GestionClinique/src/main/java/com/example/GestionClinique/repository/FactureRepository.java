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


    // Utilise la fonction DATE() ou TRUNC() selon ta base de données pour extraire la partie date
    @Query("SELECT COALESCE(SUM(f.montant), 0.0) FROM Facture f WHERE DATE(f.dateEmission) = :date")
    Double sumMontantTotalByDateFacture(@Param("date") LocalDate date);

//    // Ces méthodes sont déjà OK car elles extraient YEAR et MONTH
//    @Query("SELECT COALESCE(SUM(f.montant), 0.0) FROM Facture f WHERE YEAR(f.dateEmission) = :year AND MONTH(f.dateEmission) = :month")
//    Double sumMontantTotalByMonthFacture(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(f.montant), 0.0) FROM Facture f WHERE YEAR(f.dateEmission) = :year")
    Double sumMontantTotalByYearFacture(@Param("year") int year);
    Optional<Object> findByConsultationId(Long consultationId);

    @Query("SELECT SUM(f.montant) FROM Facture f WHERE MONTH(f.creationDate) = :month")
    Double sumRevenueByMonth(@Param("month") int month);
}
