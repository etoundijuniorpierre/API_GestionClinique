package com.example.GestionClinique.configuration.security;



import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.UtilisateurRepository;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        String username = null;
        String password = null;
        Long id = null;

        if (utilisateur != null && utilisateur.getActif()) {

                username = utilisateur.getEmail();
                id = utilisateur.getId();
            // Vérifier motDePasse
            password = utilisateur.getPassword();
            if (password == null) {
                // Log ou lancer une exception si le mot de passe est null
                System.err.println("ERREUR: Le mot de passe est null pour l'utilisateur : " + username);
                throw new IllegalStateException("Mot de passe manquant pour l'utilisateur.");
            }

            // Vérifier les autorités
            if (utilisateur.getAuthorities() == null) {
                // Log ou lancer une exception si getAuthorities() retourne null
                System.err.println("ERREUR: getAuthorities() retourne null pour l'utilisateur : " + username);
                throw new IllegalStateException("Les autorités sont nulles pour l'utilisateur.");
            }
            if (utilisateur.getAuthorities().isEmpty()) {
                // Optionnel : Log ou lancer une exception si l'utilisateur n'a aucun rôle
                System.out.println("ATTENTION: L'utilisateur " + username + " n'a aucune autorité/rôle assigné.");
                // Ceci n'est pas une erreur bloquante pour Spring Security mais peut-être un problème logique pour votre app
            }

        } else {
            // Normalement, on ne devrait jamais atteindre ce bloc grâce à orElseThrow
            System.err.println("ERREUR FATALE: Utilisateur est null APRES orElseThrow dans UserDetailsServiceImpl.");
            throw new UsernameNotFoundException("Utilisateur introuvable ou problème interne.");
        }


        // IMPORTANT : Construire l'objet UserDetails de Spring Security
        // Si vous arrivez ici, 'username', 'password', et 'utilisateur.getAuthorities()' devraient être non nuls
        return new User(
                 // id de l'user à exploiter plutard
                username,                 // Le nom d'utilisateur (email)
                password,                 // Le mot de passe haché
                utilisateur.getAuthorities() // Les rôles/autorisations de l'utilisateur
        );
    }
}