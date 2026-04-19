package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.config.AppConfig;
import co.istad.blogapplication.blog.dto.request.LoginRequest;
import co.istad.blogapplication.blog.dto.request.RegisterRequest;
import co.istad.blogapplication.blog.dto.response.AuthResponse;
import co.istad.blogapplication.blog.entity.RefreshToken;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.exception.UnauthorizedException;
import co.istad.blogapplication.blog.repository.RefreshTokenRepository;
import co.istad.blogapplication.blog.repository.UserRepository;
import co.istad.blogapplication.blog.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    private final PasswordEncoder passwordEncoder = new AppConfig().passwordEncoder();

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                new AppConfig().modelMapper(),
                jwtService
        );
    }

    @Test
    void registerAuthorCreatesActiveAuthor() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setFullName("Alice Doe");
        request.setEmail("alice@example.com");
        request.setPassword("Password123");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        AuthResponse response = authService.registerAuthor(request);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("alice@example.com", savedUser.getEmail());
        assertEquals(User.Role.AUTHOR, savedUser.getRole());
        assertTrue(savedUser.isActive());
        assertEquals("alice@example.com", response.getUser().getEmail());
        assertEquals(User.Role.AUTHOR.name(), response.getUser().getRole());
        assertTrue(passwordEncoder.matches("Password123", savedUser.getPasswordHash()));
    }

    @Test
    void loginReturnsTokensForUsernameIdentifier() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("verified");
        request.setPassword("password123");

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("verified")
                .fullName("Verified User")
                .email("verified@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .isActive(true)
                .isDeleted(false)
                .role(User.Role.AUTHOR)
                .build();

        when(userRepository.findByUsernameAndIsDeletedFalse("verified")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtService.hashToken("refresh-token")).thenReturn("refresh-hash");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(request);

        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("verified@example.com", response.getUser().getEmail());
        assertEquals("refresh-hash", refreshTokenCaptor.getValue().getTokenHash());
    }

    @Test
    void loginFailsForEmailIdentifier() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("verified@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsernameAndIsDeletedFalse("verified@example.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void refreshTokenUsesUserIdSubjectForCurrentTokens() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("verified")
                .fullName("Verified User")
                .email("verified@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .isActive(true)
                .isDeleted(false)
                .role(User.Role.AUTHOR)
                .build();

        RefreshToken storedToken = RefreshToken.builder()
                .tokenHash("refresh-hash")
                .expiresAt(java.time.LocalDateTime.now().plusHours(1))
                .build();

        when(jwtService.extractSubject("refresh-token")).thenReturn(userId.toString());
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(jwtService.hashToken("refresh-token")).thenReturn("refresh-hash");
        when(refreshTokenRepository.findByTokenHash("refresh-hash")).thenReturn(Optional.of(storedToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(jwtService.hashToken("new-refresh-token")).thenReturn("new-refresh-hash");

        AuthResponse response = authService.refreshToken("refresh-token");

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshTokenFallsBackToUsernameSubjectForLegacyTokens() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("verified")
                .fullName("Verified User")
                .email("verified@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .isActive(true)
                .isDeleted(false)
                .role(User.Role.AUTHOR)
                .build();

        RefreshToken storedToken = RefreshToken.builder()
                .tokenHash("refresh-hash")
                .expiresAt(java.time.LocalDateTime.now().plusHours(1))
                .build();

        when(jwtService.extractSubject("refresh-token")).thenReturn("verified");
        when(userRepository.findByUsernameAndIsDeletedFalse("verified")).thenReturn(Optional.of(user));
        when(jwtService.hashToken("refresh-token")).thenReturn("refresh-hash");
        when(refreshTokenRepository.findByTokenHash("refresh-hash")).thenReturn(Optional.of(storedToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(jwtService.hashToken("new-refresh-token")).thenReturn("new-refresh-hash");

        AuthResponse response = authService.refreshToken("refresh-token");

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
    }
}
