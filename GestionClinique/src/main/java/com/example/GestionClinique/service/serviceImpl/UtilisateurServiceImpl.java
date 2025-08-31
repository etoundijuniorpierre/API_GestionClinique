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
import com.example.GestionClinique.service.UtilisateurService;
import com.example.GestionClinique.service.photoService.FileStorageServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
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
                                  PasswordEncoder passwordEncoder,
                                  RendezVousRepository rendezVousRepository,
                                  FileStorageServiceImpl fileStorageService1,
                                  HistoriqueActionService historiqueActionService,
                                  LoggingAspect loggingAspect) {
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


        Role role = roleRepository.findById(utilisateur.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé avec ID: " + utilisateur.getRole().getId()));

        if(role.getRoleType()==SECRETAIRE || role.getRoleType()==ADMIN){
            utilisateur.setServiceMedical(null);
        }

        if(role.getRoleType()==MEDECIN) {
            utilisateur.setServiceMedical(utilisateur.getServiceMedical());
        }

        utilisateur.setAge((long) Period.between(utilisateur.getDateNaissance(), LocalDate.now()).getYears());

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

            // Appel de la nouvelle méthode de mise à jour partielle
            utilisateurRepository.updatePhotoProfil(userId, newPhotoPath);

            historiqueActionService.enregistrerAction(
                    String.format("Mise à jour de la photo de profil de l'utilisateur ID: %d", userId),
                    loggingAspect.currentUserId()
            );
        }
        // Recharger l'utilisateur pour renvoyer l'objet complet mis à jour
        return utilisateurRepository.findById(userId).orElse(null);
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
    @Transactional
    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec ID: " + id));

        if (utilisateurDetails.getNom() != null) {
            existingUtilisateur.setNom(utilisateurDetails.getNom());
        }
        if (utilisateurDetails.getPrenom() != null) {
            existingUtilisateur.setPrenom(utilisateurDetails.getPrenom());
        }

        if (utilisateurDetails.getEmail() != null) {
            existingUtilisateur.setEmail(utilisateurDetails.getEmail());
        }

        if (utilisateurDetails.getAdresse() != null) {
            existingUtilisateur.setAdresse(utilisateurDetails.getAdresse());
        }
        // ... et ainsi de suite pour tous les autres champs
        if (utilisateurDetails.getTelephone() != null) {
            existingUtilisateur.setTelephone(utilisateurDetails.getTelephone());
        }
        if (utilisateurDetails.getDateNaissance() != null) {
            existingUtilisateur.setDateNaissance(utilisateurDetails.getDateNaissance());
        }
        if (utilisateurDetails.getGenre() != null) {
            existingUtilisateur.setGenre(utilisateurDetails.getGenre());
        }
        if (utilisateurDetails.getServiceMedical() != null) {
            existingUtilisateur.setServiceMedical(utilisateurDetails.getServiceMedical());
        }
        if (utilisateurDetails.getActif() != null) {
            existingUtilisateur.setActif(utilisateurDetails.getActif());
        }

        if (utilisateurDetails.getRole() != null && utilisateurDetails.getRole().getId() != null) {
            Role newRole = roleRepository.findById(utilisateurDetails.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé avec ID: " + utilisateurDetails.getRole().getId()));
            existingUtilisateur.setRole(newRole);
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

    // ...
    @Override
    @Transactional
    public Utilisateur updateUtilisateurStatus(Long id, boolean isActive) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));

        utilisateurRepository.updateActifStatus(id, isActive);

        historiqueActionService.enregistrerAction(
                String.format("Changement de statut de l'utilisateur ID: %d à %s",
                        id, isActive ? "ACTIF" : "INACTIF"),
                loggingAspect.currentUserId()
        );
        return utilisateurRepository.findById(id).orElse(null);
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
        return rendezVousRepository.findConfirmedRendezVousForMedecinAndDate(medecinId, date);
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


    @Transactional
    public Utilisateur updateUserConnectStatus(Long utilisateurId, StatusConnect statusConnect) {
        if (statusConnect.equals(StatusConnect.DECONNECTE)) {
            utilisateurRepository.updateLogout(utilisateurId, statusConnect, LocalDateTime.now());
        } else if (statusConnect.equals(StatusConnect.CONNECTE)) {
            utilisateurRepository.updateLogin(utilisateurId, statusConnect, LocalDateTime.now());
        }
        return utilisateurRepository.findById(utilisateurId).orElseThrow();
    }

    @Override
    public List<Utilisateur> findAllByIds(List<Long> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            return Collections.emptyList();
        }
        return utilisateurRepository.findAllById(participantIds);
    }

}