package com.example.GestionClinique.configuration.security.jwtConfig;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.service.UtilisateurService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.example.GestionClinique.model.entity.enumElem.StatusConnect.CONNECTE;
import static com.example.GestionClinique.model.entity.enumElem.StatusConnect.DECONNECTE;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final @Lazy UtilisateurService utilisateurService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                                   @Lazy UtilisateurService utilisateurService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.utilisateurService = utilisateurService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String userEmail = null; // Initialiser userEmail à null

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7).trim();
        if (jwt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT vide");
            return;
        }

        try {
            // Extrait le nom d'utilisateur, et cette méthode lance une exception si le token est invalide ou expiré.
            userEmail = jwtUtil.extractUsername(jwt);

            // Si le nom d'utilisateur est extrait avec succès et qu'il n'y a pas d'authentification dans le contexte
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Une fois les détails de l'utilisateur chargés, on valide le token.
                // Cela vérifie que le nom d'utilisateur correspond et que le token n'a pas expiré (même si l'exception est déjà gérée plus haut).
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            System.err.println("JWT expiré : " + e.getMessage());

            userEmail = e.getClaims().getSubject();
            if (userEmail != null) {
                Utilisateur user = utilisateurService.findUtilisateurByEmail(userEmail);
                if (user != null) {
                    utilisateurService.updateUserConnectStatus(user.getId(), DECONNECTE);
                }
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT expiré ou invalide : " + e.getMessage());
            return;
        }

        // Si tout est correct, on passe au filtre suivant.
        filterChain.doFilter(request, response);
    }
}
