package ro.eusebiu.identityservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.eusebiu.identityservice.dto.request.LoginRequest;
import ro.eusebiu.identityservice.dto.request.RefreshTokenRequest;
import ro.eusebiu.identityservice.dto.request.RegisterRequest;
import ro.eusebiu.identityservice.dto.response.AuthResponse;
import ro.eusebiu.identityservice.entity.RefreshToken;
import ro.eusebiu.identityservice.entity.Role;
import ro.eusebiu.identityservice.entity.User;
import ro.eusebiu.identityservice.exception.BadRequestException;
import ro.eusebiu.identityservice.repository.RefreshTokenRepository;
import ro.eusebiu.identityservice.repository.UserRepository;
import ro.eusebiu.identityservice.security.JwtService;
import ro.eusebiu.identityservice.service.AuthService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getUsername(),
                user.getRole().name()
        );
    }
    @Transactional
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.usernameOrEmail())
                .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getUsername(),
                user.getRole().name()
        );
    }
    @Transactional
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (storedRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedRefreshToken);
            throw new BadRequestException("Refresh token expired");
        }

        User user = storedRefreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = createRefreshToken(user);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                user.getUsername(),
                user.getRole().name()
        );
    }

    @Transactional
    private String createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush();

        String tokenValue = UUID.randomUUID() + "-" + UUID.randomUUID();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }
    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        refreshTokenRepository.delete(refreshToken);
    }
}