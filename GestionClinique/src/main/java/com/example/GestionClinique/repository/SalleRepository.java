package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface SalleRepository extends JpaRepository<Salle, Long> {

    List<Salle> findByStatutSalle(StatutSalle statutSalle);

    Optional<Salle> findByNumero(String numero);


//    @Query("SELECT s FROM Salle s WHERE s.statutSalle = 'DISPONIBLE' AND s.id NOT IN (" +
//            "  SELECT rv.salle.id FROM RendezVous rv JOIN rv.consultation c " +
//            "  WHERE rv.salle IS NOT NULL AND c IS NOT NULL " +
//            "  AND (:slotStart < (c.dateHeureDebut + c.dureeMinutes * INTERVAL '1 MINUTE') AND :slotEnd > c.dateHeureDebut)" +
//            ")")
    List<Salle> findAvailableSalles(
            @Param("slotStart") LocalDateTime slotStart,
            @Param("slotEnd") LocalDateTime slotEnd);
}