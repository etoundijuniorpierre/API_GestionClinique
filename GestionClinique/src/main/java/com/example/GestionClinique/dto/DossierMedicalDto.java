package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.*;
import lombok.*;

import java.util.List;


@Data
@Builder
public class DossierMedicalDto {
    private Integer id;
    private String antecedents;
    private String allergies;
    private String traitementsEnCours;
    private String observations;
    private PatientDto patient;
    private List<ConsultationDto> consultations; // Ajouté
    private List<PrescriptionDto> prescriptions; // Ajouté

    public static DossierMedicalDto fromEntity( DossierMedical dossierMedical){
        if(dossierMedical == null) return null;

        // Précaution pour éviter les chargements intempestifs si Lazy et non initialisé
        List<ConsultationDto> consultationDtos = (dossierMedical.getConsultations() != null) ?
                dossierMedical.getConsultations().stream().map(ConsultationDto::fromEntity).toList() : null;
        List<PrescriptionDto> prescriptionDtos = (dossierMedical.getPrescriptions() != null) ?
                dossierMedical.getPrescriptions().stream().map(PrescriptionDto::fromEntity).toList() : null;


        return DossierMedicalDto.builder()
                .id(dossierMedical.getId())
                .antecedents(dossierMedical.getAntecedents())
                .allergies(dossierMedical.getAllergies())
                .traitementsEnCours(dossierMedical.getTraitementsEnCours())
                .observations(dossierMedical.getObservations())
                .patient(PatientDto.fromEntity(dossierMedical.getPatient()))
                .consultations(consultationDtos) // Mappé
                .prescriptions(prescriptionDtos) // Mappé
                .build();
    }

    public static DossierMedical toEntity(DossierMedicalDto dossierMedicalDto){
        if(dossierMedicalDto == null) return null;
        DossierMedical dossierMedical = new DossierMedical();
        // L'ID n'est généralement pas défini ici pour la création d'une nouvelle entité.
        dossierMedical.setAntecedents(dossierMedicalDto.getAntecedents());
        dossierMedical.setAllergies(dossierMedicalDto.getAllergies());
        dossierMedical.setTraitementsEnCours(dossierMedicalDto.getTraitementsEnCours());
        dossierMedical.setObservations(dossierMedicalDto.getObservations());

        // Conversion des DTOs imbriqués en entités
        dossierMedical.setPatient(PatientDto.toEntity(dossierMedicalDto.getPatient()));

        if (dossierMedicalDto.getConsultations() != null) {
            dossierMedical.setConsultations(
                    dossierMedicalDto.getConsultations().stream()
                            .map(ConsultationDto::toEntity)
                            .peek(c -> c.setDossierMedical(dossierMedical)) // Définir la relation inverse
                            .toList()
            );
        }
        if (dossierMedicalDto.getPrescriptions() != null) {
            dossierMedical.setPrescriptions(
                    dossierMedicalDto.getPrescriptions().stream()
                            .map(PrescriptionDto::toEntity)
                            .peek(p -> p.setDossierMedical(dossierMedical)) // Définir la relation inverse
                            .toList()
            );
        }

        return dossierMedical;
    }
}
