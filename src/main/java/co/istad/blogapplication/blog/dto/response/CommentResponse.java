package co.istad.blogapplication.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private UUID id;
    private String content;
    private LocalDateTime createdAt;
    private UserResponse user;
    private UUID parentId;
    private List<CommentResponse> replies;
    private long likeCount;
    private boolean likedByCurrentUser;
}
