package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.dto.ResponseDto.RendezVousResponseDto;
import com.example.GestionClinique.model.entity.RendezVous;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RendezVousMapper {

    @Mapping(target = "patient", ignore = true) // Géré par le service
    @Mapping(target = "medecin", ignore = true) // Géré par le service
    @Mapping(target = "salle", ignore = true) // Géré par le service
    @Mapping(target = "consultation", ignore = true)
    RendezVous toEntity(RendezVousRequestDto dto);

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(source = "salle.id", target = "salleId")
    @Mapping(source = "consultation.id", target = "consultationId")
    @Mapping(target = "patientNomComplet", expression = "java(entity.getPatient() != null ? entity.getPatient().getNom() + \" \" + entity.getPatient().getPrenom() : null)")
    @Mapping(target = "medecinNomComplet", expression = "java(entity.getMedecin() != null ? entity.getMedecin().getNom() + \" \" + entity.getMedecin().getPrenom() : null)")
    @Mapping(source = "salle.numeroSalle", target = "nomSalle")
    RendezVousResponseDto toDto(RendezVous entity);

    List<RendezVousResponseDto> toDtoList(List<RendezVous> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    @Mapping(target = "salle", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    void updateEntityFromDto(RendezVousRequestDto dto, @MappingTarget RendezVous entity);
}
