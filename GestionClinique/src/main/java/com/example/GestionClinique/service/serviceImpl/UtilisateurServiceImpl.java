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
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UtilisateurServiceImpl implements UtilisateurService, UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder, @Lazy HistoriqueActionService historiqueActionService) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.historiqueActionService = historiqueActionService;
    }



    @Override
    @Transactional
    public UtilisateurDto createUtilisateur(UtilisateurDto utilisateurDto) {
        // --- Input Validation ---
        if (utilisateurDto.getInfoPersonnel() == null || utilisateurDto.getInfoPersonnel().getEmail() == null ||
                utilisateurDto.getInfoPersonnel().getNom() == null || utilisateurDto.getInfoPersonnel().getPrenom() == null) {
            throw new IllegalArgumentException("Les informations personnelles (nom, prénom, email) de l'utilisateur sont obligatoires.");
        }

        if (utilisateurRepository.findUtilisateurByInfoPersonnel_Email(utilisateurDto.getInfoPersonnel().getEmail()).isPresent()) {
            throw new IllegalArgumentException("L'utilisateur avec l'email '" + utilisateurDto.getInfoPersonnel().getEmail() + "' existe déjà.");
        }

        if (utilisateurDto.getMotDePasse() == null || utilisateurDto.getMotDePasse().isEmpty()) {
            throw new IllegalArgumentException("L'utilisateur doit absolument avoir un mot de passe.");
        }
        if (utilisateurDto.getMotDePasse().length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit avoir au minimum 8 caractères."); // Changed to IllegalArgumentException
        }

        // Medecin specific validation
        boolean isMedecin = utilisateurDto.getRoles() != null &&
                utilisateurDto.getRoles().stream().anyMatch(roleDto -> roleDto.getRoleType() == RoleType.MEDECIN);

        if (isMedecin && utilisateurDto.getServiceMedical() == null) {
            throw new IllegalArgumentException("Un médecin doit être lié à un service médical.");
        }

        // --- DTO to Entity Conversion & Relationship Handling ---
        Utilisateur utilisateurToSave = new Utilisateur();

        // Map InfoPersonnel
        InfoPersonnel infoPersonnel = InfoPersonnelDto.toEntity(utilisateurDto.getInfoPersonnel());
        utilisateurToSave.setInfoPersonnel(infoPersonnel); // Set the InfoPersonnel entity

        utilisateurToSave.setActif(utilisateurDto.getActif() != null ? utilisateurDto.getActif() : true); // Default to active
        utilisateurToSave.setServiceMedical(utilisateurDto.getServiceMedical());
        String hashedPassword = passwordEncoder.encode(utilisateurDto.getMotDePasse());
        utilisateurToSave.setMotDePasse(hashedPassword);

        // Handle Roles: Fetch existing Role entities and set them as a Set
        Set<Role> roles = new HashSet<>();
        if (utilisateurDto.getRoles() != null && !utilisateurDto.getRoles().isEmpty()) {
            for (RoleDto roleDto : utilisateurDto.getRoles()) {
                Role role = roleRepository.findFirstByRoleType(roleDto.getRoleType())
                        .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable: " + roleDto.getRoleType() + ". Veuillez vous assurer que ce rôle existe dans la base de données."));
                roles.add(role);
            }
        }
        utilisateurToSave.setRole(roles);

        // --- Save Entity ---
        Utilisateur savedEntity = utilisateurRepository.save(utilisateurToSave);
        UtilisateurDto savedUtilisateurDto = UtilisateurDto.fromEntity(savedEntity);

        // --- Historique Logging ---
        String rolesString = savedUtilisateurDto.getRoles().stream()
                .map(r -> r.getRoleType().name())
                .collect(Collectors.joining(", "));
        historiqueActionService.enregistrerAction(
                "Création de l'utilisateur ID: " + savedUtilisateurDto.getId() +
                        ", Nom: " + savedUtilisateurDto.getInfoPersonnel().getNom() + " " + savedUtilisateurDto.getInfoPersonnel().getPrenom() +
                        ", Rôles: [" + rolesString + "]"
        );
        return savedUtilisateurDto;
    }



    @Override
    @Transactional()
    public UtilisateurDto findUtilisateurById(Integer id) {
        Utilisateur foundUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur avec l'ID " + id + " n'existe pas.")); // Use EntityNotFoundException

        UtilisateurDto foundUtilisateurDto = UtilisateurDto.fromEntity(foundUtilisateur);

        historiqueActionService.enregistrerAction(
                "Recherche de l'utilisateur ID: " + id + ", Nom: " + foundUtilisateurDto.getInfoPersonnel().getNom()
        );
        return foundUtilisateurDto;
    }



    @Override
    @Transactional()
    public List<UtilisateurDto> findUtilisateurByInfoPersonnel_Nom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide pour la recherche.");
        }

        List<UtilisateurDto> usersByNom = utilisateurRepository.findUtilisateurByInfoPersonnel_Nom(nom).stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche d'utilisateurs par nom: '" + nom + "' (nombre de résultats: " + usersByNom.size() + ")"
        );
        return usersByNom;
    }



    @Override
    @Transactional()
    public List<UtilisateurDto> findAllUtilisateur() {
        List<UtilisateurDto> allUsers = utilisateurRepository.findAll().stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Affichage de tous les utilisateurs (nombre de résultats: " + allUsers.size() + ")."
        );
        return allUsers;
    }



    @Override
    @Transactional
    public UtilisateurDto updateUtilisateur(Integer utilisateurId, UtilisateurDto utilisateurDto) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur avec l'ID " + utilisateurId + " n'existe pas.")); // Use EntityNotFoundException

        // Store old values for logging
        String oldNom = existingUtilisateur.getInfoPersonnel() != null ? existingUtilisateur.getInfoPersonnel().getNom() : "N/A";
        String oldPrenom = existingUtilisateur.getInfoPersonnel() != null ? existingUtilisateur.getInfoPersonnel().getPrenom() : "N/A";
        String oldEmail = existingUtilisateur.getInfoPersonnel() != null ? existingUtilisateur.getInfoPersonnel().getEmail() : "N/A";
        Boolean oldActif = existingUtilisateur.getActif();
        String oldServiceMedical = existingUtilisateur.getServiceMedical() != null ? existingUtilisateur.getServiceMedical().name() : "N/A"; // Handle null ServiceMedical
        String oldRoles = existingUtilisateur.getRole().stream().map(r -> r.getRoleType().name()).collect(Collectors.joining(", "));


        if (existingUtilisateur.getInfoPersonnel() == null) {
            existingUtilisateur.setInfoPersonnel(new InfoPersonnel()); // Initialize if null
        }

        // --- Update Personal Information ---
        if (utilisateurDto.getInfoPersonnel() != null) {
            InfoPersonnelDto dtoInfo = utilisateurDto.getInfoPersonnel();

            // Only update if not null and not empty
            if (dtoInfo.getNom() != null && !dtoInfo.getNom().trim().isEmpty()) {
                existingUtilisateur.getInfoPersonnel().setNom(dtoInfo.getNom());
            }
            if (dtoInfo.getPrenom() != null && !dtoInfo.getPrenom().trim().isEmpty()) {
                existingUtilisateur.getInfoPersonnel().setPrenom(dtoInfo.getPrenom());
            }

            if (dtoInfo.getEmail() != null && !dtoInfo.getEmail().trim().isEmpty()) {
                if (!dtoInfo.getEmail().equals(existingUtilisateur.getInfoPersonnel().getEmail())) {
                    // Check for email uniqueness only if email is changing
                    if (utilisateurRepository.findUtilisateurByInfoPersonnel_Email(dtoInfo.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("L'email '" + dtoInfo.getEmail() + "' est déjà utilisé par un autre utilisateur.");
                    }
                }
                existingUtilisateur.getInfoPersonnel().setEmail(dtoInfo.getEmail());
            }
            // Update other InfoPersonnel fields if provided
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

        // --- Update Password ---
        if (utilisateurDto.getMotDePasse() != null && !utilisateurDto.getMotDePasse().isEmpty()) {
            if (utilisateurDto.getMotDePasse().length() < 8) {
                throw new IllegalArgumentException("Le nouveau mot de passe doit avoir au minimum 8 caractères."); // Changed to IllegalArgumentException
            }
            existingUtilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDto.getMotDePasse()));
        }

        // --- Update Roles ---
        // If the DTO provides a list of roles, clear existing ones and add new ones.
        // If the DTO's roles list is null, it means no change to roles is requested.
        // If it's an empty list, it means all roles should be removed.
        if (utilisateurDto.getRoles() != null) {
            Set<Role> newRoles = new HashSet<>();
            for (RoleDto roleDto : utilisateurDto.getRoles()) { // Iterate through potentially empty list
                Role role = roleRepository.findFirstByRoleType(roleDto.getRoleType())
                        .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable: " + roleDto.getRoleType() + ". Veuillez vous assurer que ce rôle existe dans la base de données."));
                newRoles.add(role);
            }
            existingUtilisateur.setRole(newRoles); // This will replace the old set of roles
        }


        // --- Update Medical Service ---
        if (utilisateurDto.getServiceMedical() != null) {
            existingUtilisateur.setServiceMedical(utilisateurDto.getServiceMedical());
        }

        // --- Update Active Status ---
        if (utilisateurDto.getActif() != null) {
            existingUtilisateur.setActif(utilisateurDto.getActif());
        }

        // --- Final Save ---
        UtilisateurDto updatedUtilisateur = UtilisateurDto.fromEntity(utilisateurRepository.save(existingUtilisateur));

        // --- Historique Logging ---
        StringBuilder logMessage = new StringBuilder("Mise à jour de l'utilisateur ID: " + utilisateurId + ".");
        if (!Objects.equals(oldNom, updatedUtilisateur.getInfoPersonnel().getNom())) logMessage.append(" Nom: '").append(oldNom).append("' -> '").append(updatedUtilisateur.getInfoPersonnel().getNom()).append("'.");
        if (!Objects.equals(oldPrenom, updatedUtilisateur.getInfoPersonnel().getPrenom())) logMessage.append(" Prénom: '").append(oldPrenom).append("' -> '").append(updatedUtilisateur.getInfoPersonnel().getPrenom()).append("'.");
        if (!Objects.equals(oldEmail, updatedUtilisateur.getInfoPersonnel().getEmail())) logMessage.append(" Email: '").append(oldEmail).append("' -> '").append(updatedUtilisateur.getInfoPersonnel().getEmail()).append("'.");
        if (!Objects.equals(oldActif, updatedUtilisateur.getActif())) logMessage.append(" Statut actif: ").append(oldActif).append(" -> ").append(updatedUtilisateur.getActif()).append("'.");
        String updatedServiceMedical = updatedUtilisateur.getServiceMedical() != null ? updatedUtilisateur.getServiceMedical().name() : "N/A";
        if (!Objects.equals(oldServiceMedical, updatedServiceMedical)) logMessage.append(" Service Médical: '").append(oldServiceMedical).append("' -> '").append(updatedServiceMedical).append("'.");
        String currentRoles = updatedUtilisateur.getRoles().stream().map(r -> r.getRoleType().name()).collect(Collectors.joining(", "));
        if (!Objects.equals(oldRoles, currentRoles)) logMessage.append(" Rôles: '").append(oldRoles).append("' -> '").append(currentRoles).append("'.");

        historiqueActionService.enregistrerAction(logMessage.toString());
        return updatedUtilisateur;
    }



    @Override
    @Transactional
    public void deleteUtilisateur(Integer utilisateurId) {
        Utilisateur utilisateurToDelete = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur avec l'ID " + utilisateurId + " n'existe pas et ne peut pas être supprimé."));

        // Check for associated entities (Consultations, RendezVous, Prescriptions, DossiersMedicaux, etc.)
        // Ensure that cascade types in your entity mappings are appropriate.
        // For example, if a Medecin (Utilisateur) is deleted, should their consultations also be deleted, or should this be prevented?
        // It's generally safer to prevent deletion if there are associated records.

        if (utilisateurToDelete.getConsultations() != null && !utilisateurToDelete.getConsultations().isEmpty()) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression de l'utilisateur ID: " + utilisateurId + ": échec, car associé à des consultations (" + utilisateurToDelete.getConsultations().size() + "). "
            );
            throw new IllegalStateException("Impossible de supprimer cet utilisateur car il est associé à des consultations existantes. Veuillez d'abord réaffecter ou supprimer ces consultations.");
        }
        if (utilisateurToDelete.getRendezVous() != null && !utilisateurToDelete.getRendezVous().isEmpty()) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression de l'utilisateur ID: " + utilisateurId + ": échec, car associé à des rendez-vous (" + utilisateurToDelete.getRendezVous().size() + "). "
            );
            throw new IllegalStateException("Impossible de supprimer cet utilisateur car il est associé à des rendez-vous existants. Veuillez d'abord réaffecter ou supprimer ces rendez-vous.");
        }
        if (utilisateurToDelete.getPrescriptions() != null && !utilisateurToDelete.getPrescriptions().isEmpty()) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression de l'utilisateur ID: " + utilisateurId + ": échec, car associé à des prescriptions (" + utilisateurToDelete.getPrescriptions().size() + "). "
            );
            throw new IllegalStateException("Impossible de supprimer cet utilisateur car il est associé à des prescriptions existantes. Veuillez d'abord réaffecter ou supprimer ces prescriptions.");
        }
        // Add more checks for other potential relationships (e.g., dossiers médicaux if a user can own them)

        utilisateurRepository.deleteById(utilisateurId);

        historiqueActionService.enregistrerAction(
                "Suppression de l'utilisateur ID: " + utilisateurId + ", Nom: " + utilisateurToDelete.getInfoPersonnel().getNom()
        );
    }



    @Override
    @Transactional()
    public UtilisateurDto findUtilisateurByInfoPersonnel_Email(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide pour la recherche.");
        }
        UtilisateurDto foundUser = utilisateurRepository.findUtilisateurByInfoPersonnel_Email(email)
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur avec l'email " + email + " n'existe pas.")); // Use EntityNotFoundException

        historiqueActionService.enregistrerAction(
                "Recherche de l'utilisateur par email: '" + email + "'"
        );
        return foundUser;
    }



    @Override
    @Transactional()
    public List<UtilisateurDto> findUtilisateurByRole_RoleType(RoleType roleType) {
        if (roleType == null) {
            throw new IllegalArgumentException("Le rôle ne peut pas être vide pour la recherche.");
        }

        List<UtilisateurDto> usersByRole = utilisateurRepository.findUtilisateurByRole_RoleType(roleType).stream()
                .filter(Objects::nonNull) // Filter out any potential nulls if the query could return them
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche d'utilisateurs par rôle: '" + roleType.name() + "' (nombre de résultats: " + usersByRole.size() + ")"
        );
        return usersByRole;
    }



    @Override
    @Transactional
    public UtilisateurDto updateUtilisateurStatus(Integer utilisateurId, boolean isActive) { // Corrected parameter name
        Utilisateur existingUtilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur avec l'ID " + utilisateurId + " n'existe pas.")); // Use EntityNotFoundException

        boolean oldStatus = existingUtilisateur.getActif();
        existingUtilisateur.setActif(isActive);
        UtilisateurDto updatedUser = UtilisateurDto.fromEntity(utilisateurRepository.save(existingUtilisateur));

        historiqueActionService.enregistrerAction(
                "Mise à jour du statut de l'utilisateur ID: " + utilisateurId + " de '" + oldStatus + "' à '" + isActive + "'. (Nom: " + updatedUser.getInfoPersonnel().getNom() + ")"
        );
        return updatedUser;
    }



    @Override
    @Transactional() // Important for UserDetailsService methods
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Assume 'username' is the email address for login
        return utilisateurRepository.findUtilisateurByInfoPersonnel_Email(username)
                .map(utilisateur -> new org.springframework.security.core.userdetails.User(
                        utilisateur.getInfoPersonnel().getEmail(),
                        utilisateur.getMotDePasse(),
                        utilisateur.getRole().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name())) // Roles must be prefixed with "ROLE_"
                                .collect(Collectors.toList())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + username));
    }
}