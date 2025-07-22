package com.example.GestionClinique.service.authService;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.UtilisateurRepository;
import jakarta.transaction.Transactional; // Use jakarta.transaction for @Transactional
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        if (!utilisateur.getActif()) {
            throw new UsernameNotFoundException("Utilisateur avec l'email : " + email + " est désactivé.");
        }

        return new MonUserDetailsCustom(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                utilisateur.getPhotoProfilPath(), // Passer le chemin de la photo
                true,
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                utilisateur.getAuthorities()
        );
    }
}