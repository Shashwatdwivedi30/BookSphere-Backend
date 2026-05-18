package com.booksphere.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final String applicationJwtSecret = "bookspherebookspherebookspherebooksphere";
    private final Key validationKey = Keys.hmacShaKeyFor(applicationJwtSecret.getBytes());

    public String resolveSubject(String token) {
        return parseClaimsFromJwt(token).getSubject();
    }

    public String resolveAuthorities(String token) {
        return parseClaimsFromJwt(token).get("role", String.class);
    }

    public boolean verifyTokenLegitimacy(String sToken) {
        try {
            Claims claims = parseClaimsFromJwt(sToken);
            return claims.getSubject() != null && claims.get("role") != null;
        } catch (Exception err) {
            return false;
        }
    }

    private Claims parseClaimsFromJwt(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) validationKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}