package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.RoleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.LoggingAspect;
import com.example.GestionClinique.service.UtilisateurService;
import com.example.GestionClinique.service.authService.SecurityUtil;
import com.example.GestionClinique.service.photoService.FileStorageService;
import com.example.GestionClinique.service.photoService.FileStorageServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.example.GestionClinique.model.entity.enumElem.RoleType.*;


@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository; // Inject RoleRepository
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final RendezVousRepository rendezVousRepository;
    private final FileStorageServiceImpl fileStorageService;
    private final HistoriqueActionService historiqueActionService;
    private final LoggingAspect loggingAspect;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder, RendezVousRepository rendezVousRepository, FileStorageService fileStorageService, FileStorageServiceImpl fileStorageService1, HistoriqueActionService historiqueActionService, SecurityUtil securityUtil, LoggingAspect loggingAspect) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.rendezVousRepository = rendezVousRepository;
        this.fileStorageService = fileStorageService1;
        this.historiqueActionService = historiqueActionService;
        this.loggingAspect = loggingAspect;
    }

    

    @PostConstruct
    public void init() {
        fileStorageService.init();
    }

    @Transactional
    @Override
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        if (findUtilisateurByEmail(utilisateur.getEmail()) != null) {
            throw new IllegalArgumentException("A user with this email address already exists.");
        }

        if (utilisateur.getPassword() == null || utilisateur.getPassword().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }
        if (utilisateur.getPassword().length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
        }
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));

        if (utilisateur.getServiceMedical() == null || utilisateur.getServiceMedical().describeConstable().isEmpty()) {
            utilisateur.setServiceMedical(null);
        }

        if (utilisateur.getActif() == null) {
            utilisateur.setActif(true);
        }

        Role role = roleRepository.findFirstByRoleType(utilisateur.getRole().getRoleType())
                .orElseThrow(() -> new IllegalArgumentException("Role not found in database"));

        if(role.getRoleType()==SECRETAIRE || role.getRoleType()==ADMIN){
            utilisateur.setServiceMedical(null);
        }

        if(role.getRoleType()==MEDECIN) {
            utilisateur.setServiceMedical(utilisateur.getServiceMedical());
        }

        utilisateur.setRole(role);
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        historiqueActionService.enregistrerAction(
                String.format("Création d'un nouvel utilisateur: %s %s (ID: %d, Rôle: %s)",
                        savedUser.getNom(), savedUser.getPrenom(), savedUser.getId(), savedUser.getRole().getRoleType()),
                loggingAspect.currentUserId()
        );

        return savedUser;
    }


    @Override
    @Transactional
    public Utilisateur updatePhotoProfil(Long userId, MultipartFile photoProfil) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (photoProfil != null && !photoProfil.isEmpty()) {
            if (utilisateur.getPhotoProfil() != null) {
                fileStorageService.delete(utilisateur.getPhotoProfil());
            }
            String newPhotoPath = fileStorageService.save(photoProfil, userId);
            utilisateur.setPhotoProfil(newPhotoPath);

            historiqueActionService.enregistrerAction(
                    String.format("Mise à jour de la photo de profil de l'utilisateur ID: %d", userId),
                    loggingAspect.currentUserId()
            );
        }
        return utilisateurRepository.save(utilisateur);
    }


    @Transactional
    @Override
    public Resource getPhotoProfil(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (utilisateur.getPhotoProfil() == null) {
            throw new RuntimeException("Aucune photo de profil pour cet utilisateur");
        }
        return fileStorageService.load(utilisateur.getPhotoProfil());
    }


    @Override
    @Transactional
    public Utilisateur findUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));
    }

    @Override
    @Transactional
    public List<Utilisateur> findAllUtilisateur() {
        return utilisateurRepository.findAll();
    }

    @Override
    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));

        existingUtilisateur.setNom(utilisateurDetails.getNom());
        existingUtilisateur.setPrenom(utilisateurDetails.getPrenom());
        existingUtilisateur.setEmail(utilisateurDetails.getEmail());
        existingUtilisateur.setPassword(passwordEncoder.encode(utilisateurDetails.getPassword()));
        existingUtilisateur.setAdresse(utilisateurDetails.getAdresse());
        existingUtilisateur.setTelephone(utilisateurDetails.getTelephone());
        existingUtilisateur.setDateNaissance(utilisateurDetails.getDateNaissance());
        existingUtilisateur.setGenre(utilisateurDetails.getGenre());
        existingUtilisateur.setServiceMedical(utilisateurDetails.getServiceMedical());
        existingUtilisateur.setActif(utilisateurDetails.getActif());
        existingUtilisateur.setRole(utilisateurDetails.getRole());

        if (utilisateurDetails.getRole() != null && utilisateurDetails.getRole().getId() != null) {
            Role newRole = roleRepository.findById(utilisateurDetails.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + utilisateurDetails.getRole().getId()));
            existingUtilisateur.setRole(newRole);
        } else if (utilisateurDetails.getRole() == null) {

            throw new IllegalArgumentException("Role cannot be null for a user.");
        }

        historiqueActionService.enregistrerAction(
                String.format("Mise à jour des informations de l'utilisateur ID: %d", id),
                loggingAspect.currentUserId()
        );

        return utilisateurRepository.save(existingUtilisateur);
    }

    @Override
    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));

        historiqueActionService.enregistrerAction(
                String.format("Suppression de l'utilisateur %s %s (ID: %d)",
                        utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getId()),
                loggingAspect.currentUserId()
        );

        utilisateurRepository.delete(utilisateur);
    }

    @Transactional
    public Utilisateur findUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public List<Utilisateur> findUtilisateurByNom(String nom) {
        return utilisateurRepository.findByNom(nom);
    }

    @Override
    @Transactional
    public List<Utilisateur> findUtilisateurByRole_RoleType(RoleType roleType) {
        return utilisateurRepository.findByRole_RoleType(roleType);
    }

    @Override
    public Utilisateur updateUtilisateurStatus(Long id, boolean isActive) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));
        existingUtilisateur.setActif(isActive);

        historiqueActionService.enregistrerAction(
                String.format("Changement de statut de l'utilisateur ID: %d à %s",
                        id, isActive ? "ACTIF" : "INACTIF"),
                loggingAspect.currentUserId()
        );

        return utilisateurRepository.save(existingUtilisateur);
    }


    @Override
    public List<RendezVous> findRendezVousByMedecinSearchTerm(String medecinSearchTerm) {
        return rendezVousRepository.findRendezVousByMedecinSearchTerm(medecinSearchTerm);
    }

    @Override
    public List<RendezVous> findRendezVousForMedecinByStatus(String medecinName, StatutRDV statut) {
        return rendezVousRepository.findRendezVousForMedecinByStatus(medecinName, statut);
    }

    @Override
    public List<RendezVous> findConfirmedRendezVousForMedecinAndDate(Long medecinId, LocalDate date) {
        LocalDate PresentDate = LocalDate.now();
        return rendezVousRepository.findConfirmedRendezVousForMedecinAndDate(medecinId, PresentDate);
    }

    @Transactional
    @Override
    public List<Utilisateur> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().length() < 2) {
            return List.of();
        }
        return utilisateurRepository.searchByTerm(searchTerm);
    }


    @Override
    @Transactional
    public List<Utilisateur> findUsersWithStatusConnected() {
        return utilisateurRepository.findByStatusConnect(StatusConnect.CONNECTE);
    }


    @Override
    @Transactional
    public List<Utilisateur> findUsersWithStatusDisconnected() {
        return utilisateurRepository.findByStatusConnect(StatusConnect.DECONNECTE);
    }

    @Override
    public List<RendezVous> findAllRendezVousCONFIRMEInBeginByToday(Long medecinId) {
        return rendezVousRepository.findConfirmedRendezVousFromTodayByMedecin(
                medecinId, StatutRDV.CONFIRME, LocalDate.now());
    }

    @Override
    public List<RendezVous> findAllRendezVousCONFIRMEByMedecin(Long medecinId) {
        return rendezVousRepository.findAllConfirmedRendezVousByMedecin(medecinId, StatutRDV.CONFIRME);
    }

    @Override
    @Transactional
    public Utilisateur updatePassword(Long utilisateurId, String newPassword, String confirmPassword) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + utilisateurId));

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Les nouveaux mots de passe ne correspondent pas.");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }

        historiqueActionService.enregistrerAction(
                "Changement de mot de passe effectué",
                loggingAspect.currentUserId()
        );

        utilisateur.setPassword(passwordEncoder.encode(newPassword));
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public List<Utilisateur> findUsersWithStatusConnectedByOrderLastConnected() {
        return utilisateurRepository.findByStatusConnectOrderByLastLoginDateDesc(StatusConnect.CONNECTE);
    }

    @Override
    public List<Utilisateur> findUsersWithStatusDisconnectedByOrderLastDeConnected() {
        return utilisateurRepository.findByStatusConnectOrderByLastLogoutDateDesc(StatusConnect.DECONNECTE);
    }


    public List<Utilisateur> getMedecinsByServiceMedical(ServiceMedical serviceMedical) {
        return utilisateurRepository.findByServiceMedical(serviceMedical);
    }

    // 2. Liste des médecins disponibles par service à une heure précise
    public List<Utilisateur> getAvailableMedecinsByServiceAndTime(
            ServiceMedical serviceMedical,
            LocalDate date,
            LocalTime heure) {
        return utilisateurRepository.findMedecinsByServiceMedicalWithoutRendezVousAt(
                serviceMedical, date, heure);
    }
}