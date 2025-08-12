package com.example.GestionClinique.service;



import com.example.GestionClinique.model.entity.Prescription;

import java.util.List;


public interface PrescriptionService {

    Prescription addPrescription(Long consultationId, Prescription prescription); // Takes a full Prescription entity


    Prescription updatePrescription(Long id, Prescription prescriptionDetails); // Takes ID and entity

    Prescription findById(Long id);

    List<Prescription> findAllPrescription();

    void deletePrescription(Long id);

    List<Prescription> findPrescriptionByMedecinId(Long medecinId);

    List<Prescription> findPrescriptionByPatientId(Long patientId);

    List<Prescription> findPrescriptionByConsultationId(Long consultationId);
}


