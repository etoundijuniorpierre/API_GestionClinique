package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.FactureRequestDto;
import com.example.GestionClinique.dto.ResponseDto.FactureResponseDto;
import com.example.GestionClinique.model.entity.Facture;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;


import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {PatientMapper.class, ConsultationMapper.class})
public interface FactureMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    Facture toEntity(FactureRequestDto dto);

    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "consultation", source = "consultation")
    FactureResponseDto toDto(Facture entity);

    List<FactureResponseDto> toDtoList(List<Facture> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    void updateEntityFromDto(FactureRequestDto dto, @MappingTarget Facture entity);
}