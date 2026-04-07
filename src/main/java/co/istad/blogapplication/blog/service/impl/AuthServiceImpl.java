package co.istad.blogapplication.blog.service.impl;


import co.istad.blogapplication.blog.dto.request.ForgotPasswordRequest;
import co.istad.blogapplication.blog.dto.request.LoginRequest;
import co.istad.blogapplication.blog.dto.request.RegisterRequest;
import co.istad.blogapplication.blog.dto.request.ResetPasswordRequest;
import co.istad.blogapplication.blog.dto.response.AuthResponse;
import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.repository.UserRepository;
import co.istad.blogapplication.blog.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .isVerified(true) // skip verify
                .isEnabled(true)
                .build();

        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setUser(modelMapper.map(user, UserResponse.class));
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        AuthResponse response = new AuthResponse();
        response.setUser(modelMapper.map(user, UserResponse.class));
        return response;
    }

    @Override
    public void verifyEmail(String token) {
        // Not needed (skip)
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        // Optional (can skip or simple logic)
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // Optional
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        throw new RuntimeException("Not supported");
    }
}