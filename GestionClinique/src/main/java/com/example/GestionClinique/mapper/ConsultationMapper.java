package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.ConsultationRequestDto;
import com.example.GestionClinique.dto.ResponseDto.ConsultationResponseDto;
import com.example.GestionClinique.model.entity.Consultation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsultationMapper {


    @Autowired
    PrescriptionMapper prescriptionMapper = null;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dossierMedical", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    @Mapping(target = "facture", ignore = true)
    Consultation toEntity(ConsultationRequestDto dto);

    @Mapping(target = "dossierMedicalId", source = "dossierMedical.id")
    @Mapping(target = "medecinId", source = "medecin.id")
    @Mapping(target = "rendezVousId", source = "rendezVous.id")
    @Mapping(target = "factureId", source = "facture.id")
    @Mapping(target = "prescriptions", expression = "java(consultation.getPrescriptions() != null ? prescriptionMapper.toDtoList(consultation.getPrescriptions()) : null)")
    ConsultationResponseDto toDto(Consultation consultation);

    List<ConsultationResponseDto> toDtoList(List<Consultation> consultations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dossierMedical", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    @Mapping(target = "facture", ignore = true)
    void updateEntityFromDto(ConsultationRequestDto dto, @MappingTarget Consultation entity);

}
