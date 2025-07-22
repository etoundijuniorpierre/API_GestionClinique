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
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    Facture toEntity(FactureRequestDto dto);



    @Mapping(target = "patientNomComplet",
            expression = "java(entity.getPatient() != null ? entity.getPatient().getNom() + \" \" + entity.getPatient().getPrenom() : null)")
    @Mapping(target = "serviceMedicalNom",
            expression = "java(getServiceMedicalName(entity))")
    FactureResponseDto toDto(Facture entity);



    List<FactureResponseDto> toDtoList(List<Facture> entities);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "rendezVous", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    void updateEntityFromDto(FactureRequestDto dto, @MappingTarget Facture entity);


    default String getServiceMedicalName(Facture facture) {
        // Priorité à la consultation si elle existe
        if (facture.getConsultation() != null
                && facture.getConsultation().getMedecin() != null
                && facture.getConsultation().getMedecin().getServiceMedical() != null) {
            return facture.getConsultation().getMedecin().getServiceMedical().name();
        }

        // Sinon, vérifier le rendez-vous
        if (facture.getRendezVous() != null
                && facture.getRendezVous().getServiceMedical() != null) {
            return facture.getRendezVous().getServiceMedical().name();
        }

        return null;
    }
}