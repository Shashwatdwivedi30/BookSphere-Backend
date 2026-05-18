package com.booksphere.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        when(request.getRequestURI()).thenReturn("/any-other-path");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtil.isTokenValid("validToken")).thenReturn(true);
        when(jwtUtil.getSubjectFromToken("validToken")).thenReturn("test@example.com");
        when(jwtUtil.getRoleFromToken("validToken")).thenReturn("USER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AuthPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/auth/login");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).isTokenValid(anyString());
    }
}
