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
    private DossierMedicalDto dossierMedicalId;
    private UtilisateurDto medecinId;
    private RendezVousDto rendezVous;
    private List<PrescriptionDto> prescriptions;
    private FactureDto facture;

}