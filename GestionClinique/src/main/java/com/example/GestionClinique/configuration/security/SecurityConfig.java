package com.example.GestionClinique.configuration.security;


import com.example.GestionClinique.configuration.security.jwtConfig.JwtAuthenticationFilter;
import io.swagger.v3.oas.models.PathItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Pour @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Activez la sécurité basée sur les annotations (@PreAuthorize)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Configure le PasswordEncoder (BCryptPasswordEncoder est recommandé)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure l'AuthenticationManager pour utiliser notre UserDetailsService et PasswordEncoder
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configure le DaoAuthenticationProvider pour les détails de l'utilisateur
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Configure la chaîne de filtres de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Désactiver CSRF pour les API RESTful (si pas de gestion de token CSRF)
                .cors(cors -> cors.configurationSource((CorsConfigurationSource) corsConfigurationSource())) // Activer CORS
                .authorizeHttpRequests(authorize -> authorize
                        // Permettre l'accès public à l'enregistrement et au login
                        .requestMatchers("/clinique/utilisateur/login").permitAll()
                        // Permettre l'accès aux endpoints Swagger UI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(String.valueOf(PathItem.HttpMethod.POST), "/clinique/utilisateur/createUtilisateur").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/clinique/utilisateur/createUtilisateur").hasRole("ADMIN")
                        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Utilisation de JWTs rend la session STATELESS
                )
                .authenticationProvider(authenticationProvider()) // Utilise notre AuthenticationProvider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Ajoute notre filtre JWT

        return http.build();
    }

    // Configuration CORS pour permettre les requêtes depuis votre frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Permet les credentials (cookies, headers d'autorisation)
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*")); // Ou votre domaine frontend exact
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Les headers autorisés
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Les méthodes HTTP autorisées
        source.registerCorsConfiguration("/**", config); // Applique cette configuration à tous les chemins
        return source;
    }
}
