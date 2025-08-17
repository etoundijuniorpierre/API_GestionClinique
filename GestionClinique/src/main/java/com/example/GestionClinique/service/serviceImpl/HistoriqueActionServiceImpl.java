package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.HistoriqueAction;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.HistoriqueActionRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class
HistoriqueActionServiceImpl implements HistoriqueActionService {

    private final HistoriqueActionRepository historiqueActionRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public HistoriqueActionServiceImpl(HistoriqueActionRepository historiqueActionRepository,
                                       UtilisateurRepository utilisateurRepository) {
        this.historiqueActionRepository = historiqueActionRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public HistoriqueAction enregistrerAction(String actionDescription, Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec ID: " + utilisateurId));

        HistoriqueAction action = new HistoriqueAction();
        action.setDate(LocalDate.now());
        action.setAction(actionDescription);
        action.setUtilisateur(utilisateur);
        return historiqueActionRepository.save(action);
    }

    @Override
    public HistoriqueAction enregistrerAction(String actionDescription, Utilisateur utilisateur) {
        HistoriqueAction action = new HistoriqueAction(); // Create new instance
        action.setDate(LocalDate.now());                  // Set date
        action.setAction(actionDescription);              // Set action description
        action.setUtilisateur(utilisateur);               // Set associated user
        return historiqueActionRepository.save(action);
    }

    @Override
    public HistoriqueAction enregistrerAction(String actionDescription) {
        HistoriqueAction action = new HistoriqueAction(); // Create new instance
        action.setDate(LocalDate.now());                  // Set date
        action.setAction(actionDescription);              // Set action description
        action.setUtilisateur(null);                      // No specific user associated
        return historiqueActionRepository.save(action);
    }

    @Override// Use Spring's Transactional
    public List<HistoriqueAction> findAllHistoriqueActionsDesc() {
        return historiqueActionRepository.findAllByOrderByIdDesc();
    }

    @Override// Use Spring's Transactional
    public HistoriqueAction findHistoriqueActionById(Long id) {
        return historiqueActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("HistoriqueAction non trouvé avec ID: " + id));
    }

    @Override// Use Spring's Transactional
    public List<HistoriqueAction> findHistoriqueActionsByUtilisateurId(Long utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec ID: " + utilisateurId);
        }
        return historiqueActionRepository.findByUtilisateurId(utilisateurId);
    }

    @Override// Use Spring's Transactional
    public List<HistoriqueAction> findHistoriqueActionsByUtilisateurName(String utilisateurName) {
        return historiqueActionRepository.findByNomCompletUtilisateur(utilisateurName);
    }

    @Override
    public List<HistoriqueAction> findHistoriqueActionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return historiqueActionRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<HistoriqueAction> rechercherHistorique(String nom, String prenom, String email, String motCle) {
        return historiqueActionRepository.searchHistoriqueActions(
                (nom != null && !nom.isEmpty()) ? nom : null,
                (prenom != null && !prenom.isEmpty()) ? prenom : null,
                (email != null && !email.isEmpty()) ? email : null,
                (motCle != null && !motCle.isEmpty()) ? motCle : null
        );
    }

}