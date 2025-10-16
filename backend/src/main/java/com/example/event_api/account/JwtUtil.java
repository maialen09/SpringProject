package com.example.event_api.account;

import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    //use secret key so both Account and Data services can share it
    private static final String SECRET_KEY_STRING = "MySecretKeyForJWTTokenValidationMustBeLongEnoughForHS256Algorithm";
    //private final SecretKey jwtSecret = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    private final SecretKey jwtSecret = new SecretKeySpec(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
                .signWith(jwtSecret, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getEncoded())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getEncoded())
                .build()
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
