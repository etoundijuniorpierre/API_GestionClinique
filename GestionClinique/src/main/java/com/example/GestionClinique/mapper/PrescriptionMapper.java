package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PrescriptionResponseDto;
import com.example.GestionClinique.model.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrescriptionMapper {

    // Map PrescriptionRequestDto to Prescription entity
//    @Mapping(target = "id", ignore = true) // ID is auto-generated
//    @Mapping(target = "consultation", ignore = true) // Will be set manually in service or controller
//    @Mapping(target = "medecin", ignore = true) // Will be set manually
//    @Mapping(target = "patient", ignore = true) // Will be set manually
//    @Mapping(target = "dossierMedical", ignore = true) // Will be set manually
    Prescription toEntity(PrescriptionRequestDto dto);

    // Map Prescription entity to PrescriptionResponseDto
    @Mapping(source = "consultation.id", target = "consultationId")
    @Mapping(source = "consultation.motif", target = "consultationDescription") // Assuming Consultation has getMotif()
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(target = "medecinNomComplet", expression = "java(entity.getMedecin().getNom() + \" \" + entity.getMedecin().getPrenom())")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(target = "patientNomComplet", expression = "java(entity.getPatient().getNom() + \" \" + entity.getPatient().getPrenom())")
    @Mapping(source = "dossierMedical.id", target = "dossierMedicalId")
    @Mapping(source = "dossierMedical.reference", target = "dossierMedicalReference") // Assuming DossierMedical has getReference()
    PrescriptionResponseDto toDto(Prescription entity);

    List<PrescriptionResponseDto> toDtoList(List<Prescription> entities);

    // Update existing Prescription entity from DTO
//    @Mapping(target = "id", ignore = true) // Never update ID
//    @Mapping(target = "consultation", ignore = true) // Associations handled separately
//    @Mapping(target = "medecin", ignore = true)
//    @Mapping(target = "patient", ignore = true)
//    @Mapping(target = "dossierMedical", ignore = true)
    void updateEntityFromDto(PrescriptionRequestDto dto, @MappingTarget Prescription entity);
}