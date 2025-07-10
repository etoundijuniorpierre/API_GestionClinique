package com.example.GestionClinique.service.authService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getCurrentAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();


        if (principal instanceof MonUserDetailsCustom) {
            return ((MonUserDetailsCustom) principal).getId();
        } else if (principal instanceof UserDetails) {
            System.err.println("Warning: Principal is UserDetails but not MonUserDetailsCustom. Cannot retrieve custom ID. Username: " + ((UserDetails)principal).getUsername());
            return null;
        } else {
            System.err.println("Warning: Unexpected principal type in SecurityUtil: " + principal.getClass().getName());
            return null;
        }
    }

    public String getCurrentAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            return authentication.getName();
        }
        return null;
    }

    public UserDetails getCurrentAuthenticatedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return (UserDetails) principal;
            }
        }
        return null;
    }
}
