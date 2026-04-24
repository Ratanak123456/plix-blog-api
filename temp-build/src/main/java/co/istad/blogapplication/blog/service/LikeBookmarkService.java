package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LikeBookmarkService {
    boolean togglePostLike(UUID postId, String username);
    boolean toggleBookmark(UUID postId, String username);
    boolean isPostLiked(UUID postId, String username);
    boolean isBookmarked(UUID postId, String username);
    Page<UserResponse> getPostLikes(UUID postId, Pageable pageable);
    Post getPostSummary(UUID postId);
}
