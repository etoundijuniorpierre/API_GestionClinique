package com.example.GestionClinique.controller;


import com.example.GestionClinique.configuration.security.jwtConfig.JwtResponse;
import com.example.GestionClinique.configuration.security.jwtConfig.JwtUtil;
import com.example.GestionClinique.controller.controllerApi.UtilisateurApi;
import com.example.GestionClinique.dto.UtilisateurDto;
import com.example.GestionClinique.dto.dtoConnexion.LoginRequest;
import com.example.GestionClinique.dto.dtoConnexion.LoginResponse;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Optional;

@RestController
public class UtilisateurController implements UtilisateurApi {
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthenticationManager authenticationManager; // Injectez l'AuthenticationManager
    private final UserDetailsService userDetailsService; // Injectez votre CustomUserDetailsService
    private final JwtUtil jwtUtil; // Injectez votre JwtUtil

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService, UtilisateurRepository utilisateurRepository,
                                 AuthenticationManager authenticationManager,
                                 @Qualifier("utilisateurServiceImpl") UserDetailsService userDetailsService,
                                 JwtUtil jwtUtil) {
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Step 1: Authenticate the user
            // This line is critical. It will throw an AuthenticationException
            // if credentials are incorrect or user is not found.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Step 2: If authentication is successful, get the UserDetails
            // The authentication object contains the principal (UserDetails) if successful.
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // POINT D'ARRÊT MAJEUR ICI: Inspecter 'userDetails'
            // Est-ce que 'userDetails' est null à ce point? Si oui, l'authentification a échoué silencieusement,
            // ou un problème avec le principal après l'authentification.
            if (userDetails == null) {
                System.err.println("ERREUR: UserDetails est null après l'authentification.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: UserDetails is null.");
            }

            // Step 3: Generate JWT token using the UserDetails
            // This is where your original NPE was reported.
            String jwt = jwtUtil.generateToken(userDetails); // Ligne 71 de JwtUtil

            // Step 4: Return successful response with token
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));

        } catch (org.springframework.security.core.AuthenticationException e) {
            // Catch specific Spring Security authentication exceptions
            System.err.println("Authentication failed for user " + loginRequest.getEmail() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("An error occurred during login for user " + loginRequest.getEmail() + ": " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @Override
    public UtilisateurDto createUtilisateur(UtilisateurDto utilisateurDto) {
        return utilisateurService.createUtilisateur(utilisateurDto);
    }

    @Override
    public UtilisateurDto findUtilisateurById(Integer id) {
        return utilisateurService.findUtilisateurById(id);
    }

    @Override
    public List<UtilisateurDto> findUtilisateurByInfoPersonnel_Nom(String nom) {
        return utilisateurService.findUtilisateurByInfoPersonnel_Nom(nom);
    }

    @Override
    public UtilisateurDto findUtilisateurByInfoPersonnel_Email(String email) {
        return utilisateurService.findUtilisateurByInfoPersonnel_Email(email);
    }

    @Override
    public List<UtilisateurDto> findUtilisateurByRole_RoleType(RoleType roleType) {
        return utilisateurService.findUtilisateurByRole_RoleType(roleType);
    }

    @Override
    public List<UtilisateurDto> findAllUtilisateur() {
        return utilisateurService.findAllUtilisateur();
    }

    @Override
    public UtilisateurDto updateUtilisateur(Integer id, UtilisateurDto utilisateurDto) {
        return utilisateurService.updateUtilisateur(id, utilisateurDto);
    }

    @Override
    public UtilisateurDto updateUtilisateurStatus(Integer id, boolean isActive) {
        return utilisateurService.updateUtilisateurStatus(id, isActive);
    }

    @Override
    public void deleteUtilisateur(Integer id) {

        utilisateurService.deleteUtilisateur(id);
    }
}
