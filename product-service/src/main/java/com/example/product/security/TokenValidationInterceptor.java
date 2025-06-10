package com.example.product.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${app.gateway.header.name:X-Gateway-Validated}")
    private String gatewayHeaderName;

    @Value("${app.gateway.header.value:true}")
    private String gatewayHeaderValue;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        // Skip validation for non-API endpoints
        if (!request.getRequestURI().startsWith("/api/")) {
            return true;
        }

        try {
            // Check if request came through API Gateway
            String gatewayHeader = request.getHeader(gatewayHeaderName);

            if (gatewayHeaderValue.equals(gatewayHeader)) {
                log.debug("Request validated by API Gateway, extracting user info from headers");
                return handleGatewayValidatedRequest(request, response);
            } else {
                log.debug("Direct service call detected, performing JWT validation");
                return handleDirectServiceCall(request, response);
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");
            return false;
        }
    }

    private boolean handleGatewayValidatedRequest(HttpServletRequest request,
                                                  HttpServletResponse response) throws IOException {

        String userId = request.getHeader("X-User-Id");
        String userEmail = request.getHeader("X-User-Email");
        String roles = request.getHeader("X-User-Roles");

        if (userId == null || roles == null) {
            log.warn("Missing required headers from API Gateway");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid gateway headers");
            return false;
        }

        // Set security context from gateway headers
        setSecurityContextFromHeaders(userId, userEmail, roles);
        return true;
    }

    private boolean handleDirectServiceCall(HttpServletRequest request,
                                            HttpServletResponse response) throws IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);

        if (!jwtTokenUtil.validateToken(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
            return false;
        }

        // Extract user info from JWT and set security context
        String userId = jwtTokenUtil.getUsernameFromToken(token);
        String userEmail = jwtTokenUtil.getEmailFromToken(token);
        List<String> roles = jwtTokenUtil.getRolesFromToken(token);

        setSecurityContextFromJWT(userId, userEmail, roles);
        return true;
    }

    private void setSecurityContextFromHeaders(String userId, String userEmail, String rolesStr) {
        List<String> roles = Arrays.asList(rolesStr.split(","));
        setSecurityContext(userId, userEmail, roles);
    }

    private void setSecurityContextFromJWT(String userId, String userEmail, List<String> roles) {
        setSecurityContext(userId, userEmail, roles);
    }

    private void setSecurityContext(String userId, String userEmail, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(String::trim)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());

        UserPrincipal userPrincipal = new UserPrincipal(userId, userEmail, authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Security context set for user: {} with roles: {}", userId, roles);
    }
}