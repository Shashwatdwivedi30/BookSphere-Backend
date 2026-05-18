package com.booksphere.apigateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Allow OPTIONS requests for CORS preflight
        if (exchange.getRequest().getMethod().name().equalsIgnoreCase("OPTIONS")) {
            return chain.filter(exchange);
        }

        // Only secure /api/v1/ routes
        if (!path.startsWith("/api/v1/")) {
            return chain.filter(exchange);
        }

        // Allow all authentication service endpoints to pass through (Security handled by Auth Service)
        if (path.contains("/auth/")) {
            return chain.filter(exchange);
        }

        // Allow public GET requests for books and reviews
        if (exchange.getRequest().getMethod().name().equalsIgnoreCase("GET") && 
            (path.startsWith("/api/v1/books") || path.startsWith("/api/v1/reviews"))) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return returnUnauthorized(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.verifyTokenLegitimacy(token)) {
            return returnUnauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> returnUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        String origin = exchange.getRequest().getHeaders().getFirst("Origin");
        if (origin != null && !origin.isEmpty()) {
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", origin);
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
        } else {
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
        }
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        return exchange.getResponse().setComplete();
    }
}
