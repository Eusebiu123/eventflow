package ro.eusebiu.identityservice.service;

import ro.eusebiu.identityservice.dto.request.LoginRequest;
import ro.eusebiu.identityservice.dto.request.RefreshTokenRequest;
import ro.eusebiu.identityservice.dto.request.RegisterRequest;
import ro.eusebiu.identityservice.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}