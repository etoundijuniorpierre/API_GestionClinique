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
    private String medecinNomComplet; //qui correspond ici à l'utilisateur(medecin) qui c'est login et qui effectue actuellement la consultation
    private String patientNomComplet; //à recevoir du rendezVous
    private String serviceMedecin; //à récupérer chez l'utilisateur(médecin)
//    private Long factureId;
    private List<PrescriptionResponseDto> prescriptions;
}