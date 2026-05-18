package com.booksphere.bookservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final String jwtSecretValue = "bookspherebookspherebookspherebooksphere";
    private final Key signingKey = Keys.hmacShaKeyFor(jwtSecretValue.getBytes());

    public String getEmailFromToken(String authToken) {
        return fetchAllClaims(authToken).getSubject();
    }

    public String getUserRoleFromToken(String authToken) {
        return fetchAllClaims(authToken).get("role", String.class);
    }

    public boolean isAuthTokenValid(String token) {
        try {
            Claims body = fetchAllClaims(token);
            return body.getSubject() != null && body.get("role") != null;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims fetchAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}