package com.user.infrastructure.security.adapter;

import com.user.domain.auth.TokenData;
import com.user.domain.auth.port.TokenProviderPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtServiceAdapter implements TokenProviderPort {

    private final Key key;

    public JwtServiceAdapter(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generate(TokenData data) {
        return Jwts.builder()
                .setSubject(data.getUserId().toString())
                .claim("email", data.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(data.getExpMillis()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public TokenData parse(String token) {
        var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return new TokenData(
                UUID.fromString(claims.getSubject()),
                claims.get("email", String.class),
                claims.getExpiration().getTime()
        );
    }
}
