package com.example.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
//        add routes
        return builder.routes()
                .route(p -> p
                        .path("/library/users/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "User-Service")
                                .addResponseHeader("X-Service", "User-Service")
                                .rewritePath("/library/users/(?<segment>.*)", "/api/users/${segment}")
                        )
                        .uri("lb://User-Service"))
                .build();
    }
}
