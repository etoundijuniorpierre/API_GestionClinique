// PrescriptionDto.java (updated)
package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Prescription;
import lombok.*;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDto {
    private Integer id;
    private LocalDate datePrescription;
    private String typePrescription;
    private String medicaments;
    private String instructions;
    private String dureePrescription;
    private Integer quantite;

    private Integer consultationId;

    // Use Summary DTOs to avoid recursion
    private UtilisateurSummaryDto medecinSummary;
    private PatientSummaryDto patientSummary;
    private DossierMedicalSummaryDto dossierMedicalSummary; // Correctly referencing the new DTO

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
                .consultationId(prescription.getConsultation() != null ? prescription.getConsultation().getId() : null)
                // Use Summary DTOs for related objects
                .medecinSummary(UtilisateurSummaryDto.fromEntity(prescription.getMedecin()))
                .patientSummary(PatientSummaryDto.fromEntity(prescription.getPatient()))
                .dossierMedicalSummary(DossierMedicalSummaryDto.fromEntity(prescription.getDossierMedical())) // This is the fix!
                .build();
    }

    public static Prescription toEntity(PrescriptionDto prescriptionDto) {
        if(prescriptionDto == null) {
            return null;
        }

        Prescription prescription = new Prescription();
        prescription.setId(prescriptionDto.getId());
        prescription.setDatePrescription(prescriptionDto.getDatePrescription());
        prescription.setTypePrescription(prescriptionDto.getTypePrescription());
        prescription.setMedicaments(prescriptionDto.getMedicaments());
        prescription.setInstructions(prescriptionDto.getInstructions());
        prescription.setDureePrescription(prescriptionDto.getDureePrescription());
        prescription.setQuantite(prescriptionDto.getQuantite());

        // Relationships for `toEntity` should be handled in the service layer using IDs.
        // Avoid mapping full nested DTOs here to prevent potential issues.
        return prescription;
    }
}