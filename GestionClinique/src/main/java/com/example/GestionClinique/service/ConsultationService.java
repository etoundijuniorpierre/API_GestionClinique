package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.ConsultationDto;
import com.example.GestionClinique.dto.DossierMedicalDto;
import com.example.GestionClinique.dto.PrescriptionDto;
import com.example.GestionClinique.dto.RendezVousDto;

import java.util.List;


public interface ConsultationService {
    ConsultationDto createConsultation(ConsultationDto consultationDto);
    ConsultationDto updateConsultation(Integer id, ConsultationDto consultationDto);
    ConsultationDto findById(Integer id);
    List<ConsultationDto> findAll();
    DossierMedicalDto findDossierMedicalByConsultationId(Integer id); // Renommé pour clarté
    RendezVousDto findRendezVousByConsultationId(Integer id); // Renommé pour clarté
    void deleteById(Integer id);

    // Nouvelle méthode: Créer une consultation à partir d'un rendez-vous
    ConsultationDto startConsultation(Integer rendezVousId, ConsultationDto consultationDetails);
    // Nouvelle méthode: Ajouter une prescription à une consultation existante
    ConsultationDto addPrescriptionToConsultation(Integer consultationId, PrescriptionDto prescriptionDto);
    // Nouvelle méthode: Récupérer les prescriptions d'une consultation
    List<PrescriptionDto> findPrescriptionsByConsultationId(Integer consultationId);
}
