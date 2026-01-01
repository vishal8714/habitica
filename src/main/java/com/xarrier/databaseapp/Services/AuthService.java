package com.xarrier.databaseapp.Services;

import com.xarrier.databaseapp.DTOs.Auth.AuthResponse;
import com.xarrier.databaseapp.DTOs.Auth.LoginRequest;
import com.xarrier.databaseapp.DTOs.Auth.RegisterRequest;
import com.xarrier.databaseapp.Entities.Concession;
import com.xarrier.databaseapp.Entities.User;
import com.xarrier.databaseapp.Exceptions.EmailAlreadyExistsException;
import com.xarrier.databaseapp.Exceptions.InvalidCredentialsException;
import com.xarrier.databaseapp.Exceptions.UsernameAlreadyExistsException;
import com.xarrier.databaseapp.Repositories.ConcessionRepository;
import com.xarrier.databaseapp.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ConcessionRepository concessionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /* --------------------
       Register
       -------------------- */
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Create default concessions
        Concession concession = Concession.builder()
                .user(user)
                .habitFreezesLeft(2)
                .dailyFreezesLeft(1)
                .build();

        concessionRepository.save(concession);

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    /* --------------------
       Login
       -------------------- */
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
