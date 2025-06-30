package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PrescriptionResponseDto;
import com.example.GestionClinique.model.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrescriptionMapper {


    Prescription toEntity(PrescriptionRequestDto dto);

    @Mapping(source = "consultation.id", target = "consultationId")
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(target = "medecinNomComplet", expression = "java(entity.getMedecin() != null ? entity.getMedecin().getNom() + \" \" + entity.getMedecin().getPrenom() : null)")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(target = "patientNomComplet", expression = "java(entity.getPatient() != null ? entity.getPatient().getNom() + \" \" + entity.getPatient().getPrenom() : null)")
    @Mapping(source = "dossierMedical.id", target = "dossierMedicalId")
    PrescriptionResponseDto toDto(Prescription entity);

    List<PrescriptionResponseDto> toDtoList(List<Prescription> entities);


    void updateEntityFromDto(PrescriptionRequestDto dto, @MappingTarget Prescription entity);
}