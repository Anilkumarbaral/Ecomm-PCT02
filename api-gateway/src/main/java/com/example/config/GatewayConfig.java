package com.example.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //Auth Service Routes
                .route("auth-service", r -> r.path("/api/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                                .circuitBreaker(config -> config
                                        .setName("authServiceCircuitBreaker")
                                        .setFallbackUri("/fallback/auth")))
                        .uri("lb://auth-service"))
                // User Service Routes
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                                .circuitBreaker(config -> config
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("/fallback/user")))
                        .uri("lb://user-service"))

                // Product Service Routes
                .route("product-service", r -> r.path("/api/products/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                                .circuitBreaker(config -> config
                                        .setName("productServiceCircuitBreaker")
                                        .setFallbackUri("/fallback/product")))
                        .uri("lb://product-service"))

                // Order Service Routes
                .route("order-service", r -> r.path("/api/orders/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://order-service"))

                // Auth Service Routes (No JWT filtering)
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.addRequestHeader("X-Gateway", "API-Gateway"))
                        .uri("lb://auth-service"))

                // Static content or public APIs
                .route("public-service", r -> r.path("/public/**")
                        .uri("lb://public-service"))

                .build();
    }
}