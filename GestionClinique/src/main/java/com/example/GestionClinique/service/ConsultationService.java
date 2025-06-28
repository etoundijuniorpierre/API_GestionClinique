package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.ConsultationRequestDto;
import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;

import java.util.List;


public interface ConsultationService {
    ConsultationRequestDto createConsultation(ConsultationRequestDto consultationRequestDto);
    ConsultationRequestDto updateConsultation(Integer id, ConsultationRequestDto consultationRequestDto);
    ConsultationRequestDto findById(Integer id);
    List<ConsultationRequestDto> findAll();
    DossierMedicalRequestDto findDossierMedicalByConsultationId(Integer id); // Renommé pour clarté
    RendezVousRequestDto findRendezVousByConsultationId(Integer id); // Renommé pour clarté
    void deleteById(Integer id);

    // Nouvelle méthode: Créer une consultation à partir d'un rendez-vous
    ConsultationRequestDto startConsultation(Integer rendezVousId, ConsultationRequestDto consultationDetails);
    // Nouvelle méthode: Ajouter une prescription à une consultation existante
    ConsultationRequestDto addPrescriptionToConsultation(Integer consultationId, PrescriptionRequestDto prescriptionRequestDto);
    // Nouvelle méthode: Récupérer les prescriptions d'une consultation
    List<PrescriptionRequestDto> findPrescriptionsByConsultationId(Integer consultationId);
}
