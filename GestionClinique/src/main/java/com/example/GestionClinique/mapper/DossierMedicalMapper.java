package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.DossierMedicalResponseDto;
import com.example.GestionClinique.dto.ResponseDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Patient;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {PatientMapper.class, ConsultationMapper.class, PrescriptionMapper.class})
public interface DossierMedicalMapper {

    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "consultations", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    DossierMedical toEntity(DossierMedicalRequestDto dto);

    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "consultations", source = "consultations")
    @Mapping(target = "prescriptions", source = "prescriptions")
    DossierMedicalResponseDto toDto(DossierMedical entity);

    List<DossierMedicalResponseDto> toDtoList(List<DossierMedical> entities);

    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "consultations", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    void updateEntityFromDto(DossierMedicalRequestDto dto, @MappingTarget DossierMedical entity);
}