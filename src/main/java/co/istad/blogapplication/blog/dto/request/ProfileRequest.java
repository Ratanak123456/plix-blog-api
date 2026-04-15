package co.istad.blogapplication.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {

    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_]+$",
            message = "Username can only contain letters, numbers, and underscores"
    )
    private String username;

    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @Size(max = 500, message = "Bio must be at most 500 characters")
    private String bio;

    @Pattern(
            regexp = "^(https?://.+)?$",
            message = "Profile image must be a valid http or https URL"
    )
    @Size(max = 500, message = "Profile image URL must be at most 500 characters")
    private String profileImage;

    @Pattern(
            regexp = "^(https?://.+)?$",
            message = "Cover image must be a valid http or https URL"
    )
    @Size(max = 500, message = "Cover image URL must be at most 500 characters")
    private String coverImage;
}
