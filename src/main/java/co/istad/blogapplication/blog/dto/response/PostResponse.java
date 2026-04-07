package co.istad.blogapplication.blog.dto.response;

import co.istad.blogapplication.blog.entity.Post.PostStatus;
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
public class PostResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String thumbnail;
    private Integer readingTime;
    private PostStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse author;
    private CategoryResponse category;
    private List<TagResponse> tags;
    private long likeCount;
    private long commentCount;
    private boolean likedByCurrentUser;
    private boolean bookmarkedByCurrentUser;
}
