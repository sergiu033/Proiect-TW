package com.example.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // User service routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "User-Service")
                                .addResponseHeader("X-Service", "User-Service")
                        )
                        .uri("lb://user"))

                // Review service routes
                .route("review-service", r -> r
                        .path("/reviews/**")
                        .uri("lb://review"))

                .build();
    }
}