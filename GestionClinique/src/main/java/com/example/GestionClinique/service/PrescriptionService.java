package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.PrescriptionDto;

import java.util.List;


public interface PrescriptionService {
    PrescriptionDto createPrescription(PrescriptionDto prescriptionDto);
    PrescriptionDto updatePrescription(Integer id, PrescriptionDto prescriptionDto);
    PrescriptionDto findById(Integer id);
    List<PrescriptionDto> findAllPrescription();
    void deletePrescription(Integer id);
    List<PrescriptionDto> findPrescriptionByMedecinId(Integer id);
    List<PrescriptionDto> findPrescriptionByPatientId(Integer id);

    // Nouvelle méthode: Trouver les prescriptions par ID de consultation
    List<PrescriptionDto> findPrescriptionByConsultationId(Integer consultationId);

}


