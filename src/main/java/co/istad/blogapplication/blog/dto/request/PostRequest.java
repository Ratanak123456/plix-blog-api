package co.istad.blogapplication.blog.dto.request;

import co.istad.blogapplication.blog.entity.Post.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PostRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Pattern(
            regexp = "^(https?://.+)?$",
            message = "Thumbnail must be a valid http or https URL"
    )
    @Size(max = 500, message = "Thumbnail URL must be at most 500 characters")
    private String thumbnail;

    private UUID categoryId;

    @Size(max = 10, message = "A post can have at most 10 tags")
    private List<UUID> tagIds;

    @NotNull(message = "Status is required")
    private PostStatus status = PostStatus.DRAFT;
}
