package com.example.GestionClinique.mapper;


import com.example.GestionClinique.dto.ResponseDto.stats.*;
import com.example.GestionClinique.model.entity.stats.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatMapper {

    StatDuJourResponseDto toStatDuJourDto(StatDuJour entity);
    StatParMoisResponseDto toStatParMoisDto(StatsMois entity);

    @Mapping(source = "annee", target = "annee")
    StatsSurLanneeResponseDto toStatsSurLanneeDto(StatsSurLannee entity);
}