package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.dto.dtoConnexion.PrescriptionResponseDto;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConsultationResponseDto extends BaseResponseDto {
    private String motifs;
    private String tensionArterielle;
    private Float temperature;
    private Float poids;
    private Float taille;
    private String compteRendu;
    private String diagnostic;
    private DossierMedicalResponseDto dossierMedical;
    private UtilisateurResponseDto medecin;
    private RendezVousResponseDto rendezVous;
    private List<PrescriptionResponseDto> prescriptions;
    private FactureResponseDto facture;

}