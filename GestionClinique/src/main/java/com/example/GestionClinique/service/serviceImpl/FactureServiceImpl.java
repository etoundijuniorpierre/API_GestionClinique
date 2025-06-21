package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.FactureDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.ConsultationRepository;
import com.example.GestionClinique.repository.FactureRepository;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.service.FactureService;
import com.example.GestionClinique.service.HistoriqueActionService; // Import HistoriqueActionService
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FactureServiceImpl implements FactureService {
    private final FactureRepository factureRepository;
    private final ConsultationRepository consultationRepository;
    private final HistoriqueActionService historiqueActionService; // Inject HistoriqueActionService

    @Autowired
    public FactureServiceImpl(FactureRepository factureRepository, PatientRepository patientRepository, ConsultationRepository consultationRepository, HistoriqueActionService historiqueActionService) { // Add to constructor
        this.factureRepository = factureRepository;
        this.consultationRepository = consultationRepository;
        this.historiqueActionService = historiqueActionService; // Initialize
    }

    //pas besoin de cette méthode vu que nous avons déjà createFactureForConsultation
    //    @Override
    //    public FactureDto createFacture(FactureDto factureDto) {
    //        return FactureDto.fromEntity(
    //                factureRepository
    //                        .save(FactureDto
    //                                .toEntity(factureDto))
    //        );
    //
    //    }

    @Override
    @Transactional
    public FactureDto updateFacture(Integer factureId, FactureDto factureDto) {
        Facture existingFacture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Le facture n'existe pas"));

        existingFacture.setMontant(factureDto.getMontant());
        existingFacture.setDateEmission(factureDto.getDateEmission());
        existingFacture.setStatutPaiement(factureDto.getStatutPaiement());
        existingFacture.setModePaiement(factureDto.getModePaiement());

        FactureDto updatedFactureDto = FactureDto.fromEntity(
                factureRepository.save(existingFacture)
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Facture ID " + factureId + " mise à jour."
        );
        // --- Fin de l'ajout de l'historique ---

        return updatedFactureDto;
    }

    @Override
    public List<FactureDto> findAllFactures() {
        List<FactureDto> factures = factureRepository.findAll()
                .stream()
                .map(FactureDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction("Liste de toutes les factures récupérée.");
        // --- Fin de l'ajout de l'historique ---

        return factures;
    }

    @Override
    public List<FactureDto> findFacturesByStatut(StatutPaiement statutPaiement) {

        List<FactureDto> factures = factureRepository.findFacturesByStatutPaiement(statutPaiement)
                .stream()
                .map(FactureDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction("Factures récupérées avec le statut : " + statutPaiement);
        // --- Fin de l'ajout de l'historique ---

        return factures;
    }

    @Override
    public List<FactureDto> findFacturesByModePaiement(ModePaiement modePaiement) {

        List<FactureDto> factures = factureRepository.findFacturesByModePaiement(modePaiement)
                .stream()
                .map(FactureDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction("Factures récupérées avec le mode de paiement : " + modePaiement);
        // --- Fin de l'ajout de l'historique ---

        return factures;
    }

    @Override
    public FactureDto findById(Integer FactureId) {

        FactureDto factureDto = factureRepository.findById(FactureId)
                .map(FactureDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("id : " + FactureId + " pas trouvé"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction("Facture ID " + FactureId + " récupérée.");
        // --- Fin de l'ajout de l'historique ---

        return factureDto;
    }

    @Override
    public void deleteFacture(Integer FactureId) {
        if (!factureRepository.existsById(FactureId)) {
            throw new RuntimeException("Le facture n'existe pas");
        }

        factureRepository.deleteById(FactureId);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction("Facture ID " + FactureId + " supprimée.");
        // --- Fin de l'ajout de l'historique ---
    }


    @Override
    public PatientDto findPatientByFactureId(Integer factureId) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new EntityNotFoundException("Facture avec l'id : " + factureId + " pas trouvé"));

        PatientDto patientDto = Optional.ofNullable(facture.getPatient())
                .map(PatientDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Aucun patient associé à la facture avec l'ID : " + factureId));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction("Patient récupéré pour la facture ID : " + factureId);
        // --- Fin de l'ajout de l'historique ---

        return patientDto;
    }

    @Override
    @Transactional
    public FactureDto createFactureForConsultation(Integer consultationId, FactureDto factureDto) {
        // Logique pour créer une facture pour une consultation terminée:
        // 1. Trouver la Consultation par consultationId
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation avec l'id : " + consultationId + " pas trouvé"));
        // 2. Vérifier si la consultation est terminée et non encore facturée
        if (consultation.getRendezVous() == null || consultation.getRendezVous().getStatut() != StatutRDV.CONFIRME) {
            throw new IllegalStateException("Impossible de créer la facture : le rendez-vous de la consultation n'est pas terminé.");
        }
        if (consultation.getFacture() != null) {
            throw new IllegalStateException("Impossible de créer la facture : la consultation avec l'ID " + consultationId + " a déjà une facture associée.");
        }

        // 3. Créer et Lier la Facture à la Consultation et au Patient
        Facture newFacture = new Facture();
        newFacture.setConsultation(consultation); //liaison de la facture à la consultation
        newFacture.setPatient(consultation.getDossierMedical().getPatient()); //liaison de la facture au patient en passant par le dossier médical

        // 4. Définir les valeurs par défaut ou calculées pour la nouvelle facture
        newFacture.setDateEmission(LocalDate.now()); // Date du jour
        newFacture.setStatutPaiement(StatutPaiement.NONPAYE); // Statut initial
        newFacture.setModePaiement(ModePaiement.ESPECE); // Mode de paiement par défaut

        newFacture.setMontant((float) consultation.getRendezVous().getServiceMedical().getMontant()); // Exemple de montant par défaut

        //optionnel pour assurer que la la nexFacture est bien lier à Consultation pour bien gèrer la relation entre les deux
        //        consultation.setFacture(newFacture);
        //        consultationRepository.save(consultation);

        FactureDto createdFactureDto = FactureDto.fromEntity(factureRepository.save(newFacture));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Facture créée pour la consultation ID : " + consultationId + ", Facture ID: " + createdFactureDto.getId()
        );
        // --- Fin de l'ajout de l'historique ---

        return createdFactureDto;
    }

    @Override
    public FactureDto updateStatutPaiement(Integer factureId, StatutPaiement nouveauStatut) {
        Facture existingFacture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Le facture n'existe pas"));

        existingFacture.setStatutPaiement(nouveauStatut);

        FactureDto updatedFactureDto = FactureDto.fromEntity(
                factureRepository.save(existingFacture)
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Statut de paiement de la facture ID " + factureId + " mis à jour à : " + nouveauStatut
        );
        // --- Fin de l'ajout de l'historique ---

        return updatedFactureDto;
    }
}