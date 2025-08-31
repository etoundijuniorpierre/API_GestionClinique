package com.example.GestionClinique.controller;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.UtilisateurService;
import com.example.GestionClinique.service.authService.MonUserDetailsCustom;
import com.example.GestionClinique.service.authService.UserDetailsServiceImpl;
import com.example.GestionClinique.configuration.security.jwtConfig.JwtUtil;
import com.example.GestionClinique.dto.dtoConnexion.LoginRequest;
import com.example.GestionClinique.dto.dtoConnexion.LoginResponse;
import com.example.GestionClinique.repository.UtilisateurRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;
import static com.example.GestionClinique.model.entity.enumElem.StatusConnect.DECONNECTE;


@Tag(name = "AUTHENTIFICATION", description = "API pour se login dans notre système")
@RequestMapping
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UtilisateurRepository utilisateurRepository; // Inject UtilisateurRepository
    private final UtilisateurService utilisateurService;
    private final HistoriqueActionService historiqueActionService;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UserDetailsServiceImpl userDetailsServiceImpl, UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, HistoriqueActionService historiqueActionService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.historiqueActionService = historiqueActionService;
    }

    @PostMapping(path = API_NAME + "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login un utilisateur",
            description = "Permet à un utilisateur de se connecter au système en fournissant email et mot de passe, et obtient un JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie, JWT retourné",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentification échouée (informations d'identification invalides)"),
            @ApiResponse(responseCode = "400", description = "Requête invalide (email ou mot de passe manquant)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            MonUserDetailsCustom userDetails = (MonUserDetailsCustom) authentication.getPrincipal();

            if (userDetails == null) {
                System.err.println("ERREUR: UserDetails est null après l'authentification.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: UserDetails is null.");
            }

            String jwt = jwtUtil.generateToken(userDetails);

            String photoUrl = userDetails.getPhotoProfilPath() != null ?
                    "/api/utilisateurs/" + userDetails.getId() + "/photo" :
                    null;

            historiqueActionService.enregistrerAction(
                    "Connexion avec l'email : " + loginRequest.getEmail(),
                    userDetails.getId()
            );

            return ResponseEntity.ok(new LoginResponse(
                    userDetails.getId(),
                    jwt,
                    userDetails.getUsername(),
                    photoUrl,
                    userDetails.getAuthorities()
            ));

        } catch (org.springframework.security.core.AuthenticationException e) {
            System.err.println("Authentication failed for user " + loginRequest.getEmail() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        } catch (Exception e) {
            System.err.println("An error occurred during login for user " + loginRequest.getEmail() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Token d'autorisation manquant ou invalide."));
//        }
//
//        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie."));
//    }
    }
