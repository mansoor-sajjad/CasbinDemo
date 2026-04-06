package com.trapexoid.eldmatix.controller;

import com.trapexoid.eldmatix.dto.AuthResponse;
import com.trapexoid.eldmatix.dto.LoginRequest;
import com.trapexoid.eldmatix.model.Permission;
import com.trapexoid.eldmatix.model.Role;
import com.trapexoid.eldmatix.model.User;
import com.trapexoid.eldmatix.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Extract roles and permissions
                List<String> roles = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());

                List<String> permissions = user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .distinct()
                        .collect(Collectors.toList());

                byte[] keyBytes = Decoders.BASE64.decode(secret);

                String token = Jwts.builder()
                        .subject(user.getUsername())
                        .claim("tenantId", user.getTenantId())
                        .claim("roles", roles)
                        .claim("permissions", permissions)
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + expiration))
                        .signWith(Keys.hmacShaKeyFor(keyBytes))
                        .compact();

                AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(user.getUsername(), user.getTenantId(), roles);
                AuthResponse response = new AuthResponse(token, userInfo);

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
