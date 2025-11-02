package com.example.back_end.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey key;
  private final long expMs;

  public JwtService(@Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.expiration-ms}") long expMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expMs = expMs;
  }

  public String generate(String subject) {
    Date now = new Date();
    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expMs))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
