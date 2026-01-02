package com.xarrier.databaseapp.JWT;


import com.xarrier.databaseapp.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    private static final String SECRET_KEY = "super-secret-key-change-this";
//    private static final long EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days
//
//    public String generateToken(User user) {
//
//        return Jwts.builder()
//                .setSubject(user.getId().toString())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
//                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public Long extractUserId(String token) {
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        return Long.valueOf(claims.getSubject());
//    }
//}


import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {


    private static final SecretKey SECRET_KEY =
            Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final long EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000;

    public String generateToken(User user) {

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Long extractUserId(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }
}

