package com.example.GestionClinique.configuration.security.jwtConfig;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
// Example of a JwtAuthenticationFilter (simplified)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // Your service to handle JWT operations
    private final UserDetailsService userDetailsService; // To load user details

    public JwtAuthenticationFilter(JwtUtil jwtUtil, @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;

    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continue if no token
            return;
        }

        jwt = authHeader.substring(7).trim();
        if (jwt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Empty JWT token");
            return;
        }
        try {
            userEmail = jwtUtil.extractUsername(jwt); // This is where expiration is checked by the library
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtUtil.isTokenExpired(jwt, userDetails)) { // Re-check validity (expiration, signature, etc.)
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            // IMPORTANT: Handle expired token explicitly
            System.err.println("JWT expired: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Send 401 Unauthorized
            response.getWriter().write("JWT expired or invalid: " + e.getMessage());
            return; // STOP THE FILTER CHAIN
        } catch (SignatureException | MalformedJwtException | IllegalArgumentException e) {
            // Handle other JWT invalidation reasons
            System.err.println("Invalid JWT signature or format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Send 401 Unauthorized
            response.getWriter().write("JWT invalid: " + e.getMessage());
            return; // STOP THE FILTER CHAIN
        }

        // Si le nom d'utilisateur est trouvé et qu'il n'y a pas déjà d'authentification dans le contexte
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Valider le token
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Créer l'objet d'authentification et le définir dans le SecurityContext
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response); // Continue to next filter if token is valid
    }
}