package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.ResponseDto.DossierMedicalResponseDto;
import com.example.GestionClinique.model.entity.DossierMedical;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DossierMedicalMapper {


    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "consultations", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    DossierMedical toEntity(DossierMedicalRequestDto dto);

    @Mapping(source = "patient", target = "patient")
    DossierMedicalResponseDto toDto(DossierMedical entity);


    void updateEntityFromDto(DossierMedicalRequestDto dto, @MappingTarget DossierMedical entity);


    List<DossierMedicalResponseDto> toDtoList(List<DossierMedical> entities);

}