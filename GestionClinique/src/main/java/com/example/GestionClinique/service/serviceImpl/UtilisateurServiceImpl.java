package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.InfoPersonnelDto;
import com.example.GestionClinique.dto.RoleDto;
import com.example.GestionClinique.dto.UtilisateurDto;
import com.example.GestionClinique.model.entity.InfoPersonnel;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.repository.RoleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService; // Import HistoriqueActionService
import com.example.GestionClinique.service.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UtilisateurServiceImpl implements UtilisateurService, UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final HistoriqueActionService historiqueActionService; // Inject HistoriqueActionService

    @Autowired
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder,@Lazy HistoriqueActionService historiqueActionService) { // Add to constructor
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.historiqueActionService = historiqueActionService; // Initialize
    }

// (login method commented out in original code, so not adding history for it here)



    @Override
    @Transactional
    public UtilisateurDto createUtilisateur(UtilisateurDto utilisateurDto) {
        if (utilisateurDto.getInfoPersonnel() == null || utilisateurDto.getInfoPersonnel().getEmail() == null || utilisateurDto.getInfoPersonnel().getNom() == null || utilisateurDto.getInfoPersonnel().getPrenom() == null) {
            throw new IllegalArgumentException("Les informations personnelles (nom, prenom, email) de l'utilisateur sont obligatoires.");
        }

        if (utilisateurRepository.findUtilisateurByInfoPersonnel_Email(utilisateurDto.getInfoPersonnel().getEmail()).isPresent()) {
            throw new IllegalArgumentException("l'utilisateur avec l'email '" + utilisateurDto.getInfoPersonnel().getEmail() + "' existe déjà.");
        }

        if (utilisateurDto.getMotDePasse() == null || utilisateurDto.getMotDePasse().isEmpty()) {
            throw new IllegalArgumentException("l'utilisateur doit absolument avoir un mot de passe ");
        }

        boolean isMedecin = utilisateurDto.getRoles() != null &&
                utilisateurDto.getRoles().stream()
                        .anyMatch(roleDto -> roleDto.getType() == RoleType.MEDECIN);

        if (isMedecin && utilisateurDto.getServiceMedical() == null) {
            throw new IllegalArgumentException("Un médecin doit être lié à un service médical.");
        }

        if (utilisateurDto.getMotDePasse().length() < 8) {
            throw new RuntimeException("le mot de passe doit avoir minimun 8 characters.");
        }

        Utilisateur utilisateurToSave = new Utilisateur();
        utilisateurToSave.setInfoPersonnel(InfoPersonnelDto.toEntity(utilisateurDto.getInfoPersonnel()));
        utilisateurToSave.setActif(utilisateurDto.getActif() != null ? utilisateurDto.getActif() : true); // Default to active if not provided
        utilisateurToSave.setServiceMedical(utilisateurDto.getServiceMedical());
        utilisateurToSave.setMotDePasse(passwordEncoder.encode(utilisateurDto.getMotDePasse()));

        if (utilisateurDto.getMotDePasse().length() < 8) {
            throw new RuntimeException("le mot de passe doit avoir minimun 8 characters.");
        }

        List<Role> roles = new ArrayList<>();
        if (utilisateurDto.getRoles() != null) {
            for (RoleDto roleDto : utilisateurDto.getRoles()) {
                Role role = roleRepository.findFirstByRoleType(roleDto.getType()).orElseThrow(() -> new EntityNotFoundException("Rôle introuvable: " + roleDto.getType()));
                roles.add(role);
            }
        }
        utilisateurToSave.setRole(roles);


        UtilisateurDto savedUtilisateur = UtilisateurDto.fromEntity(
                utilisateurRepository.save(
                        utilisateurToSave
                )
        );

        // --- Ajout de l'historique ---
        String rolesString = savedUtilisateur.getRoles().stream()
                .map(r -> r.getType().name())
                .collect(Collectors.joining(", "));
        historiqueActionService.enregistrerAction(
                "Création de l'utilisateur ID: " + savedUtilisateur.getId() +
                        ", Nom: " + savedUtilisateur.getInfoPersonnel().getNom() + " " + savedUtilisateur.getInfoPersonnel().getPrenom() +
                        ", Rôles: [" + rolesString + "]"
        );
        // --- Fin de l'ajout de l'historique ---

        return savedUtilisateur;
    }



    @Override
    @Transactional
    public UtilisateurDto findUtilisateurById(Integer id) {
        UtilisateurDto foundUtilisateur = utilisateurRepository.findById(id)
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("utilisateur n'existe pas"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche de l'utilisateur ID: " + id + ", Nom: " + foundUtilisateur.getInfoPersonnel().getNom()
        );
        // --- Fin de l'ajout de l'historique ---

        return foundUtilisateur;
    }



    @Override
    public List<UtilisateurDto> findUtilisateurByInfoPersonnel_Nom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide pour la recherche.");
        }

        List<UtilisateurDto> usersByNom = utilisateurRepository.findUtilisateurByInfoPersonnel_Nom(nom)
                .stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche d'utilisateurs par nom: '" + nom + "' (nombre de résultats: " + usersByNom.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return usersByNom;
    }



    @Override
    @Transactional
    public List<UtilisateurDto> findAllUtilisateur() {
        List<UtilisateurDto> allUsers = utilisateurRepository.findAll()
                .stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Affichage de tous les utilisateurs."
        );
        // --- Fin de l'ajout de l'historique ---

        return allUsers;
    }



    @Override
    @Transactional
    public UtilisateurDto updateUtilisateur(Integer utilisateurId, UtilisateurDto utilisateurDto) {

        Utilisateur existingUtilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("l'utilisateur avec l'ID "+utilisateurId+" n'existe pas"));

        // Store old values for logging
        String oldNom = existingUtilisateur.getInfoPersonnel() != null ? existingUtilisateur.getInfoPersonnel().getNom() : null;
        String oldPrenom = existingUtilisateur.getInfoPersonnel() != null ? existingUtilisateur.getInfoPersonnel().getPrenom() : null;
        String oldEmail = existingUtilisateur.getInfoPersonnel() != null ? existingUtilisateur.getInfoPersonnel().getEmail() : null;
        Boolean oldActif = existingUtilisateur.getActif();
        String oldServiceMedical = String.valueOf(existingUtilisateur.getServiceMedical());
        String oldRoles = existingUtilisateur.getRole().stream().map(r -> r.getRoleType().name()).collect(Collectors.joining(", "));


        if (existingUtilisateur.getInfoPersonnel() == null) {
            existingUtilisateur.setInfoPersonnel(new InfoPersonnel());
        }

        // mis à jour des informations personnelles des utilisateurs
        if (utilisateurDto.getInfoPersonnel() != null) {
            InfoPersonnelDto dtoInfo = utilisateurDto.getInfoPersonnel();

            if (dtoInfo.getNom() != null && !dtoInfo.getNom().trim().isEmpty()) {
                existingUtilisateur.getInfoPersonnel().setNom(dtoInfo.getNom());
            }
            if (dtoInfo.getPrenom() != null && !dtoInfo.getPrenom().trim().isEmpty()) {
                existingUtilisateur.getInfoPersonnel().setPrenom(dtoInfo.getPrenom());
            }

            if (dtoInfo.getEmail() != null && !dtoInfo.getEmail().trim().isEmpty()) {
                // l'email doit être toujours unique
                if (!dtoInfo.getEmail().equals(existingUtilisateur.getInfoPersonnel().getEmail())) {
                    if (utilisateurRepository.findUtilisateurByInfoPersonnel_Email(dtoInfo.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("L'email '" + dtoInfo.getEmail() + "' est déjà utilisé par un autre utilisateur."); // Corrected message for user
                    }
                }
                existingUtilisateur.getInfoPersonnel().setEmail(dtoInfo.getEmail());
            }
            if (dtoInfo.getDateNaissance() != null) {
                existingUtilisateur.getInfoPersonnel().setDateNaissance(dtoInfo.getDateNaissance());
            }
            if (dtoInfo.getTelephone() != null && !dtoInfo.getTelephone().trim().isEmpty()) {
                existingUtilisateur.getInfoPersonnel().setTelephone(dtoInfo.getTelephone());
            }
            if (dtoInfo.getAdresse() != null && !dtoInfo.getAdresse().trim().isEmpty()) {
                existingUtilisateur.getInfoPersonnel().setAdresse(dtoInfo.getAdresse());
            }
            if (dtoInfo.getGenre() != null) {
                existingUtilisateur.getInfoPersonnel().setGenre(dtoInfo.getGenre());
            }
        }

        // Only update password if a new one is provided and is valid
        if (utilisateurDto.getMotDePasse() != null && !utilisateurDto.getMotDePasse().isEmpty()) {
            if (utilisateurDto.getMotDePasse().length() < 8) {
                throw new RuntimeException("le nouveau mot de passe doit avoir minimun 8 caractères.");
            }
            existingUtilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDto.getMotDePasse()));
        }

        //mise à jour du rôle
        if (utilisateurDto.getRoles() != null && !utilisateurDto.getRoles().isEmpty()) {
            List<Role> newRoles = new ArrayList<>();
            for (RoleDto roleDto : utilisateurDto.getRoles()) {
                Role role = roleRepository.findFirstByRoleType(roleDto.getType())
                        .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable: " + roleDto.getType()));
                newRoles.add(role);
            }
            existingUtilisateur.setRole(newRoles);
        }

        //mise à jour du service médical
        if (utilisateurDto.getServiceMedical() != null) {
            existingUtilisateur.setServiceMedical(utilisateurDto.getServiceMedical());
        }

        // Mise à jour du statut actif
        if (utilisateurDto.getActif() != null) {
            existingUtilisateur.setActif(utilisateurDto.getActif());
        }

        UtilisateurDto updatedUtilisateur = UtilisateurDto.fromEntity(
                utilisateurRepository.save(
                        existingUtilisateur
                )
        );

        // --- Ajout de l'historique ---
        StringBuilder logMessage = new StringBuilder("Mise à jour de l'utilisateur ID: " + utilisateurId + ".");
        if (!Objects.equals(oldNom, updatedUtilisateur.getInfoPersonnel().getNom())) logMessage.append(" Nom: '").append(oldNom).append("' -> '").append(updatedUtilisateur.getInfoPersonnel().getNom()).append("'.");
        if (!Objects.equals(oldPrenom, updatedUtilisateur.getInfoPersonnel().getPrenom())) logMessage.append(" Prénom: '").append(oldPrenom).append("' -> '").append(updatedUtilisateur.getInfoPersonnel().getPrenom()).append("'.");
        if (!Objects.equals(oldEmail, updatedUtilisateur.getInfoPersonnel().getEmail())) logMessage.append(" Email: '").append(oldEmail).append("' -> '").append(updatedUtilisateur.getInfoPersonnel().getEmail()).append("'.");
        if (!Objects.equals(oldActif, updatedUtilisateur.getActif())) logMessage.append(" Statut actif: ").append(oldActif).append(" -> ").append(updatedUtilisateur.getActif()).append("'.");
        if (!Objects.equals(oldServiceMedical, updatedUtilisateur.getServiceMedical())) logMessage.append(" Service Médical: '").append(oldServiceMedical).append("' -> '").append(updatedUtilisateur.getServiceMedical()).append("'.");
        String currentRoles = updatedUtilisateur.getRoles().stream().map(r -> r.getType().name()).collect(Collectors.joining(", "));
        if (!Objects.equals(oldRoles, currentRoles)) logMessage.append(" Rôles: '").append(oldRoles).append("' -> '").append(currentRoles).append("'.");


        historiqueActionService.enregistrerAction(logMessage.toString());
        // --- Fin de l'ajout de l'historique ---

        return updatedUtilisateur;
    }



    @Override
    @Transactional
    public void deleteUtilisateur(Integer utilisateurId) {

        Utilisateur utilisateurToDelete = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur avec l'ID " + utilisateurId + " n'existe pas et ne peut pas être supprimé."));


        if (!utilisateurToDelete.getConsultations().isEmpty()) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression de l'utilisateur ID: " + utilisateurId + ": échec, car associé à des consultations."
            );
            throw new IllegalStateException("Impossible de supprimer cet utilisateur (médecin) car il est associé à des consultations.");
        }
        if (!utilisateurToDelete.getRendezVous().isEmpty()) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression de l'utilisateur ID: " + utilisateurId + ": échec, car associé à des rendez-vous."
            );
            throw new IllegalStateException("Impossible de supprimer cet utilisateur (médecin) car il est associé à des rendez-vous.");
        }

        utilisateurRepository.deleteById(utilisateurId);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Suppression de l'utilisateur ID: " + utilisateurId + ", Nom: " + utilisateurToDelete.getInfoPersonnel().getNom()
        );
        // --- Fin de l'ajout de l'historique ---
    }


    @Override
    @Transactional
    public UtilisateurDto findUtilisateurByInfoPersonnel_Email(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide pour la recherche.");
        }
        UtilisateurDto foundUser = utilisateurRepository.findUtilisateurByInfoPersonnel_Email(email)
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("l'utilisateur avec l'email "+ email +" n'existe pas"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche de l'utilisateur par email: '" + email + "'"
        );
        // --- Fin de l'ajout de l'historique ---

        return foundUser;
    }



    @Override
    @Transactional
    public List<UtilisateurDto> findUtilisateurByRole_RoleType(RoleType roleType) {
        if (roleType == null) {
            throw new IllegalArgumentException("le rôle ne peut pas être vide pour la recherche.");
        }

        List<UtilisateurDto> usersByRole = utilisateurRepository.findUtilisateurByRole_RoleType(roleType)
                .stream()
                .filter(Objects::nonNull)
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche d'utilisateurs par rôle: '" + roleType.name() + "' (nombre de résultats: " + usersByRole.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return usersByRole;
    }



    @Override
    @Transactional
    public UtilisateurDto updateUtilisateurStatus(Integer utilisatuerId, boolean isActive) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(utilisatuerId)
                .orElseThrow(() -> new RuntimeException("l'utilisateur n'existe pas"));

        boolean oldStatus = existingUtilisateur.getActif();
        existingUtilisateur.setActif(isActive);
        UtilisateurDto updatedUser = UtilisateurDto.fromEntity(utilisateurRepository.save(existingUtilisateur));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Mise à jour du statut de l'utilisateur ID: " + utilisatuerId + " de '" + oldStatus + "' à '" + isActive + "'."
        );
        // --- Fin de l'ajout de l'historique ---

        return updatedUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This method is for Spring Security's UserDetailsService,
        // and doesn't directly involve business logic for history logging in the same way.
        // It's typically handled at the authentication layer.
        return null;
    }
}