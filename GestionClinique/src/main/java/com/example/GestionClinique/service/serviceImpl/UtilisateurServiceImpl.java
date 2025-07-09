package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.RoleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.UtilisateurService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;


@Service
@Transactional // Ensures atomicity for database operations
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository; // Inject RoleRepository
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final RendezVousRepository rendezVousRepository;


    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder, RendezVousRepository rendezVousRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.rendezVousRepository = rendezVousRepository;
    }

    @Override
    @Transactional
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {

        if (findUtilisateurByEmail(utilisateur.getEmail()) != null) {
            throw new IllegalArgumentException("A user with this email address already exists.");
        }

        if (utilisateur.getPassword() == null || utilisateur.getPassword().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
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

        utilisateur.setRole(role); // Fixe le rôle final


        return utilisateurRepository.save(utilisateur);
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
        return utilisateurRepository.save(existingUtilisateur);
    }

    @Override
    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));
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
    public List<RendezVous> findRendezVousCONFIRMEThisDay(Long medecinId) {
        LocalDate today = LocalDate.now();
        return rendezVousRepository.findRendezVousByMedecinStatusCONFIRMEForThisDay(medecinId, today);
    }

}