package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.LoginRequest;
import co.istad.blogapplication.blog.dto.request.RegisterRequest;
import co.istad.blogapplication.blog.dto.response.AuthResponse;
import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.RefreshToken;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.exception.BadRequestException;
import co.istad.blogapplication.blog.exception.ConflictException;
import co.istad.blogapplication.blog.exception.ForbiddenException;
import co.istad.blogapplication.blog.exception.NotFoundException;
import co.istad.blogapplication.blog.exception.UnauthorizedException;
import co.istad.blogapplication.blog.repository.RefreshTokenRepository;
import co.istad.blogapplication.blog.repository.UserRepository;
import co.istad.blogapplication.blog.service.AuthService;
import co.istad.blogapplication.blog.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    @Override
    public AuthResponse registerAuthor(RegisterRequest request) {
        return register(request, User.Role.AUTHOR);
    }

    @Override
    public AuthResponse registerAdmin(RegisterRequest request) {
        ensureAdminRegistrationAllowed();
        return register(request, User.Role.ADMIN);
    }

    private AuthResponse register(RegisterRequest request, User.Role role) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .isDeleted(false)
                .build();

        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .user(modelMapper.map(savedUser, UserResponse.class))
                .build();
    }

    private void ensureAdminRegistrationAllowed() {
        boolean adminExists = userRepository.existsByRoleAndIsDeletedFalse(User.Role.ADMIN);
        if (!adminExists) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getAuthorities().stream().noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            throw new ForbiddenException("Only an admin can create another admin");
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentifier().trim();

        User user = userRepository.findByUsernameAndIsDeletedFalse(identifier)
                .or(() -> userRepository.findAllByEmail(identifier).stream()
                        .filter(u -> !u.isDeleted())
                        .findFirst())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        jwtService.validateRefreshToken(refreshToken);

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String tokenHash = jwtService.hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now()) || storedToken.getRevokedAt() != null) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        storedToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(storedToken);

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(jwtService.hashToken(refreshToken))
                .expiresAt(LocalDateTime.now().plusDays(1)) // Requirement: Refresh expires in 1 day
                .build();
        refreshTokenRepository.save(token);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(modelMapper.map(user, UserResponse.class))
                .build();
    }
}
