package com.maven.neuto.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    @Value("${spring.app.JWT_ACCESS_SECRET_KEY}")
    private String JWT_ACCESS_SECRET_KEY;

    @Value("${spring.app.JWT_REFRESH_SECRET_KEY}")
    private String JWT_REFRESH_SECRET_KEY;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;


    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove Bearer prefix
        }
        return null;
    }

    private Key getAccessKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_ACCESS_SECRET_KEY));
    }

    private Key getRefreshKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_REFRESH_SECRET_KEY));
    }

    public String generateAccessToken(Long userId) {
        return generateToken(userId, jwtExpirationMs, getAccessKey());
    }

    public String generateRefreshToken(Long userId) {
        return generateToken(userId, jwtRefreshExpirationMs, getRefreshKey());
    }

    private String generateToken(Long userId, int expirationTime, Key key) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public Long getUserIdFromJwtToken(String token, boolean isRefreshToken) {
        Key key = isRefreshToken ? getRefreshKey() : getAccessKey();
        log.info("getUserIdFromJwtToken for request: {}", key);
        return Long.valueOf(Jwts.parser().verifyWith((SecretKey) key).build()
                .parseSignedClaims(token).getPayload().getSubject());
    }

    public boolean validateJwtToken(String token, boolean isRefreshToken) {
        try {
            Key key = isRefreshToken ? getRefreshKey() : getAccessKey();
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
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
