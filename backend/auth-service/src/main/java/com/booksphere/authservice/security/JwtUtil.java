package com.booksphere.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String authSecretString = "bookspherebookspherebookspherebooksphere";
    private final Key signingKey = Keys.hmacShaKeyFor(authSecretString.getBytes());

    public String generateToken(String userEmail, String userRole) {
        return Jwts.builder()
                .subject(userEmail)
                .claim("role", userRole)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(signingKey)
                .compact();
    }

    public String getSubjectFromToken(String jwt) {
        return getClaims(jwt).getSubject();
    }

    public String getRoleFromToken(String jwt) {
        return getClaims(jwt).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            return getSubjectFromToken(token) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}