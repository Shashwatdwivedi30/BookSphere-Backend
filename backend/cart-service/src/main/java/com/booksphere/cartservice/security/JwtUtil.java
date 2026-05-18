package com.booksphere.cartservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final String sharedSecretKey = "bookspherebookspherebookspherebooksphere";
    private final Key signingKey = Keys.hmacShaKeyFor(sharedSecretKey.getBytes());

    public Map<String, String> getIdentityInfo(String token) {
        Claims body = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Map<String, String> info = new HashMap<>();
        info.put("email", body.getSubject());
        info.put("role", body.get("role", String.class));
        return info;
    }

    public boolean checkTokenIntegrity(String jwt) {
        try {
            Map<String, String> data = getIdentityInfo(jwt);
            return data.get("email") != null && data.get("role") != null;
        } catch (Exception ex) {
            return false;
        }
    }
}