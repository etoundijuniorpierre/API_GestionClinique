package com.example.GestionClinique.service;

import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Prescription;
import com.example.GestionClinique.model.entity.RendezVous;

import java.util.List;


public interface ConsultationService {
    Consultation createConsultation(Consultation consultation, Long medecinId);
    Consultation updateConsultation(Long id, Consultation consultationDetails);
    Consultation findById(Long id);
    List<Consultation> findAll();
    DossierMedical findDossierMedicalByConsultationId(Long id);
    RendezVous findRendezVousByConsultationId(Long id);
    void deleteById(Long id);
    Consultation startConsultation(Long rendezVousId, Consultation consultationDetails, Long medecinId);
    Prescription addPrescriptionToConsultation(Long consultationId, Prescription prescription);
    List<Prescription> findPrescriptionsByConsultationId(Long consultationId);
}