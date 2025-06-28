package com.example.GestionClinique.dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
public class ConsultationRequestDto {

    @NotNull
    private String motifs;

    @NotNull
    private String tensionArterielle;

    @NotNull
    private Float temperature;

    @NotNull
    private Float poids;

    @NotNull
    private Float taille;

    @NotNull
    private String compteRendu;

    @NotNull
    private String diagnostic;

    @NotNull
    private Long dossierMedicalId;

    @NotNull
    private Long medecinId;

    private Long rendezVousId;

    @NotNull
    private List<PrescriptionRequestDto> prescriptions;

    @NotNull
    private Long factureId;

}