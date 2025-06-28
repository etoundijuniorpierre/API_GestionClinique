package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.repository.RoleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.UtilisateurService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@Transactional // Ensures atomicity for database operations
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository; // Inject RoleRepository
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @Autowired
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        
        if (utilisateur.getRole() != null && utilisateur.getRole().getId() != null) {
            Role existingRole = roleRepository.findById(utilisateur.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + utilisateur.getRole().getId()));
            utilisateur.setRole(existingRole);
        } else {
            throw new IllegalArgumentException("A user must be associated with a valid role.");
        }
        
        if (utilisateur.getActif() == null) {
            utilisateur.setActif(true);
        }
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public Utilisateur findUtilisateurById(Integer id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));
    }

    @Override
    @Transactional
    public List<Utilisateur> findAllUtilisateur() {
        return utilisateurRepository.findAll();
    }

    @Override
    public Utilisateur updateUtilisateur(Integer id, Utilisateur utilisateurDetails) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));

        existingUtilisateur.setNom(utilisateurDetails.getNom());
        existingUtilisateur.setPrenom(utilisateurDetails.getPrenom());
        existingUtilisateur.setEmail(utilisateurDetails.getEmail());
        existingUtilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDetails.getMotDePasse()));
        existingUtilisateur.setAdresse(utilisateurDetails.getAdresse());
        existingUtilisateur.setTelephone(utilisateurDetails.getTelephone());
        existingUtilisateur.setDateNaissance(utilisateurDetails.getDateNaissance());
        existingUtilisateur.setGenre(utilisateurDetails.getGenre());
        existingUtilisateur.setServiceMedical(utilisateurDetails.getServiceMedical());
        existingUtilisateur.setActif(utilisateurDetails.getActif());

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
    public void deleteUtilisateur(Integer id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));
        utilisateurRepository.delete(utilisateur);
    }

    @Transactional
    public Utilisateur findUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with email: " + email));
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
    public Utilisateur updateUtilisateurStatus(Integer id, boolean isActive) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));
        existingUtilisateur.setActif(isActive);
        return utilisateurRepository.save(existingUtilisateur);
    }


//    public Utilisateur updatePassword(Integer id, String newPassword) {
//        Utilisateur utilisateur = utilisateurRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found with ID: " + id));
//        utilisateur.setMotDePasse(passwordEncoder.encode(newPassword));
//        return utilisateurRepository.save(utilisateur);
//    }
}