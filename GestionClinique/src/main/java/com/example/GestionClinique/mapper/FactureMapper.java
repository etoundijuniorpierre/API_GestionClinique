package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.FactureRequestDto;
import com.example.GestionClinique.dto.ResponseDto.FactureResponseDto;
import com.example.GestionClinique.model.entity.Facture;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {PatientMapper.class, ConsultationMapper.class})
public abstract class FactureMapper {

    @Autowired
    protected PatientMapper patientMapper;
    @Autowired
    protected ConsultationMapper consultationMapper;

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "patient", ignore = true)
//    @Mapping(target = "consultation", ignore = true)
    public abstract Facture toEntity(FactureRequestDto dto);

    // Convert Entity to Response DTO
    @Mapping(target = "patient", expression = "java(patientMapper.toDto(entity.getPatient()))")
    @Mapping(target = "consultation", expression = "java(consultationMapper.toDto(entity.getConsultation()))")
    public abstract FactureResponseDto toDto(Facture entity);

    public abstract List<FactureResponseDto> toDtoList(List<Facture> entities);

    // Update existing entity from DTO
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "patient", ignore = true)
//    @Mapping(target = "consultation", ignore = true)
    public abstract void updateEntityFromDto(FactureRequestDto dto, @MappingTarget Facture entity);
}