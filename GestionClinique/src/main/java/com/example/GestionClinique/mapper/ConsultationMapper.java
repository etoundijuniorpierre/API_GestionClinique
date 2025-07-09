package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.ConsultationRequestDto;
import com.example.GestionClinique.dto.ResponseDto.ConsultationResponseDto;
import com.example.GestionClinique.model.entity.Consultation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {PrescriptionMapper.class})
public interface ConsultationMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dossierMedical", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "prescriptions", source = "prescriptions")
    @Mapping(target = "facture", ignore = true)
    Consultation toEntity(ConsultationRequestDto dto);


    // @Mapping(target = "dossierMedicalId", source = "dossierMedical.id")
    @Mapping(target = "medecinNomComplet", expression = "java(entity.getMedecin() != null ? entity.getMedecin().getNom() + \" \" + entity.getMedecin().getPrenom() : null)")
    // @Mapping(target = "rendezVousId", source = "rendezVous.id")
    @Mapping(target = "patientNomComplet", expression = "java(entity.getDossierMedical() != null && entity.getDossierMedical().getPatient() != null ? entity.getDossierMedical().getPatient().getNom() + \" \" + entity.getDossierMedical().getPatient().getPrenom() : null)")
    @Mapping(target = "serviceMedecin", expression = "java(entity.getMedecin() != null && entity.getMedecin().getServiceMedical() != null ? entity.getMedecin().getServiceMedical().name() : null)")
    @Mapping(target = "prescriptions", source = "prescriptions")
    ConsultationResponseDto toDto(Consultation entity);

    List<ConsultationResponseDto> toDtoList(List<Consultation> entities);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dossierMedical", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "prescriptions", source = "prescriptions")
    @Mapping(target = "facture", ignore = true)
    void updateEntityFromDto(ConsultationRequestDto dto, @MappingTarget Consultation entity);
}