package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.DossierMedical;
import lombok.*;
import java.time.LocalDateTime; // Use LocalDateTime for creationDate
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DossierMedicalDto {
    private Integer id;
    private LocalDateTime creationDate; // Inherited from EntityAbstracte, included for read-only DTO
    private String groupeSanguin; // Added field
    private String antecedentsMedicaux;
    private String allergies;
    private String traitementsEnCours;
    private String observations;

    private PatientSummaryDto patientSummary;
    private List<ConsultationSummaryDto> consultationSummaries;
    private List<PrescriptionDto> prescriptions; // Renamed for clarity

    public static DossierMedicalDto fromEntity(DossierMedical dossierMedical){
        if(dossierMedical == null) return null;

        List<ConsultationSummaryDto> consultationSummaries = (dossierMedical.getConsultations() != null) ?
                dossierMedical.getConsultations().stream().map(ConsultationSummaryDto::fromEntity).collect(Collectors.toList()) : null;
        List<PrescriptionDto> prescriptions = (dossierMedical.getPrescriptions() != null) ?
                dossierMedical.getPrescriptions().stream().map(PrescriptionDto::fromEntity).collect(Collectors.toList()) : null;

        return DossierMedicalDto.builder()
                .id(dossierMedical.getId())
                .creationDate(dossierMedical.getCreationDate()) // Mapping from EntityAbstracte's creationDate
                .groupeSanguin(dossierMedical.getGroupeSanguin()) // Mapping new field
                .antecedentsMedicaux(dossierMedical.getAntecedentsMedicaux())
                .allergies(dossierMedical.getAllergies())
                .traitementsEnCours(dossierMedical.getTraitementsEnCours())
                .observations(dossierMedical.getObservations())
                .patientSummary(PatientSummaryDto.fromEntity(dossierMedical.getPatient()))
                .consultationSummaries(consultationSummaries)
                .prescriptions(prescriptions)
                .build();
    }

    public static DossierMedical toEntity(DossierMedicalDto dossierMedicalDto){
        if(dossierMedicalDto == null) return null;

        DossierMedical dossierMedical = new DossierMedical();
        dossierMedical.setId(dossierMedicalDto.getId()); // Set ID if used for updates
        // creationDate and modificationDate are handled by @CreationTimestamp/@UpdateTimestamp on entity, not set directly from DTO
        dossierMedical.setGroupeSanguin(dossierMedicalDto.getGroupeSanguin()); // Mapping new field
        dossierMedical.setAntecedentsMedicaux(dossierMedicalDto.getAntecedentsMedicaux());
        dossierMedical.setAllergies(dossierMedicalDto.getAllergies());
        dossierMedical.setTraitementsEnCours(dossierMedicalDto.getTraitementsEnCours());
        dossierMedical.setObservations(dossierMedicalDto.getObservations());

        return dossierMedical;
    }
}