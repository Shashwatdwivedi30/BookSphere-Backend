package com.booksphere.bookservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(this::configureMatchers)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void configureMatchers(org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/books/add", "/books/update/**", "/books/delete/**").hasRole("ADMIN")
            .requestMatchers("/books", "/books/all", "/books/{id}", "/books/search/**", "/books/sync/**",
                    "/books/reduce-stock/**", "/books/increase-stock/**", "/swagger-ui/**", "/v3/api-docs/**",
                    "/swagger-ui.html").permitAll()
            .anyRequest().authenticated();
    }
}