package com.booksphere.apigateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();
    private final String secret = "bookspherebookspherebookspherebooksphere";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    @Test
    void testValidToken() {
        String token = Jwts.builder()
                .subject("test@example.com")
                .claim("role", "USER")
                .signWith(key)
                .compact();

        assertTrue(jwtUtil.verifyTokenLegitimacy(token));
        assertEquals("test@example.com", jwtUtil.resolveSubject(token));
        assertEquals("USER", jwtUtil.resolveAuthorities(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtil.verifyTokenLegitimacy("invalid.token"));
    }

    @Test
    void testExpiredToken() {
        String token = Jwts.builder()
                .subject("test@example.com")
                .claim("role", "USER")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        assertFalse(jwtUtil.verifyTokenLegitimacy(token));
    }

    @Test
    void testMissingClaims() {
        String token = Jwts.builder()
                .subject("test@example.com")
                .signWith(key)
                .compact();

        assertFalse(jwtUtil.verifyTokenLegitimacy(token));
    }
}
