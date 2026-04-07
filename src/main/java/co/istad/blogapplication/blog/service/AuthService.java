package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.request.ForgotPasswordRequest;
import co.istad.blogapplication.blog.dto.request.LoginRequest;
import co.istad.blogapplication.blog.dto.request.RegisterRequest;
import co.istad.blogapplication.blog.dto.request.ResetPasswordRequest;
import co.istad.blogapplication.blog.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyEmail(String token);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthResponse refreshToken(String refreshToken);
}