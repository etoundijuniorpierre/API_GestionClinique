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
public class HistoriqueActionServiceImpl implements HistoriqueActionService {

    // --- Add a logger for internal used---
    private static final Logger logger = LoggerFactory.getLogger(HistoriqueActionServiceImpl.class);
    // ---------------------------------------------------

    private final HistoriqueActionRepository historiqueActionRepository;
    private final AuthenticationFacade authenticationFacade;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;

    // Constructor injection for all dependencies
    public HistoriqueActionServiceImpl(HistoriqueActionRepository historiqueActionRepository, AuthenticationFacade authenticationFacade,
                                       UtilisateurRepository utilisateurRepository,@Lazy UtilisateurService utilisateurService) {
        this.historiqueActionRepository = historiqueActionRepository;
        this.authenticationFacade = authenticationFacade;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
    }

    @Override
    @Transactional
    public void enregistrerAction(String actionDescription) {
        String username = "Inconnu";
        Utilisateur currentUser = null;

        if (authenticationFacade.getAuthentication() != null &&
                authenticationFacade.getAuthentication().getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authenticationFacade.getAuthentication().getPrincipal();
            username = userDetails.getUsername();
            currentUser = utilisateurRepository.findUtilisateurByInfoPersonnel_Email(username).orElse(null);
        }

        HistoriqueAction action = new HistoriqueAction();
        action.setUtilisateur(currentUser);
        action.setDate(LocalDate.now());
        action.setAction(actionDescription + " (par " + (currentUser != null ? currentUser.getInfoPersonnel().getEmail() : username) + ")");
        historiqueActionRepository.save(action);
        logger.info("Action historique enregistrée: {}", actionDescription); // Log the fact that an action was recorded
    }

    @Override
    @Transactional
    public void enregistrerAction(String actionDescription, Utilisateur utilisateur) {
        HistoriqueAction action = new HistoriqueAction();
        action.setUtilisateur(utilisateur);
        action.setDate(LocalDate.now());
        action.setAction(actionDescription + " (par " + (utilisateur != null ? utilisateur.getInfoPersonnel().getEmail() : "Inconnu") + ")");
        historiqueActionRepository.save(action);
        logger.info("Action historique enregistrée (par utilisateur spécifié): {}", actionDescription); // Log the fact that an action was recorded
    }

    @Override
    @Transactional
    public List<HistoriqueActionRequestDto> findAll() {
        logger.debug("Récupération de tous les historiques d'action."); // Internal log
        return historiqueActionRepository
                .findAll()
                .stream()
                .map(HistoriqueActionRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HistoriqueActionRequestDto findById(Integer historiqueId) {
        logger.debug("Recherche de l'historique par ID: {}", historiqueId); // Internal log
        return historiqueActionRepository.findById(historiqueId)
                .map(HistoriqueActionRequestDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("L'historique avec l'id "+historiqueId+" n'existe pas"));
    }

    @Override
    @Transactional
    public List<HistoriqueActionRequestDto> findHistoriqueByUtilisateurId(Integer utilisateurId) {
        logger.debug("Recherche de l'historique pour l'utilisateur ID: {}", utilisateurId); // Internal log
        Utilisateur existingUtilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("L'utilisateur avec l'id "+utilisateurId+" n'existe pas"));

        if (existingUtilisateur.getHistoriqueActions() == null) {
            return List.of();
        }

        return existingUtilisateur.getHistoriqueActions()
                .stream()
                .filter(Objects::nonNull)
                .map(HistoriqueActionRequestDto::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<HistoriqueActionRequestDto> findHistoriqueByUtilisateurName(String utilisateurName) {
        Collection<Utilisateur> usersByNom = utilisateurRepository.findUtilisateurByInfoPersonnel_Nom(utilisateurName);

        if (usersByNom == null || usersByNom.isEmpty()) {
            return new ArrayList<>();
        }


        return usersByNom.stream()
                .filter(Objects::nonNull)
                .flatMap(user -> {
                    if (user.getHistoriqueActions() == null) {
                        return java.util.stream.Stream.empty();
                    }
                    return user.getHistoriqueActions().stream();
                })
                .filter(Objects::nonNull)
                .map(HistoriqueActionRequestDto::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<HistoriqueActionRequestDto> findByDateAfterAndDateBefore(LocalDate startDate, LocalDate endDate) {
        logger.debug("Recherche de l'historique entre {} et {}", startDate, endDate); // Internal log
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate et endDate ne peuvent être null"); // Corrected message
        }

        if(startDate.isAfter(endDate)) { // Simplified condition
            throw new IllegalArgumentException("La date de début ne peut pas être après la date de fin."); // Corrected message and exception type
        }

        return historiqueActionRepository.findByDateAfterAndDateBefore(startDate, endDate)
                .stream()
                .filter(Objects::nonNull)
                .map(HistoriqueActionRequestDto::fromEntity)
                .collect(Collectors.toList());
    }
}