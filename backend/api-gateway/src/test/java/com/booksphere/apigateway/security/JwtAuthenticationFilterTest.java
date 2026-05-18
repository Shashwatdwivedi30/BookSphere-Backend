package com.booksphere.apigateway.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private WebFilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Test
    void testFilter_OptionsRequest() {
        MockServerHttpRequest request = MockServerHttpRequest.options("/api/v1/orders").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void testFilter_NotSecurePath() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/other").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void testFilter_AuthPath() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/v1/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void testFilter_PublicGet() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/books").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void testFilter_NoAuthHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/v1/orders").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        filter.filter(exchange, chain).block();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testFilter_MalformedHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, "Basic user:pass")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        filter.filter(exchange, chain).block();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testFilter_InvalidToken() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(jwtUtil.verifyTokenLegitimacy("invalid")).thenReturn(false);
        filter.filter(exchange, chain).block();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testFilter_ValidToken() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(jwtUtil.verifyTokenLegitimacy("valid")).thenReturn(true);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }
}
