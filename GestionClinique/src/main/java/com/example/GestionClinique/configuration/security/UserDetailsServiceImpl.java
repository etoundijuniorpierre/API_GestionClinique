package com.example.GestionClinique.configuration.security;



import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
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


        if (utilisateur.getPassword() == null || utilisateur.getPassword().isBlank()) {
            System.err.println("ERREUR: Le mot de passe est null ou vide pour l'utilisateur : " + utilisateur.getEmail());
            throw new IllegalStateException("Mot de passe manquant pour l'utilisateur.");
        }

        if (utilisateur.getAuthorities() == null) {
            System.err.println("ERREUR: getAuthorities() retourne null pour l'utilisateur : " + utilisateur.getEmail());
            throw new IllegalStateException("Les autorités sont nulles pour l'utilisateur.");
        }

        return new MonUserDetailsCustom(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                utilisateur.getActif(),
                true,
                true,
                true,
                utilisateur.getAuthorities()
        );
    }
}