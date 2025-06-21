package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.RendezVous;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
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
    private List<PrescriptionDto> prescriptions; // Ajouté
    private FactureDto facture; // Ajouté

    public static ConsultationDto fromEntity(Consultation consultation) {
        if(consultation == null) return null;

        // Précaution pour éviter les chargements intempestifs si Lazy et non initialisé
        List<PrescriptionDto> prescriptionDtos = (consultation.getPrescriptions() != null) ?
                consultation.getPrescriptions().stream().map(PrescriptionDto::fromEntity).toList() : null;
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
                .prescriptions(prescriptionDtos) // Mappé
                .facture(factureDto) // Mappé
                .build();
    }


    public static Consultation toEntity(ConsultationDto consultationDto) {
        if(consultationDto == null) return null;

        Consultation consultation = new Consultation();
        // L'ID n'est généralement pas défini ici pour la création d'une nouvelle entité.
        // Pour les mises à jour, on chargerait l'entité existante par son ID.
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
                            .toList()
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

