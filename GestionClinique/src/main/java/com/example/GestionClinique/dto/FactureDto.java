package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FactureDto {
    private Integer id;
    private Float montant;
    private LocalDate dateEmission;
    private StatutPaiement statutPaiement;
    private ModePaiement modePaiement;
    private PatientDto patient;
    private ConsultationDto consultation; // Renommé pour cohérence avec l'entité

    public static FactureDto fromEntity(Facture facture) {
        if(facture == null) return null;

        return FactureDto.builder()
                .id(facture.getId())
                .montant(facture.getMontant())
                .dateEmission(facture.getDateEmission())
                .statutPaiement(facture.getStatutPaiement())
                .modePaiement(facture.getModePaiement())
                .patient(PatientDto.fromEntity(facture.getPatient()))
                .consultation(ConsultationDto.fromEntity(facture.getConsultation())) // Mappé correctement
                .build();
    }

    public static Facture toEntity(FactureDto factureDto) {
        if(factureDto == null) return null;

        Facture facture = new Facture();
        // L'ID est souvent défini pour les mises à jour, mais pas pour la création.
        // Ici, il est conservé car l'entité Facture a un setId dans toEntity
        facture.setId(factureDto.getId()); // Si l'ID est utilisé pour la mise à jour, assurez-vous qu'il est géré correctement
        facture.setMontant(factureDto.getMontant());
        facture.setDateEmission(factureDto.getDateEmission());
        facture.setStatutPaiement(factureDto.getStatutPaiement());
        facture.setModePaiement(factureDto.getModePaiement());

        // Conversion des DTOs imbriqués en entités
        facture.setPatient(PatientDto.toEntity(factureDto.getPatient()));
        facture.setConsultation(ConsultationDto.toEntity(factureDto.getConsultation())); // Mappé correctement
        return facture;
    }
}