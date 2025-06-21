package com.example.GestionClinique.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.GestionClinique.dto.DossierMedicalDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.model.EntityAbstracte;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Patient;
import io.micrometer.observation.ObservationFilter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Integer> {



}
