package com.example.GestionClinique.dto.dtoConnexion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String token;
    private String username;
    private String photoUrl;
    private Collection<? extends GrantedAuthority> authorities;
}
