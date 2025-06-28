package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;

import java.util.List;


public interface PrescriptionService {
    PrescriptionRequestDto createPrescription(PrescriptionRequestDto prescriptionRequestDto);
    PrescriptionRequestDto updatePrescription(Integer id, PrescriptionRequestDto prescriptionRequestDto);
    PrescriptionRequestDto findById(Integer id);
    List<PrescriptionRequestDto> findAllPrescription();
    void deletePrescription(Integer id);
    List<PrescriptionRequestDto> findPrescriptionByMedecinId(Integer id);
    List<PrescriptionRequestDto> findPrescriptionByPatientId(Integer id);

    // Nouvelle méthode: Trouver les prescriptions par ID de consultation
    List<PrescriptionRequestDto> findPrescriptionByConsultationId(Integer consultationId);

}


