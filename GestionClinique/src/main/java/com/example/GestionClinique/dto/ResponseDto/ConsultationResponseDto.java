package com.example.GestionClinique.dto.ResponseDto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class  ConsultationResponseDto extends BaseResponseDto {
    private String motifs;
    private String tensionArterielle;
    private Float temperature;
    private Float poids;
    private Float taille;
    private String compteRendu;
    private String diagnostic;
//    private LocalDateTime dateHeureDebut;
//    private Long dureeMinutes;
//    private Long rendezVousId;
    private String medecinNomComplet;
    private String patientNomComplet;
    private String serviceMedecin;
    private List<PrescriptionResponseDto> prescriptions;
}