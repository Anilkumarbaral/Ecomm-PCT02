package com.ecomm.userservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Access token expiration in milliseconds (e.g., 15 minutes)
    @Value("${jwt.access-token-expiration}")
    private long jwtAccessTokenExpiration;

    // Refresh token expiration in milliseconds (e.g., 7 days)
    @Value("${jwt.refresh-token-expiration}")
    private long jwtRefreshTokenExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate Access Token with default expiration
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtAccessTokenExpiration, "ACCESS");
    }

    // Generate Refresh Token with longer expiration
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtRefreshTokenExpiration, "REFRESH");
    }

    // General method with token type and expiration parameter
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, String tokenType) {
        extraClaims.put("type", tokenType);  // Add token type claim (ACCESS or REFRESH)

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token and check that it is an Access Token
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        return validateTokenType(token, userDetails, "ACCESS");
    }

    // Validate token and check that it is a Refresh Token
    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        return validateTokenType(token, userDetails, "REFRESH");
    }

    private boolean validateTokenType(String token, UserDetails userDetails, String expectedType) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class);

            final String username = claims.getSubject();
            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && expectedType.equals(tokenType);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // General validation ignoring user details
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}
