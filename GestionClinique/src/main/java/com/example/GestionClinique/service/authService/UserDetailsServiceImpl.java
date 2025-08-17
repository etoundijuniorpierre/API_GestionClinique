package com.example.GestionClinique.service.authService;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.UtilisateurService;
import jakarta.transaction.Transactional; // Use jakarta.transaction for @Transactional
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueActionService historiqueActionService;
    @Lazy private final UtilisateurService utilisateurService;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository,
                                  HistoriqueActionService historiqueActionService,
                                  @Lazy UtilisateurService utilisateurService) {
        this.utilisateurRepository = utilisateurRepository;
        this.historiqueActionService = historiqueActionService;
        this.utilisateurService = utilisateurService;
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

        utilisateurService.updateUserConnectStatus(utilisateur.getId(), StatusConnect.CONNECTE);

        return userDetails;
    }
}