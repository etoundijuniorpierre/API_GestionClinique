package com.example.GestionClinique.controller;

import com.example.GestionClinique.configuration.security.jwtConfig.JwtResponse;
import com.example.GestionClinique.configuration.security.jwtConfig.JwtUtil;
import com.example.GestionClinique.dto.dtoConnexion.LoginRequest;
import com.example.GestionClinique.dto.dtoConnexion.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.GestionClinique.utils.Constants.API_NAME;


@Tag(name = "AUTHENTIFICATION", description = "API pour se login dans notre système")
@RequestMapping
@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil; // Injectez votre JwtUtil

    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                System.err.println("ERREUR: UserDetails est null après l'authentification.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: UserDetails is null.");
            }

            String jwt = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));

        } catch (org.springframework.security.core.AuthenticationException e) {

            System.err.println("Authentication failed for user " + loginRequest.getEmail() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        } catch (Exception e) {

            System.err.println("An error occurred during login for user " + loginRequest.getEmail() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
