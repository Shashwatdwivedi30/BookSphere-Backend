package com.booksphere.bookservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            if (jwtUtil.isAuthTokenValid(jwt)) {
                String userEmail = jwtUtil.getEmailFromToken(jwt);
                String userRole = jwtUtil.getUserRoleFromToken(jwt);

                var auth = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole))
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}