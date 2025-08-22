package com.noteapp.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private Key getSignKey(){
        if (jwtSecret == null) {
            log.error("JWT secret is null. Configure 'jwt.secret'.");
            throw new IllegalStateException("JWT secret is not configured");
        }
        // HS256 için en az 256-bit (32 byte) secret önerilir
        if (jwtSecret.length() < 32) {
            log.error("JWT secret is too short (length: {}). Must be >= 32 characters.", jwtSecret.length());
            throw new IllegalStateException("JWT secret is too short; must be at least 32 characters");
        }
        log.debug("Using JWT secret: {}", jwtSecret.substring(0, Math.min(jwtSecret.length(), 10)) + "...");
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username){
        log.debug("Generating token for username: {}", username);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
        log.debug("Generated token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
        return token;
    }

    public String getUsernameFromToken(String token){
        log.debug("Extracting username from token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            log.debug("Successfully extracted username: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            throw e;
        }
    }

    public Boolean validateToken(String token){
        log.debug("Validating token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            log.debug("Token validation successful");
            return true;
        }catch (ExpiredJwtException e){
            log.warn("Token expired at {}", e.getClaims() != null ? e.getClaims().getExpiration() : "unknown");
            return false;
        }catch (JwtException e){
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }

    }
}
