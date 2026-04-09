package co.istad.blogapplication.blog.security;

import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(User user) {
        return generateToken(user.getUsername(), user, accessTokenExpiration, "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user.getUsername(), user, refreshTokenExpiration, "refresh");
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public void validateAccessToken(String token) {
        validateTokenType(token, "access");
    }

    public void validateRefreshToken(String token) {
        validateTokenType(token, "refresh");
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    private String generateToken(String subject, User user, long expiration, String tokenType) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .claims(Map.of("role", user.getRole().name(), "type", tokenType))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .signWith(secretKey)
                .compact();
    }

    private void validateTokenType(String token, String expectedType) {
        Claims claims = parseClaims(token);
        String actualType = claims.get("type", String.class);
        if (!expectedType.equals(actualType)) {
            throw new UnauthorizedException("Invalid token type");
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception exception) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }
}