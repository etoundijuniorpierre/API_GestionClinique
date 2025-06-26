package com.example.GestionClinique.dto;// package com.example.GestionClinique.dto; // Make sure this is in the correct DTO package

import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousDto {
    private Integer id;
    private LocalTime heure;
    private LocalDate jour;
    private StatutRDV statut;
    private String notes;
    private ServiceMedical serviceMedical; // Assuming this is a simple string now or enum from entity directly

    // For incoming requests (POST, PUT), use IDs for simplicity
    private Integer patientId;
    private Integer medecinId; // Utilisateur for medecin
    private Integer salleId;
    private Integer consultationId; // For one-to-one relationship

    // For outgoing responses (GET), use summary DTOs to avoid recursion
    // These Summary DTOs must NOT map back to RendezVousDto or any DTO that leads to it.
    private PatientSummaryDto patientSummary;
    private UtilisateurSummaryDto medecinSummary;
    private SalleSummaryDto salleSummary;
    private ConsultationSummaryDto consultationSummary; // NEW: Added if Consultation can be linked back for summary


    public static RendezVousDto fromEntity(RendezVous rendezVous) {
        if(rendezVous == null) {
            return null;
        }

        return RendezVousDto.builder()
                .id(rendezVous.getId())
                .heure(rendezVous.getHeure())
                .jour(rendezVous.getJour())
                .statut(rendezVous.getStatut())
                .notes(rendezVous.getNotes())
                .serviceMedical(rendezVous.getServiceMedical())
                // Populate IDs for convenience on the client side, if needed
                .patientId(rendezVous.getPatient() != null ? rendezVous.getPatient().getId() : null)
                .medecinId(rendezVous.getMedecin() != null ? rendezVous.getMedecin().getId() : null)
                .salleId(rendezVous.getSalle() != null ? rendezVous.getSalle().getId() : null)
                .consultationId(rendezVous.getConsultation() != null ? rendezVous.getConsultation().getId() : null)
                // Populate summary DTOs to avoid recursion
                .patientSummary(PatientSummaryDto.fromEntity(rendezVous.getPatient()))
                .medecinSummary(UtilisateurSummaryDto.fromEntity(rendezVous.getMedecin()))
                .salleSummary(SalleSummaryDto.fromEntity(rendezVous.getSalle()))
                .consultationSummary(ConsultationSummaryDto.fromEntity(rendezVous.getConsultation())) // NEW: Map to summary
                .build();
    }


    public static RendezVous toEntity(RendezVousDto rendezVousDto) {
        if(rendezVousDto == null) {
            return null;
        }

        RendezVous rendezVous = new RendezVous();
        rendezVous.setId(rendezVousDto.getId()); // Set ID if used for updates
        rendezVous.setHeure(rendezVousDto.getHeure());
        rendezVous.setJour(rendezVousDto.getJour());
        rendezVous.setStatut(rendezVousDto.getStatut());
        rendezVous.setNotes(rendezVousDto.getNotes());
        rendezVous.setServiceMedical(rendezVousDto.getServiceMedical());

        // Relationships (Patient, Medecin, Salle, Consultation) should be handled
        // in the service layer based on the IDs provided in the DTO.
        // Do NOT map full DTOs or summary DTOs to entities here to prevent cycles or complex cascade issues.

        return rendezVous;
    }
}