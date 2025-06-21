package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Prescription;
import lombok.*;

import java.time.LocalDate;


@Data
@Builder
public class PrescriptionDto {
    private Integer id;
    private LocalDate datePrescription;
    private String typePrescription;
    private String medicaments;
    private String instructions;
    private String dureePrescription;
    private Integer quantite;

    private ConsultationDto consultation;
    private UtilisateurDto medecin;
    private PatientDto patient;
    private DossierMedicalDto dossierMedical;

    public static PrescriptionDto fromEntity(Prescription prescription) {
        if(prescription == null) {
            return null;
        }

        return PrescriptionDto.builder()
                .id(prescription.getId())
                .datePrescription(prescription.getDatePrescription())
                .typePrescription(prescription.getTypePrescription())
                .medicaments(prescription.getMedicaments())
                .instructions(prescription.getInstructions())
                .dureePrescription(prescription.getDureePrescription())
                .quantite(prescription.getQuantite())
                .consultation(ConsultationDto.fromEntity(prescription.getConsultation()))
                .medecin(UtilisateurDto.fromEntity(prescription.getMedecin()))
                .patient(PatientDto.fromEntity(prescription.getPatient()))
                .dossierMedical(DossierMedicalDto.fromEntity(prescription.getDossierMedical()))
                .build();
    }

    public static Prescription toEntity(PrescriptionDto prescriptionDto) {
        if(prescriptionDto == null) {
            return null;
        }

        Prescription prescription = new Prescription();
        // L'ID n'est généralement pas défini ici pour la création d'une nouvelle entité.
        prescription.setDatePrescription(prescriptionDto.getDatePrescription());
        prescription.setTypePrescription(prescriptionDto.getTypePrescription());
        prescription.setMedicaments(prescriptionDto.getMedicaments());
        prescription.setInstructions(prescriptionDto.getInstructions());
        prescription.setDureePrescription(prescriptionDto.getDureePrescription());
        prescription.setQuantite(prescriptionDto.getQuantite());

        // Conversion des DTOs imbriqués en entités
        prescription.setConsultation(ConsultationDto.toEntity(prescriptionDto.getConsultation()));
        prescription.setMedecin(UtilisateurDto.toEntity(prescriptionDto.getMedecin()));
        prescription.setPatient(PatientDto.toEntity(prescriptionDto.getPatient()));
        prescription.setDossierMedical(DossierMedicalDto.toEntity(prescriptionDto.getDossierMedical()));

        return prescription;
    }
}

