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
        log.debug("Using JWT secret: {}", jwtSecret != null ? jwtSecret.substring(0, Math.min(jwtSecret.length(), 10)) + "..." : "null");
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
        }catch (JwtException e){
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }

    }
}
