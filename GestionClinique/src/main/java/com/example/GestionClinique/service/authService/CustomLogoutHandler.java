package com.example.GestionClinique.service.authService;

import com.example.GestionClinique.configuration.security.jwtConfig.JwtUtil;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.UtilisateurService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.GestionClinique.model.entity.enumElem.StatusConnect.DECONNECTE;

@Service
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final @Lazy UtilisateurService utilisateurService;
    private final HistoriqueActionService historiqueActionService;

    public CustomLogoutHandler(JwtUtil jwtUtil, @Lazy UtilisateurService utilisateurService, HistoriqueActionService historiqueActionService) {
        this.jwtUtil = jwtUtil;
        this.utilisateurService = utilisateurService;
        this.historiqueActionService = historiqueActionService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);
        String userEmail = jwtUtil.extractUsername(jwt);

        if (userEmail != null) {
            Utilisateur checkUser = utilisateurService.findUtilisateurByEmail(userEmail);
            if (checkUser != null) {
                utilisateurService.updateUserConnectStatus(checkUser.getId(), DECONNECTE);
            }
        }

    }
}