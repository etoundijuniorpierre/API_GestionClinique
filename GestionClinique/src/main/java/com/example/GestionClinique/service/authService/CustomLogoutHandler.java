package com.example.GestionClinique.service.authService;

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

    private final UtilisateurRepository utilisateurRepository;
    private final LoggingAspect loggingAspect;
    @Lazy private final UtilisateurService utilisateurService;

    public CustomLogoutHandler(UtilisateurRepository utilisateurRepository,
                               LoggingAspect loggingAspect,
                               @Lazy UtilisateurService utilisateurService) {
        this.utilisateurRepository = utilisateurRepository;
        this.loggingAspect = loggingAspect;
        this.utilisateurService = utilisateurService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Log out logic should only proceed if an authenticated user is found in the context
        if (authentication == null || !(authentication.getPrincipal() instanceof MonUserDetailsCustom)) {
            System.out.println("Logout attempt from unauthenticated or invalid session. No user details to update.");
            return;
        }

        MonUserDetailsCustom userDetails = (MonUserDetailsCustom) authentication.getPrincipal();
        String username = userDetails.getUsername();

        utilisateurService.updateUserConnectStatus(userDetails.getId(), StatusConnect.CONNECTE);


        SecurityContextHolder.clearContext();
    }
}