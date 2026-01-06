package com.xarrier.databaseapp.Controllers;

import com.xarrier.databaseapp.DTOs.Auth.AuthResponse;
import com.xarrier.databaseapp.DTOs.Auth.LoginRequest;
import com.xarrier.databaseapp.DTOs.Auth.RegisterRequest;
import com.xarrier.databaseapp.Entities.User;
import com.xarrier.databaseapp.JWT.JwtUtil;
import com.xarrier.databaseapp.Repositories.UserRepository;
import com.xarrier.databaseapp.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

// nothing works

    private final AuthService authService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
           @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(
//           @Valid @RequestBody LoginRequest request
//    ) {
//        return ResponseEntity.ok(authService.login(request));
//    }
    // This comments says that I have edited via the github editor


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }
}
