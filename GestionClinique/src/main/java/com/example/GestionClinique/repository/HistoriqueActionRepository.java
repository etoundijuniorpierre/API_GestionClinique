package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Integer > {
    Collection<HistoriqueAction> findByDateAfterAndDateBefore(@Param("startDate")LocalDate startDate, @Param("endDate") LocalDate endDate);
}
