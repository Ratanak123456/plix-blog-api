package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.request.LoginRequest;
import co.istad.blogapplication.blog.dto.request.RegisterRequest;
import co.istad.blogapplication.blog.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse registerAuthor(RegisterRequest request);
    AuthResponse registerAdmin(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}
