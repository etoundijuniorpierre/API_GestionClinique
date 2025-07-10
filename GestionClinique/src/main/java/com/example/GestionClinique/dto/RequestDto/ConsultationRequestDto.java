package com.example.GestionClinique.dto.RequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequestDto {

    @NotBlank(message = "Le motif de la consultation est requis.")
    private String motifs;

    @NotBlank(message = "La tension artérielle est requise.")
    private String tensionArterielle;

    @NotNull(message = "La température est requise.")
    private Float temperature;

    @NotNull(message = "Le poids est requis.")
    private Float poids;

    @NotNull(message = "La taille est requise.")
    private Float taille;

    @NotBlank(message = "Le compte rendu est requis.")
    private String compteRendu;

    @NotBlank(message = "Le diagnostic est requis.")
    private String diagnostic;

//    @NotNull(message = "La date et l'heure de début sont requises.")
//    private LocalDateTime dateHeureDebut;
//
//    @NotNull(message = "La durée de la consultation est requise.")
//    private Long dureeMinutes;

//    @NotNull(message = "L'ID du dossier médical est requis.")
//    private Long dossierMedicalId;

    private Long rendezVousId;

    private List<PrescriptionRequestDto> prescriptions;
}