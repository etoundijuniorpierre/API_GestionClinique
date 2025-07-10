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
    @Transactional // Ensure transactional for database operations if any
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        if (!utilisateur.getActif()) {
            throw new UsernameNotFoundException("Utilisateur avec l'email : " + email + " est désactivé.");
        }

        // Defensive checks, good for debugging but consider custom exceptions for production
        if (utilisateur.getPassword() == null || utilisateur.getPassword().isBlank()) {
            System.err.println("ERREUR: Le mot de passe est null ou vide pour l'utilisateur : " + utilisateur.getEmail());
            throw new IllegalStateException("Mot de passe manquant pour l'utilisateur.");
        }

        // This assumes getAuthorities() is implemented in your Utilisateur entity
        // If not, you'll need to create authorities here based on utilisateur.getRole()
        if (utilisateur.getAuthorities() == null) {
            System.err.println("ERREUR: getAuthorities() retourne null pour l'utilisateur : " + utilisateur.getEmail());
            throw new IllegalStateException("Les autorités sont nulles pour l'utilisateur.");
        }

        return new MonUserDetailsCustom(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                utilisateur.getActif(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                utilisateur.getAuthorities()
        );
    }
}