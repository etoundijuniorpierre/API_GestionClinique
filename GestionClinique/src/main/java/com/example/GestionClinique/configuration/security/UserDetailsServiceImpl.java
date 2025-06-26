package com.example.GestionClinique.configuration.security;



import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.repository.UtilisateurRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // --- POINT DE CONTRÔLE 1 : L'objet 'utilisateur' est-il réellement trouvé et non null ici ? ---
        // Explication : Si vous avez mis un breakpoint ici, est-ce que 'utilisateur' est null ?
        // Normalement, .orElseThrow() devrait éviter qu'il soit null si l'Optional est vide.
        // Si findUtilisateurByInfoPersonnel_Email() renvoie un Optional.empty(), cette ligne lèvera une exception.
        // Si elle retourne un Optional avec un Utilisateur qui lui-même contient des champs null,
        // la NPE arrivera plus tard.
        Utilisateur utilisateur = utilisateurRepository.findUtilisateurByInfoPersonnel_Email(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        // --- POINT DE CONTRÔLE 2 : Vérifier les champs utilisés pour construire UserDetails ---
        // Chaque appel de méthode sur 'utilisateur' ou 'utilisateur.getInfoPersonnel()'
        // est une source potentielle de NullPointerException si l'objet est null.

        String username = null;
        String password = null;

        if (utilisateur != null) { // Ce bloc devrait être exécuté si orElseThrow n'a pas été déclenché
            // Vérifier InfoPersonnel
            if (utilisateur.getInfoPersonnel() != null) {
                username = utilisateur.getInfoPersonnel().getEmail();
            } else {
                // Log ou lancer une exception si infoPersonnel est null (c'est une anomalie si l'email est censé être là)
                System.err.println("ERREUR: InfoPersonnel est null pour l'utilisateur avec ID: " + utilisateur.getId());
                throw new IllegalStateException("InfoPersonnel est manquant pour l'utilisateur.");
            }

            // Vérifier motDePasse
            password = utilisateur.getMotDePasse();
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
                username,                 // Le nom d'utilisateur (email)
                password,                 // Le mot de passe haché
                utilisateur.getAuthorities() // Les rôles/autorisations de l'utilisateur
        );
    }
}