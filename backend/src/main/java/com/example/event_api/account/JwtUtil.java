package com.example.event_api.account;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    //use secret key so both Account and Data services can share it
    private static final String SECRET_KEY_STRING = "MySecretKeyForJWTTokenValidationMustBeLongEnoughForHS256Algorithm";
    private final SecretKey jwtSecret = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getEncoded())
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret.getEncoded())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(jwtSecret.getEncoded())
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }
}
