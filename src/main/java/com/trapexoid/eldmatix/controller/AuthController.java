package com.trapexoid.eldmatix.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.secret}")
    private String secret;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String tenantId) {
        // In a real application, you would validate password here.
        // For skeleton demonstration, we issue token for provided user/tenant.

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Jwts.builder()
                .subject(username)
                .claim("tenantId", tenantId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();
    }
}
