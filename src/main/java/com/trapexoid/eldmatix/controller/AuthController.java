package com.trapexoid.eldmatix.controller;

import com.trapexoid.eldmatix.dto.AuthResponse;
import com.trapexoid.eldmatix.dto.LoginRequest;
import com.trapexoid.eldmatix.dto.SignupRequest;
import com.trapexoid.eldmatix.model.User;
import com.trapexoid.eldmatix.repository.UserRepository;
import com.trapexoid.eldmatix.service.UserService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.secret}")
    private String secret;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
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

                byte[] keyBytes = Decoders.BASE64.decode(secret);

                String token = Jwts.builder()
                        .subject(user.getUsername())
                        .claim("tenantId", user.getTenantId())
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                        .signWith(Keys.hmacShaKeyFor(keyBytes))
                        .compact();

                AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(user.getUsername(), user.getTenantId());
                AuthResponse response = new AuthResponse(token, userInfo);

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            User user = userService.registerUser(
                    signupRequest.getUsername(),
                    signupRequest.getPassword(),
                    signupRequest.getTenantId()
            );
            
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(user.getUsername(), user.getTenantId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(null, userInfo)); // No token on signup, user must login
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed", e.getMessage()));
        }
    }
