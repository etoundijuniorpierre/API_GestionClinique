package com.example.GestionClinique.configuration.security.jwtConfig; // Ou votre package approprié

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

@Data // Fournit getters, setters, toString, equals et hashCode via Lombok
@AllArgsConstructor // Génère un constructeur avec tous les champs
@NoArgsConstructor // Génère un constructeur sans arguments (utile pour la désérialisation JSON)
public class JwtResponse {
    private String jwtToken;
    private String username;
    private Collection<? extends GrantedAuthority> authorities; // Pour les rôles de l'utilisateur

}