package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.component.AuthenticationFacade;
import com.example.GestionClinique.dto.RequestDto.HistoriqueActionRequestDto;
import com.example.GestionClinique.model.entity.HistoriqueAction;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.HistoriqueActionRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.UtilisateurService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class HistoriqueActionServiceImpl implements HistoriqueActionService {

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

        HistoriqueAction action = HistoriqueAction.builder()
                .date(LocalDate.now())
                .action(actionDescription)
                .utilisateur(utilisateur)
                .build();
        return historiqueActionRepository.save(action);
    }

    @Override
    public HistoriqueAction enregistrerAction(String actionDescription, Utilisateur utilisateur) {
        // This method assumes the Utilisateur object passed is already managed or fetched
        // and is primarily for internal service-to-service logging.
        HistoriqueAction action = HistoriqueAction.builder()
                .date(LocalDate.now())
                .action(actionDescription)
                .utilisateur(utilisateur)
                .build();
        return historiqueActionRepository.save(action);
    }

    @Override
    public HistoriqueAction enregistrerAction(String actionDescription) {
        // For actions not tied to a specific user (e.g., system startup, global errors)
        // Or if you want to get the currently authenticated user here:
        // You'd typically use SecurityContextHolder.getContext().getAuthentication()
        // and extract user details if available. For now, it will be null for user.
        HistoriqueAction action = HistoriqueAction.builder()
                .date(LocalDate.now())
                .action(actionDescription)
                .utilisateur(null) // Or a specific 'System' user
                .build();
        return historiqueActionRepository.save(action);
    }

    @Override
    @Transactional
    public List<HistoriqueAction> findAllHistoriqueActions() {
        return historiqueActionRepository.findAll();
    }

    @Override
    @Transactional
    public HistoriqueAction findHistoriqueActionById(Long id) {
        return historiqueActionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("HistoriqueAction non trouvé avec ID: " + id));
    }

    @Override
    @Transactional
    public List<HistoriqueAction> findHistoriqueActionsByUtilisateurId(Long utilisateurId) {
        // Optional: Check if utilisateur exists first for a clearer 404 vs 204
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec ID: " + utilisateurId);
        }
        return historiqueActionRepository.findByUtilisateurId(utilisateurId);
    }

    @Override
    @Transactional
    public List<HistoriqueAction> findHistoriqueActionsByUtilisateurName(String utilisateurName) {
        // You might want to check if any user exists with that name before returning empty list
        // This depends on whether you want a 404 for non-existent user name or 204 for no actions
        return historiqueActionRepository.findByUtilisateurNomCompletContainingIgnoreCase(utilisateurName);
    }

    @Override
    @Transactional
    public List<HistoriqueAction> findHistoriqueActionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return historiqueActionRepository.findByDateBetween(startDate, endDate);
    }
}