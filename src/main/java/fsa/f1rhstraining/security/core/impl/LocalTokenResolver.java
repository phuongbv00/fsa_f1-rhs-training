package fsa.f1rhstraining.security.core.impl;

import fsa.f1rhstraining.security.core.TokenResolver;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LocalTokenResolver implements TokenResolver {

    // Secret key used to sign the JWT tokens
    private final String SECRET_KEY_STRING = "your_secret_key_should_be_longer_and_more_complex_in_production";

    // Create a SecretKey instance for the JWT signature
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    // Token expiration time (1 hour in milliseconds)
    private final long EXPIRATION_TIME = 3600000;

    @Override
    public String generate(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        // Add roles to claims if they are provided
        if (roles != null) {
            claims.put("roles", roles);
        }

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    @Override
    public Map<String, Object> verify(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
