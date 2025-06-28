package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.DossierMedicalResponseDto;
import com.example.GestionClinique.dto.ResponseDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {PatientMapper.class, ConsultationMapper.class, PrescriptionMapper.class}) // Declare other mappers used
public abstract class DossierMedicalMapper { // Make it abstract if you use @Autowired within

    @Autowired
    protected PatientMapper patientMapper; // Inject patientMapper
    @Autowired
    protected ConsultationMapper consultationMapper; // Inject consultationMapper
    @Autowired
    protected PrescriptionMapper prescriptionMapper; // Inject prescriptionMapper

    // Mapping from Request DTO to Entity for creation/update
    // 'id' is ignored because it's generated
    // 'patient', 'consultations', 'prescriptions' are ignored because the service will fetch/manage them
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "patient", ignore = true)
//    @Mapping(target = "consultations", ignore = true)
//    @Mapping(target = "prescriptions", ignore = true)
    public abstract DossierMedical toEntity(DossierMedicalRequestDto dto);

    // Mapping from Entity to Response DTO
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientToResponseDto")
    @Mapping(target = "consultations", expression = "java(consultationMapper.toDtoList(entity.getConsultations()))")
    @Mapping(target = "prescriptions", expression = "java(prescriptionMapper.toDtoList(entity.getPrescriptions()))")
    public abstract DossierMedicalResponseDto toDto(DossierMedical entity);


    public PatientResponseDto patientToResponseDto(Patient patient) {
        return patientMapper.toDto(patient);
    }

    public abstract List<DossierMedicalResponseDto> toDtoList(List<DossierMedical> entities);

    // Update existing entity from DTO
//    @Mapping(target = "id", ignore = true) // Never update ID
//    @Mapping(target = "patient", ignore = true) // Patient linkage usually handled separately for updates
//    @Mapping(target = "consultations", ignore = true) // Collections usually managed by separate add/remove operations
//    @Mapping(target = "prescriptions", ignore = true)
    public abstract void updateEntityFromDto(DossierMedicalRequestDto dto, @MappingTarget DossierMedical entity);
}