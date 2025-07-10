package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.SalleRequestDto;
import com.example.GestionClinique.dto.ResponseDto.SalleResponseDto;
import com.example.GestionClinique.model.entity.Salle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SalleMapper {

    @Mapping(target = "rendezVous", ignore = true)
    Salle toEntity(SalleRequestDto dto);

    @Mapping(target = "rendezVousIds",
            expression = "java(entity.getRendezVous() != null ? entity.getRendezVous().stream().map(com.example.GestionClinique.model.entity.RendezVous::getId).collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())")
    SalleResponseDto toDto(Salle entity);

    List<SalleResponseDto> toDtoList(List<Salle> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    void updateEntityFromDto(SalleRequestDto dto, @MappingTarget Salle entity);
}
