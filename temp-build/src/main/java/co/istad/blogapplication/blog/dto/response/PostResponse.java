package co.istad.blogapplication.blog.dto.response;

import co.istad.blogapplication.blog.entity.Post.PostStatus;
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
public class PostResponse {
    private UUID id;
    private String title;
    private String slug;
    private String content;
    private String thumbnailUrl;
    private PostStatus status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer bookmarkCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse author;
    private CategoryResponse category;
    private List<TagResponse> tags;
    private boolean likedByCurrentUser;
    private boolean bookmarkedByCurrentUser;
}