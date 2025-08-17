package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {


    List<RendezVous> findByJour(LocalDate jour);

    Optional<RendezVous> findByJourAndHeureAndSalleId(LocalDate jour, LocalTime heure, Long salleId);

    Optional<RendezVous> findByJourAndHeureAndMedecinId(LocalDate jour, LocalTime heure, Long medecinId);

    @Query("SELECT r FROM RendezVous r WHERE " +
            "LOWER(r.patient.nom) LIKE LOWER(CONCAT('%', :patientSearchTerm, '%')) OR " +
            "LOWER(r.patient.prenom) LIKE LOWER(CONCAT('%', :patientSearchTerm, '%')) OR " +
            "LOWER(r.patient.email) LIKE LOWER(CONCAT('%', :patientSearchTerm, '%')) OR " +
            "CAST(r.patient.id AS string) LIKE CONCAT('%', :patientSearchTerm, '%')")
    List<RendezVous> findRendezVousByPatientSearchTerm(@Param("patientSearchTerm") String patientSearchTerm);



    @Query("SELECT r FROM RendezVous r WHERE " +
            "(LOWER(r.patient.nom) LIKE LOWER(CONCAT('%', :patientName, '%')) OR " +
            "LOWER(r.patient.prenom) LIKE LOWER(CONCAT('%', :patientName, '%'))) AND " +
            "r.statut = :statut")
    List<RendezVous> findRendezVousForPatientByStatus(@Param("patientName") String patientName, @Param("statut") StatutRDV statut);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.statut = :statut " +
            "AND r.jour >= :currentDate ORDER BY r.jour ASC")
    List<RendezVous> findConfirmedRendezVousFromTodayByMedecin(
            @Param("medecinId") Long medecinId,
            @Param("statut") StatutRDV statut,
            @Param("currentDate") LocalDate currentDate
    );

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.statut = :statut " +
            "ORDER BY r.jour ASC")
    List<RendezVous> findAllConfirmedRendezVousByMedecin(
            @Param("medecinId") Long medecinId,
            @Param("statut") StatutRDV statut
    );



    @Query("SELECT r FROM RendezVous r WHERE " +
            "LOWER(r.medecin.nom) LIKE LOWER(CONCAT('%', :medecinSearchTerm, '%')) OR " +
            "LOWER(r.medecin.prenom) LIKE LOWER(CONCAT('%', :medecinSearchTerm, '%')) OR " +
            "LOWER(r.medecin.email) LIKE LOWER(CONCAT('%', :medecinSearchTerm, '%')) OR " +
            "CAST(r.medecin.id AS string) LIKE CONCAT('%', :medecinSearchTerm, '%')")
    List<RendezVous> findRendezVousByMedecinSearchTerm(@Param("medecinSearchTerm") String medecinSearchTerm);


    @Query("SELECT r FROM RendezVous r WHERE " +
            "(LOWER(r.medecin.nom) LIKE LOWER(CONCAT('%', :medecinName, '%')) OR " +
            "LOWER(r.medecin.prenom) LIKE LOWER(CONCAT('%', :medecinName, '%'))) AND " +
            "r.statut = :statut")
    List<RendezVous> findRendezVousForMedecinByStatus(@Param("medecinName") String medecinName, @Param("statut") StatutRDV statut);


    List<RendezVous> findBySalleId(Long salleId);

    List<RendezVous> findByStatut(StatutRDV statut);


    @Query("SELECT r FROM RendezVous r WHERE " +
            "r.medecin.id = :medecinId AND " + 
            "r.statut = 'CONFIRME' AND r.jour = :date")
    List<RendezVous> findConfirmedRendezVousForMedecinAndDate(
            @Param("medecinId") Long medecinId,
            @Param("date") LocalDate date);


    // Correction pour countByJourAndStatut
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE DATE(r.jour) = :date AND r.statut = :statut")
    Long countByJourAndStatut(@Param("date") LocalDate date, @Param("statut") StatutRDV statut);

    // Ces méthodes sont déjà OK
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE YEAR(r.jour) = :year AND MONTH(r.jour) = :month AND r.statut = :statut")
    Long countByMoisAndStatut(@Param("year") int year, @Param("month") int month, @Param("statut") StatutRDV statut);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE YEAR(r.jour) = :year AND r.statut = :statut")
    Long countByAnneeAndStatut(@Param("year") int year, @Param("statut") StatutRDV statut);

    @Query("SELECT r FROM RendezVous r WHERE r.jour < :today")
    List<RendezVous> findByJourBefore(@Param("today") LocalDate jourBefore);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :utilisateurId AND r.statut = :statut AND r.jour BETWEEN :startDate AND :endDate")
    List<RendezVous> findConfirmedByUtilisateurAndMonth(
            @Param("utilisateurId") Long utilisateurId,
            @Param("statut") StatutRDV statut,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<RendezVous> findByJourBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.statut = :status AND MONTH(r.creationDate) = :month")
    long countByStatusAndMonth(@Param("status") StatutRDV status, @Param("month") int month);
}
