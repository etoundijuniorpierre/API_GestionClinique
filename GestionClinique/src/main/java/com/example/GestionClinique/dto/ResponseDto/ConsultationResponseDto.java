package com.example.GestionClinique.dto.ResponseDto;


import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime dateHeureDebut;
    private Integer dureeMinutes;

    private Long dossierMedicalId;
    private Long medecinId;
    private Long rendezVousId;
    private Long factureId;

    private List<PrescriptionResponseDto> prescriptions;
}