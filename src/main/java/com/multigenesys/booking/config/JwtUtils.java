package com.multigenesys.booking.config;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.multigenesys.booking.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    // NOTE: for prod use env var or vault. Keep small for example.
    private final Key key = Keys.hmacShaKeyFor("verysecretverysecretverysecretverysecret".getBytes());
    private final long expirationMs = 3600_000; // 1 hour

    public String generateToken(String username, Long userId, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(Enum::name).collect(Collectors.toList()));
        claims.put("userId", userId);
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}

