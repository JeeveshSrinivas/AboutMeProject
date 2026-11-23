
package com.helpdesk.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.helpdesk.service.JwtService;

import com.helpdesk.enums.UserRole;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.helpdesk.exception.RevokedTokenException;
import com.helpdesk.repository.RevokedTokenRepository;

import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
            RevokedTokenRepository revokedTokenRepository) {
        this.jwtService = jwtService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.regionMatches(
                true, 0, "Bearer ", 0, 7)
                || authorizationHeader.substring(7).isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Malformed Authorization header");
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            Long userId = jwtService.extractUserId(token);
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("Invalid user ID");
            }

            String tokenId = jwtService.extractTokenId(token);

            if (revokedTokenRepository.existsById(tokenId)) {
                throw new RevokedTokenException("JWT token has been revoked");
            }

            String role = jwtService.extractRole(token);

            UserRole userRole;

            try {
                userRole = UserRole.valueOf(role);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Invalid user role");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null,
                        java.util.List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + userRole.name())));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (RevokedTokenException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "JWT token has been revoked");
            return;

        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
