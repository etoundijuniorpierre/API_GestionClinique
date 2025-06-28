package com.example.GestionClinique.repository;


import com.example.GestionClinique.dto.ConsultationDto;
import com.example.GestionClinique.model.EntityAbstracte;
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.RendezVous;
import io.micrometer.observation.ObservationFilter;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {

}
