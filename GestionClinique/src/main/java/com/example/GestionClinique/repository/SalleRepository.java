package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface SalleRepository extends JpaRepository<Salle, Integer> {

    @Query("SELECT s FROM Salle s WHERE s.numero = :numero")
    Optional<Salle> findByNumero(@Param("numero") String numero);

    @Query("SELECT s FROM Salle s WHERE s.statutSalle =:statutSalle")
    Collection<Salle> findSallesByStatut(@Param("statutSalle") StatutSalle statutSalle);

    Collection<Object> findByServiceMedical(ServiceMedical serviceMedicalEnum);

    List<Salle> findByStatutSalle(StatutSalle statutSalle);
}

