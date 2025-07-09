package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring") // Or whatever component model you use
public interface PatientMapper {


        // Mapping explicite pour éviter les surprises
        @Mapping(target = "nom", source = "nom")
        @Mapping(target = "prenom", source = "prenom")
        @Mapping(target = "email", source = "email")
        @Mapping(target = "dateNaissance", source = "dateNaissance")
        @Mapping(target = "telephone", source = "telephone")
        @Mapping(target = "adresse", source = "adresse")
        @Mapping(target = "genre", source = "genre")
        Patient toEntity(PatientRequestDto patientRequestDto);

        PatientResponseDto toDto(Patient patient);

        List<PatientResponseDto> toDtoList(List<Patient> patients);

        void updateEntityFromDto(PatientRequestDto patientRequestDto, @MappingTarget Patient patient);
    }

