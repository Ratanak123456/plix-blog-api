package co.istad.blogapplication.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private Long parentId;
}
