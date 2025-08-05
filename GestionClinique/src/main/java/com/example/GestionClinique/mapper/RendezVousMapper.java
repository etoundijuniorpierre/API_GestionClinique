package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.dto.ResponseDto.RendezVousResponseDto;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.FactureRepository;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.RendezVousService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class RendezVousMapper {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private RendezVousService rendezVousRepository;


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", source = "patientId", qualifiedByName = "mapPatientIdToPatient")
    @Mapping(target = "medecin", source = "medecinId", qualifiedByName = "mapMedecinIdToMedecin")
    @Mapping(target = "salle", ignore = true) // <-- IMPORTANT CHANGE: Salle will be set by the service based on serviceMedical
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "facture", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    public abstract RendezVous toEntity(RendezVousRequestDto dto);


    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(source = "salle.id", target = "salleId") // Still map ID from entity to response DTO
    @Mapping(source = "serviceMedical", target = "serviceMedical")
    @Mapping(target = "patientNomComplet", expression = "java(entity.getPatient() != null ? " +
            "entity.getPatient().getPrenom() + \" \" + entity.getPatient().getNom() : null)")
    @Mapping(target = "medecinNomComplet", expression = "java(entity.getMedecin() != null ? " +
            "entity.getMedecin().getPrenom() + \" \" + entity.getMedecin().getNom() : null)")
    @Mapping(source = "salle.numeroSalle", target = "nomSalle",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "factureId", expression = "java(entity.getFacture() != null ? entity.getFacture().getId() : null) ")
    public abstract RendezVousResponseDto toDto(RendezVous entity);

    public abstract List<RendezVousResponseDto> toDtoList(List<RendezVous> entities);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    @Mapping(target = "salle", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    @Mapping(target = "facture", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "jour", source = "jour")
    @Mapping(target = "heure", source = "heure")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "serviceMedical", source = "serviceMedical")
    public abstract void updateEntityFromDto(RendezVousRequestDto dto, @MappingTarget RendezVous entity);



    @Named("mapPatientIdToPatient")
    public Patient mapPatientIdToPatient(Long patientId) {
        if (patientId == null) {
            return null;
        }
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));
    }

    @Named("mapMedecinIdToMedecin")
    public Utilisateur mapMedecinIdToMedecin(Long medecinId) {
        if (medecinId == null) {
            return null;
        }
        return utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + medecinId));
    }

//    @Named("mapFactureId")
//    public RendezVous mapFactureId(Long rendezVousId) {
//        if (rendezVousId == null) {
//            return null;
//        }
//        return rendezVousRepository.findRendezVousById(rendezVousId);
//    }

}