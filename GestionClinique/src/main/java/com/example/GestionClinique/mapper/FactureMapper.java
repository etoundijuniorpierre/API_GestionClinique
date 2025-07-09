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
        uses = {PatientMapper.class, ConsultationMapper.class}) // uses are for nested mapping if needed
public interface FactureMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "patient", ignore = true)     // Set by service during creation
    @Mapping(target = "consultation", ignore = true) // Set by service during creation
    Facture toEntity(FactureRequestDto dto);


    @Mapping(target = "patientNomComplet", expression = "java(entity.getPatient() != null ? entity.getPatient().getNom() + \" \" + entity.getPatient().getPrenom() : null)")
//    @Mapping(target = "consultationDateTime", source = "consultation.dateHeureDebut") // Keep this commented if you don't have consultationDateTime in FactureResponseDto
    @Mapping(target = "serviceMedicalNom", expression = "java(entity.getConsultation() != null && entity.getConsultation().getMedecin() != null && entity.getConsultation().getMedecin().getServiceMedical() != null ? entity.getConsultation().getMedecin().getServiceMedical().name() : null)") // <-- FIX IS HERE
    FactureResponseDto toDto(Facture entity);


    List<FactureResponseDto> toDtoList(List<Facture> entities);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    void updateEntityFromDto(FactureRequestDto dto, @MappingTarget Facture entity);
}