package com.example.GestionClinique.mapper;


import com.example.GestionClinique.dto.ResponseDto.stats.StatDuJourResponseDto;
import com.example.GestionClinique.dto.ResponseDto.stats.StatMoisDernierResponseDto;
import com.example.GestionClinique.dto.ResponseDto.stats.StatMoisEncoursResponseDto;
import com.example.GestionClinique.dto.ResponseDto.stats.StatsSurLanneeResponseDto;
import com.example.GestionClinique.model.entity.stats.StatDuJour;
import com.example.GestionClinique.model.entity.stats.StatMoisDernier;
import com.example.GestionClinique.model.entity.stats.StatMoisEncours;
import com.example.GestionClinique.model.entity.stats.StatsSurLannee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatMapper {

    StatDuJourResponseDto toStatDuJourDto(StatDuJour entity);
    StatMoisDernierResponseDto toStatMoisDernierDto(StatMoisDernier entity);
    StatMoisEncoursResponseDto toStatMoisEncoursDto(StatMoisEncours entity);

    @Mapping(source = "annee", target = "annee")
    StatsSurLanneeResponseDto toStatsSurLanneeDto(StatsSurLannee entity);
}