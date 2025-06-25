package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.Facture;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors; // Ensure this import is present

@Data
@Builder
@AllArgsConstructor // Added AllArgsConstructor because you are using @Builder
@NoArgsConstructor // Added NoArgsConstructor because you are using @Builder
public class ConsultationDto {
    private Integer id;
    private String motifs;
    private String tensionArterielle;
    private Float temperature;
    private Float poids;
    private Float taille;
    private String compteRendu;
    private String diagnostic;
    private DossierMedicalDto dossierMedical;
    private UtilisateurDto medecin;
    private RendezVousDto rendezVous;
    private List<PrescriptionDto> prescriptions;
    private FactureDto facture;

    public static ConsultationDto fromEntity(Consultation consultation) {
        if(consultation == null) return null;

        // Précaution pour éviter les chargements intempestifs si Lazy et non initialisé
        List<PrescriptionDto> prescriptionDtos = (consultation.getPrescriptions() != null) ?
                consultation.getPrescriptions().stream()
                        .map(PrescriptionDto::fromEntity) // This call is now safe
                        .collect(Collectors.toList()) : null; // Changed to .collect(Collectors.toList())
        FactureDto factureDto = (consultation.getFacture() != null) ? FactureDto.fromEntity(consultation.getFacture()) : null;

        return ConsultationDto.builder()
                .id(consultation.getId())
                .motifs(consultation.getMotifs())
                .tensionArterielle(consultation.getTensionArterielle())
                .temperature(consultation.getTemperature())
                .poids(consultation.getPoids())
                .taille(consultation.getTaille())
                .compteRendu(consultation.getCompteRendu())
                .diagnostic(consultation.getDiagnostic())
                .dossierMedical(DossierMedicalDto.fromEntity(consultation.getDossierMedical()))
                .medecin(UtilisateurDto.fromEntity(consultation.getMedecin()))
                .rendezVous(RendezVousDto.fromEntity(consultation.getRendezVous()))
                .prescriptions(prescriptionDtos)
                .facture(factureDto)
                .build();
    }


    public static Consultation toEntity(ConsultationDto consultationDto) {
        if(consultationDto == null) return null;

        Consultation consultation = new Consultation();
        consultation.setMotifs(consultationDto.getMotifs());
        consultation.setTensionArterielle(consultationDto.getTensionArterielle());
        consultation.setTemperature(consultationDto.getTemperature());
        consultation.setPoids(consultationDto.getPoids());
        consultation.setTaille(consultationDto.getTaille());
        consultation.setCompteRendu(consultationDto.getCompteRendu());
        consultation.setDiagnostic(consultationDto.getDiagnostic());

        // Conversion des DTOs imbriqués en entités
        consultation.setDossierMedical(DossierMedicalDto.toEntity(consultationDto.getDossierMedical()));
        consultation.setMedecin(UtilisateurDto.toEntity(consultationDto.getMedecin()));
        consultation.setRendezVous(RendezVousDto.toEntity(consultationDto.getRendezVous()));

        // Gérer les listes de DTOs si elles sont présentes et qu'elles ne sont pas null
        if (consultationDto.getPrescriptions() != null) {
            consultation.setPrescriptions(
                    consultationDto.getPrescriptions().stream()
                            .map(PrescriptionDto::toEntity)
                            .peek(p -> p.setConsultation(consultation)) // Assurez-vous que la relation inverse est définie
                            .collect(Collectors.toList()) // Changed to .collect(Collectors.toList())
            );
        }
        if (consultationDto.getFacture() != null) {
            Facture facture = FactureDto.toEntity(consultationDto.getFacture());
            facture.setConsultation(consultation); // Définir la relation inverse
            consultation.setFacture(facture);
        }

        return consultation;
    }
}