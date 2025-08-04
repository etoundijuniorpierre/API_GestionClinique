package com.example.GestionClinique.service.authService;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.UtilisateurService;
import com.example.GestionClinique.service.serviceImpl.LoggingAspect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomLogoutHandler implements LogoutHandler {

    private final LoggingAspect loggingAspect;
    @Lazy private final UtilisateurService utilisateurService;

    public CustomLogoutHandler(
                               LoggingAspect loggingAspect,
                               @Lazy UtilisateurService utilisateurService) {
        this.loggingAspect = loggingAspect;
        this.utilisateurService = utilisateurService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof MonUserDetailsCustom) {
            Long actuUser = loggingAspect.currentUserId();
            utilisateurService.updateUserConnectStatus(actuUser, StatusConnect.DECONNECTE);
            System.out.println("successfully logged out.");
        }

        SecurityContextHolder.clearContext();
    }
}