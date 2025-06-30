package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {DossierMedicalMapper.class})
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dossierMedical", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "factures", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    Patient toEntity(PatientRequestDto dto);

    // Simple mapping without non-existent fields
    PatientResponseDto toDto(Patient entity);

    List<PatientResponseDto> toDtoList(List<Patient> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dossierMedical", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "factures", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    void updateEntityFromDto(PatientRequestDto dto, @MappingTarget Patient entity);
}