package co.istad.blogapplication.blog.controller;

import co.istad.blogapplication.blog.service.LikeBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class LikeBookmarkController {

    private final LikeBookmarkService likeBookmarkService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean liked = likeBookmarkService.togglePostLike(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean bookmarked = likeBookmarkService.toggleBookmark(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }

    @GetMapping("/{postId}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean liked = likeBookmarkService.isPostLiked(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @GetMapping("/{postId}/bookmark/status")
    public ResponseEntity<Map<String, Object>> getBookmarkStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean bookmarked = likeBookmarkService.isBookmarked(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }
}
