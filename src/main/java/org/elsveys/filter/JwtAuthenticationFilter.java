package org.elsveys.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.elsveys.entity.Credentials;
import org.elsveys.repository.CredentialsRepository;
import org.elsveys.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CredentialsRepository credentialsRepository; // Add this

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String alias = jwtUtil.extractAlias(token);
                String role = jwtUtil.extractRole(token);

                if (alias != null && !jwtUtil.isTokenExpired(token)) {
                    // Get username from alias
                    Optional<Credentials> creds = credentialsRepository.findByAlias(alias);
                    if (creds.isPresent()) {
                        String username = creds.get().getUser().getUsername();

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        username,  // Use username instead of alias
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                                );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Invalid token, continue without authentication
            }
        }

        filterChain.doFilter(request, response);
    }
}