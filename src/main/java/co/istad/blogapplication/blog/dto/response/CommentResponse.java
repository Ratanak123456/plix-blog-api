package co.istad.blogapplication.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserResponse user;
    private Long parentId;
    private List<CommentResponse> replies;
    private long likeCount;
    private boolean likedByCurrentUser;
}
