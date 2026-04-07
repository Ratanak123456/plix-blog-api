package co.istad.blogapplication.blog.dto.request;


import co.istad.blogapplication.blog.entity.Post.PostStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String thumbnail;

    private Long categoryId;

    private List<Long> tagIds;

    private PostStatus status = PostStatus.DRAFT;

    private LocalDateTime scheduledAt;
}