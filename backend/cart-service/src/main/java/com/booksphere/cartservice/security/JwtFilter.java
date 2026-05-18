package com.booksphere.cartservice.security;

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

        final String authHeaderValue = request.getHeader("Authorization");
        if (authHeaderValue != null && authHeaderValue.startsWith("Bearer ")) {
            String jwtTokenValue = authHeaderValue.substring(7);
            if (jwtUtil.checkTokenIntegrity(jwtTokenValue)) {
                var infoMap = jwtUtil.getIdentityInfo(jwtTokenValue);
                String emailId = infoMap.get("email");
                String userRoleName = infoMap.get("role");

                UsernamePasswordAuthenticationToken authObj = new UsernamePasswordAuthenticationToken(
                        emailId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRoleName))
                );

                SecurityContextHolder.getContext().setAuthentication(authObj);
            }
        }

        filterChain.doFilter(request, response);
    }
}