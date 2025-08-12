package com.example.GestionClinique.dto.ResponseDto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class  ConsultationResponseDto extends BaseResponseDto {
    private Float poids;
    private Float taille;
    private String tensionArterielle;
    private Float temperature;
    private String motifs;
    private String diagnostic;
    private String compteRendu;
//    private LocalDateTime dateHeureDebut;
//    private Long dureeMinutes;
//    private Long rendezVousId;
    private String medecinNomComplet;
    private String patientNomComplet;
    private String serviceMedecin;
    private List<PrescriptionResponseDto> prescriptions;
}