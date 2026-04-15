package co.istad.blogapplication.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String fullName;
    private String email;
    private String bio;

    @JsonProperty("profileImage")
    private String profileImage;

    @JsonProperty("coverImage")
    private String coverImage;

    private boolean isVerified;
    private String role;
    private LocalDateTime createdAt;
}
