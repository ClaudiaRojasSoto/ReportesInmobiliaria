package com.example.demo.api.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); 
    private static final long EXPIRATION_TIME = 864_000_000; // 10 días

    public static String generateToken(String username, List<String> roles) {
        String token = Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
        System.out.println("Generated Token: " + token);
        return token;
    }

    public static boolean validateToken(String token, String username) {
        try {
            String tokenUsername = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            boolean isValid = (username.equals(tokenUsername) && !isTokenExpired(token));
            System.out.println("Token validation result: " + isValid);
            return isValid;
        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        String username = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        System.out.println("Extracted username from token: " + username);
        return username;
    }

    public static List<GrantedAuthority> getRolesFromToken(String token) {
        List<String> roles = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);

        System.out.println("Extracted roles from token: " + roles);
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private static boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        boolean expired = expiration.before(new Date());
        System.out.println("Token expired: " + expired);
        return expired;
    }
}
