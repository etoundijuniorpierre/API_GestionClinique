package com.example.GestionClinique.dto;


import com.example.GestionClinique.model.EntityAbstracte;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@Builder
public class PatientDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel;
    private List<RendezVousDto> rendezVous; // Ajouté
    private List<FactureDto> factures; // Ajouté
    private List<PrescriptionDto> prescriptions; // Ajouté

    public static PatientDto fromEntity(Patient patient) {
        if(patient == null) return null;

        // Précaution pour éviter les chargements intempestifs si Lazy et non initialisé
        List<RendezVousDto> rendezVousDtos = (patient.getRendezVous() != null) ?
                patient.getRendezVous().stream().map(RendezVousDto::fromEntity).toList() : null;
        List<FactureDto> factureDtos = (patient.getFactures() != null) ?
                patient.getFactures().stream().map(FactureDto::fromEntity).toList() : null;
        List<PrescriptionDto> prescriptionDtos = (patient.getPrescriptions() != null) ?
                patient.getPrescriptions().stream().map(PrescriptionDto::fromEntity).toList() : null;

        return PatientDto.builder()
                .id(patient.getId())
                .infoPersonnel(InfoPersonnelDto.fromEntity(patient.getInfoPersonnel()))
                .rendezVous(rendezVousDtos) // Mappé
                .factures(factureDtos) // Mappé
                .prescriptions(prescriptionDtos) // Mappé
                .build();
    }

    public static Patient toEntity(PatientDto patientDto) {
        if(patientDto == null) return null;

        Patient patient = new Patient();
        // L'ID n'est généralement pas défini ici pour la création d'une nouvelle entité.
        patient.setInfoPersonnel(InfoPersonnelDto.toEntity(patientDto.infoPersonnel));

        // Gérer les listes de DTOs
        if (patientDto.getRendezVous() != null) {
            patient.setRendezVous(
                    patientDto.getRendezVous().stream()
                            .map(RendezVousDto::toEntity)
                            .peek(rv -> rv.setPatient(patient)) // Définir la relation inverse
                            .toList()
            );
        }
        if (patientDto.getFactures() != null) {
            patient.setFactures(
                    patientDto.getFactures().stream()
                            .map(FactureDto::toEntity)
                            .peek(f -> f.setPatient(patient)) // Définir la relation inverse
                            .toList()
            );
        }
        if (patientDto.getPrescriptions() != null) {
            patient.setPrescriptions(
                    patientDto.getPrescriptions().stream()
                            .map(PrescriptionDto::toEntity)
                            .peek(p -> p.setPatient(patient)) // Définir la relation inverse
                            .toList()
            );
        }

        return patient;
    }
}
