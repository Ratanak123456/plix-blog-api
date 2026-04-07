package co.istad.blogapplication.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private long totalPosts;
    private long totalLikes;
    private long totalComments;
    private long totalBookmarks;
    private List<PostResponse> mostLikedPosts;
    private List<PostResponse> recentPosts;
}
