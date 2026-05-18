package com.booksphere.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.rewritePath("/api/v1/auth/(?<segment>.*)", "/auth/${segment}"))
                        .uri("lb://auth-service"))
                
                .route("book-service", r -> r.path("/api/v1/books/**")
                        .filters(f -> f.rewritePath("/api/v1/books/(?<segment>.*)", "/books/${segment}"))
                        .uri("lb://book-service"))
                
                .route("cart-service", r -> r.path("/api/v1/cart/**")
                        .filters(f -> f.rewritePath("/api/v1/cart/(?<segment>.*)", "/cart/${segment}"))
                        .uri("lb://cart-service"))
                
                .route("order-service", r -> r.path("/api/v1/orders/**")
                        .filters(f -> f.rewritePath("/api/v1/orders/(?<segment>.*)", "/orders/${segment}"))
                        .uri("lb://order-service"))
                
                .route("wallet-service", r -> r.path("/api/v1/wallet/**")
                        .filters(f -> f.rewritePath("/api/v1/wallet/(?<segment>.*)", "/wallet/${segment}"))
                        .uri("lb://wallet-service"))
                
                .route("wishlist-service", r -> r.path("/api/v1/wishlist/**")
                        .filters(f -> f.rewritePath("/api/v1/wishlist/(?<segment>.*)", "/wishlist/${segment}"))
                        .uri("lb://wishlist-service"))
                
                .route("review-service", r -> r.path("/api/v1/reviews/**")
                        .filters(f -> f.rewritePath("/api/v1/reviews/(?<segment>.*)", "/reviews/${segment}"))
                        .uri("lb://review-service"))
                
                .route("notification-service", r -> r.path("/api/v1/notifications/**")
                        .filters(f -> f.rewritePath("/api/v1/notifications/(?<segment>.*)", "/notifications/${segment}"))
                        .uri("lb://notification-service"))
                .build();
    }
}
