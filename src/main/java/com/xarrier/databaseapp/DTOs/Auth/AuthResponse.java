package com.xarrier.databaseapp.DTOs.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String username;
    private String email;
}
