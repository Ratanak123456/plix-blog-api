package co.istad.blogapplication.blog.controller;

import co.istad.blogapplication.blog.service.LikeBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import co.istad.blogapplication.blog.dto.response.UserResponse;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class LikeBookmarkController {

    private final LikeBookmarkService likeBookmarkService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean liked = likeBookmarkService.togglePostLike(postId, userDetails.getUsername());
        int likeCount = likeBookmarkService.getPostSummary(postId).getLikeCount();
        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", likeCount));
    }

    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean bookmarked = likeBookmarkService.toggleBookmark(postId, userDetails.getUsername());
        int bookmarkCount = likeBookmarkService.getPostSummary(postId).getBookmarkCount();
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked, "bookmarkCount", bookmarkCount));
    }

    @GetMapping("/{postId}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean liked = likeBookmarkService.isPostLiked(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @GetMapping("/{postId}/bookmark/status")
    public ResponseEntity<Map<String, Object>> getBookmarkStatus(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean bookmarked = likeBookmarkService.isBookmarked(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<Page<UserResponse>> getPostLikes(
            @PathVariable UUID postId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(likeBookmarkService.getPostLikes(postId, pageable));
    }
}
