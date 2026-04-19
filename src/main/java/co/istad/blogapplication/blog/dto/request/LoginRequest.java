package co.istad.blogapplication.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[^@]+$", message = "Use your username, not your email, to login.")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}
