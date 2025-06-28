package com.example.GestionClinique.service;



import com.example.GestionClinique.model.entity.Prescription;

import java.util.List;


public interface PrescriptionService {
    Prescription createPrescription(Prescription Prescription);
    Prescription updatePrescription(Integer id, Prescription Prescription);
    Prescription findById(Integer id);
    List<Prescription> findAllPrescription();
    void deletePrescription(Integer id);
    List<Prescription> findPrescriptionByMedecinId(Integer id);
    List<Prescription> findPrescriptionByPatientId(Integer id);

    List<Prescription> findPrescriptionByConsultationId(Integer consultationId);

}


