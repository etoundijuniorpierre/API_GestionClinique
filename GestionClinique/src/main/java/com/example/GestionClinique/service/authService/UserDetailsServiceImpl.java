package com.example.GestionClinique.service.authService;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.transaction.Transactional; // Use jakarta.transaction for @Transactional
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueActionService historiqueActionService;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository, HistoriqueActionService historiqueActionService) {
        this.utilisateurRepository = utilisateurRepository;
        this.historiqueActionService = historiqueActionService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        if (!utilisateur.getActif()) {
            throw new UsernameNotFoundException("Utilisateur avec l'email : " + email + " est désactivé.");
        }

        UserDetails userDetails = new MonUserDetailsCustom(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                utilisateur.getPhotoProfil(), // Passer le chemin de la photo
                true,
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                utilisateur.getAuthorities()
        );

        historiqueActionService.enregistrerAction(
                "connexion avec l'Email : " + email, utilisateur.getId());

        return userDetails;
    }
}