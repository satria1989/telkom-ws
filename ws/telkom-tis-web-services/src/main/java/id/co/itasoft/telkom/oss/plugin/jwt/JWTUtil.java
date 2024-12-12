package id.co.itasoft.telkom.oss.plugin.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.json.JSONObject;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {
    // Generate a strong secret key
    private static final SecretKey SECRET_KEY = generateSecretKey();
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds

    private static SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256, new SecureRandom());
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate secret key", e);
        }
    }

    // Generate JWT token with JSONObject as payload
    public static String generateToken(String username, JSONObject jsonObject) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username); // claim subject as username
        claims.put("data", jsonObject.toMap()); // Add JSONObject payload as a map
        claims.put("iat", new Date().getTime()); // Issued at
        claims.put("jti", java.util.UUID.randomUUID().toString()); // JWT ID
        return createToken(claims);
    }

    private static String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate if the token is correct
    public static boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return (username.equals(extractedUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Extract username from token
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if the token is expired
    public static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static JSONObject extractPayload(String token) {
        Claims claims = extractAllClaims(token);
        Map<String, Object> data = (Map<String, Object>) claims.get("data");
        return new JSONObject(data);
    }

    public static <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
