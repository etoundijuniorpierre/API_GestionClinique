package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import lombok.*;
//import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Builder
public class RendezVousDto {
    private Integer id;
    private LocalTime heure;
    private LocalDate jour;
    private StatutRDV statut;
    private String notes;
    private ServiceMedical serviceMedical;
    private PatientDto patient;
    private UtilisateurDto medecin;
    private SalleDto salle;

    public static RendezVousDto fromEntity(RendezVous rendezVous) {
        if(rendezVous == null) return null;

        return RendezVousDto.builder()
                .id(rendezVous.getId())
                .heure(rendezVous.getHeure())
                .jour(rendezVous.getJour())
                .statut(rendezVous.getStatut())
                .notes(rendezVous.getNotes())
                .serviceMedical(rendezVous.getServiceMedical())
                .patient(PatientDto.fromEntity(rendezVous.getPatient()))
                .medecin(UtilisateurDto.fromEntity(rendezVous.getMedecin())) // Mappé
                .salle(SalleDto.fromEntity(rendezVous.getSalle()))
                .build();
    }

    public static RendezVous toEntity(RendezVousDto rendezVousDto) { // Renommé le paramètre pour clarté
        if(rendezVousDto == null) return null;

        RendezVous rendezVous = new RendezVous();
        // L'ID n'est généralement pas défini ici pour la création d'une nouvelle entité.
        rendezVous.setHeure(rendezVousDto.getHeure());
        rendezVous.setJour(rendezVousDto.getJour());
        rendezVous.setStatut(rendezVousDto.getStatut());
        rendezVous.setNotes(rendezVousDto.getNotes());
        rendezVous.setServiceMedical(rendezVousDto.getServiceMedical());

        // Conversion des DTOs imbriqués en entités
        rendezVous.setPatient(PatientDto.toEntity(rendezVousDto.getPatient()));
        rendezVous.setMedecin(UtilisateurDto.toEntity(rendezVousDto.getMedecin())); // Mappé
        rendezVous.setSalle(SalleDto.toEntity(rendezVousDto.getSalle()));

        return rendezVous;
    }
}
