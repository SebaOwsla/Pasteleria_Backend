package com.example.pasteleria.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
@Component
public class JwtUtil {
    private final Key key;
    private final long expiracion;
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiracion
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiracion = expiracion;
    }
    public String generarToken(String correo, String tipoUsuario) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", tipoUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiracion))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public Claims obtenerClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    public String obtenerCorreo(String token) {
        return obtenerClaims(token).getSubject();
    }
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }
    public boolean validarToken(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
