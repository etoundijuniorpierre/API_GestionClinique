package com.example.GestionClinique.mapper; // Consistent package name

import com.example.GestionClinique.dto.RequestDto.HistoriqueActionRequestDto;
import com.example.GestionClinique.dto.ResponseDto.HistoriqueActionResponseDto;


import com.example.GestionClinique.model.entity.HistoriqueAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UtilisateurMapper.class})
public interface HistoriqueActionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilisateur", ignore = true) // Géré manuellement dans le service
    HistoriqueAction toEntity(HistoriqueActionRequestDto dto);

    @Mapping(target = "utilisateur", source = "utilisateur")
    HistoriqueActionResponseDto toDto(HistoriqueAction entity);

    List<HistoriqueActionResponseDto> toDtoList(List<HistoriqueAction> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    void updateEntityFromDto(HistoriqueActionRequestDto dto, @MappingTarget HistoriqueAction entity);
}