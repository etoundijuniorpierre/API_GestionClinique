package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.ResponseDto.DossierMedicalResponseDto;
import com.example.GestionClinique.model.entity.DossierMedical;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DossierMedicalMapper {


    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "consultations", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    DossierMedical toEntity(DossierMedicalRequestDto dto);


    @Mapping(target = "patientNomComplet", expression = "java(entity.getPatient() != null ? entity.getPatient().getNom() + \" \" + entity.getPatient().getPrenom() : null)")
    @Mapping(target = "patientTelephone", source = "patient.telephone")
    @Mapping(target = "patientDateNaissance", source = "patient.dateNaissance")
    @Mapping(target = "patientGenre", source = "patient.genre")
    DossierMedicalResponseDto toDto(DossierMedical entity);


//    @Mapping(target = "patient", ignore = true)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "creationDate", ignore = true)
//    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateEntityFromDto(DossierMedicalRequestDto dto, @MappingTarget DossierMedical entity);

    List<DossierMedicalResponseDto> toDtoList(List<DossierMedical> entities);

}