package co.istad.blogapplication.blog.controller;

import co.istad.blogapplication.blog.dto.request.PostRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // GET all published posts
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPublishedPosts(pageable));
    }

    // POST — create post
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(request, userDetails.getUsername()));
    }

    // GET by slug
    @GetMapping("/{slug}")
    public ResponseEntity<PostResponse> getPostBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPostBySlug(slug));
    }

    // PUT — full update
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.updatePost(id, request, userDetails.getUsername()));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // GET my posts
    @GetMapping("/my-posts")
    public ResponseEntity<Page<PostResponse>> getMyPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) co.istad.blogapplication.blog.entity.Post.PostStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getMyPosts(userDetails.getUsername(), status, pageable));
    }

    // GET most liked
    @GetMapping("/most-liked")
    public ResponseEntity<Page<PostResponse>> getMostLikedPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getMostLikedPosts(pageable));
    }

    // GET most viewed
    @GetMapping("/most-viewed")
    public ResponseEntity<Page<PostResponse>> getMostViewedPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getMostViewedPosts(pageable));
    }

    // GET by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostResponse>> getPostsByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, pageable));
    }

    // GET by tag
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<PostResponse>> getPostsByTag(
            @PathVariable UUID tagId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByTag(tagId, pageable));
    }
}
