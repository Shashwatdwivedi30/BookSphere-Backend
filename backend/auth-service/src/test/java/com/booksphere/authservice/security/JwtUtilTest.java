package com.booksphere.authservice.security;

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
    void testJwtFlow() {
        String token = jwtUtil.generateToken("test@example.com", "USER");
        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals("test@example.com", jwtUtil.getSubjectFromToken(token));
        assertEquals("USER", jwtUtil.getRoleFromToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    @Test
    void testExpiredToken() {
        String token = Jwts.builder()
                .subject("test@example.com")
                .claim("role", "USER")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key)
                .compact();

        assertFalse(jwtUtil.isTokenValid(token));
    }

    @Test
    void testMalformedToken() {
        assertFalse(jwtUtil.isTokenValid("malformed"));
    }
}
