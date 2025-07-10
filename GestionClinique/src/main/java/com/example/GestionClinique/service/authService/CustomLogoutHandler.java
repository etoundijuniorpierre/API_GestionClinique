package com.example.GestionClinique.service.authService;

import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import com.example.GestionClinique.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomLogoutHandler implements LogoutHandler {

    private final UtilisateurRepository utilisateurRepository;

    public CustomLogoutHandler(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
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

        utilisateurRepository.findByEmail(username).ifPresent(utilisateur -> {
            utilisateur.setLastLogoutDate(LocalDateTime.now());
            utilisateur.setStatusConnect(StatusConnect.DECONNECTE); // Set status to DECONNECTE
            utilisateurRepository.save(utilisateur); // Save the updated user
            System.out.println("User " + username + " logged out at " + LocalDateTime.now() + ". Status set to DECONNECTE.");
        });


        SecurityContextHolder.clearContext();
    }
}