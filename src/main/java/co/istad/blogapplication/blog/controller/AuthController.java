package co.istad.blogapplication.blog.controller;

import co.istad.blogapplication.blog.dto.request.ForgotPasswordRequest;
import co.istad.blogapplication.blog.dto.request.LoginRequest;
import co.istad.blogapplication.blog.dto.request.RegisterRequest;
import co.istad.blogapplication.blog.dto.request.ResetPasswordRequest;
import co.istad.blogapplication.blog.dto.response.AuthResponse;
import co.istad.blogapplication.blog.repository.UserRepository;
import co.istad.blogapplication.blog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // HEAD /check-email — returns 200 if available, 409 if taken
    @RequestMapping(value = "/check-email", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkEmail(@RequestParam String email) {
        return userRepository.existsByEmail(email)
                ? ResponseEntity.status(HttpStatus.CONFLICT).build()
                : ResponseEntity.ok().build();
    }

    // OPTIONS — explicit CORS preflight response
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
                .header("Allow", "GET, POST, HEAD, OPTIONS")
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
