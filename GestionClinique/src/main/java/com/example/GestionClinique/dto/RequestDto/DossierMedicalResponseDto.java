package com.example.GestionClinique.dto.RequestDto;

import com.example.GestionClinique.dto.ResponseDto.BaseResponseDto;
import com.example.GestionClinique.dto.ResponseDto.ConsultationResponseDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.dto.ResponseDto.PrescriptionResponseDto;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DossierMedicalResponseDto extends BaseResponseDto {
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String traitementsEnCours;
    private String observations;
    private PatientResponseDto patient;
    private List<ConsultationResponseDto> consultations;
    private List<PrescriptionResponseDto> prescriptions;
}