package com.hackathon.bankingapp.Services;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtUtil {
    private static final String SECRET = "304f42d8711889585a433a9ec28ebae231a3c379c61b6199023b6ae00a6d4a3d";
    private static final long EXPIRATION_TIME = 86400000;
    private static final Set<String> revokedTokens = new HashSet<>();

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static String extractUsername(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static boolean validateToken(String token) {

        if (token == null || token.isEmpty()) {
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (revokedTokens.contains(token)) {
            return false;
        }

        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static void revokeToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        revokedTokens.add(token);
    }
}
