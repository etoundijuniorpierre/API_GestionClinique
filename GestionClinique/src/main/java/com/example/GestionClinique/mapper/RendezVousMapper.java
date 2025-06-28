package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.dto.ResponseDto.RendezVousResponseDto;
import com.example.GestionClinique.model.entity.RendezVous;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RendezVousMapper {


//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "patient", ignore = true)
//    @Mapping(target = "medecin", ignore = true)
//    @Mapping(target = "salle", ignore = true)
//    @Mapping(target = "consultation", ignore = true)
    RendezVous toEntity(RendezVousRequestDto dto);


    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(source = "salle.id", target = "salleId")
    @Mapping(source = "consultation.id", target = "consultationId") // Map consultation ID if exists
    @Mapping(target = "patientNomComplet", expression = "java(entity.getPatient().getNom() + \" \" + entity.getPatient().getPrenom())")
    @Mapping(target = "medecinNomComplet", expression = "java(entity.getMedecin().getNom() + \" \" + entity.getMedecin().getPrenom())")
    @Mapping(source = "salle.nom", target = "nomSalle") // Assuming Salle has getNom()
    RendezVousResponseDto toDto(RendezVous entity);


    List<RendezVousResponseDto> toDtoList(List<RendezVous> entities);


//    @Mapping(target = "id", ignore = true) // Never update ID
//    @Mapping(target = "patient", ignore = true) // Associations handled separately
//    @Mapping(target = "medecin", ignore = true)
//    @Mapping(target = "salle", ignore = true)
//    @Mapping(target = "consultation", ignore = true)
    void updateEntityFromDto(RendezVousRequestDto dto, @MappingTarget RendezVous entity);
}
