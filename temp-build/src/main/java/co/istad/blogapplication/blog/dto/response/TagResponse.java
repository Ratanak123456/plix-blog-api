package co.istad.blogapplication.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private UUID id;
    private String name;
    private String slug;
    private long postCount;
}
