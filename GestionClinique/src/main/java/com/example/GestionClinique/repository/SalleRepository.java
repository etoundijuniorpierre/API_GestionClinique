package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface SalleRepository extends JpaRepository<Salle, Long> {

    List<Salle> findByStatutSalle(StatutSalle statutSalle);

    Optional<Salle> findByNumeroSalle(String numeroSalle);

    Salle findByServiceMedical(ServiceMedical serviceMedical);

//    @Query("SELECT s FROM Salle s WHERE s.id NOT IN (" +
//            "  SELECT r.salle.id FROM RendezVous r " +
//            "  WHERE (:start < r.dateHeureFin AND :end > r.dateHeureDebut)" +
//            ")")
//    List<Salle> findAvailableSalles(
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end);
}