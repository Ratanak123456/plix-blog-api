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

    // HEAD — check endpoint availability
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> headPosts() {
        return ResponseEntity.ok().build();
    }

    // OPTIONS
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> optionsPosts() {
        return ResponseEntity.ok()
                .header("Allow", "GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS")
                .build();
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

    // HEAD by slug — check if post exists
    @RequestMapping(value = "/{slug}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headPost(@PathVariable String slug) {
        postService.getPostBySlug(slug);
        return ResponseEntity.ok().build();
    }

    // PUT — full update
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.updatePost(id, request, userDetails.getUsername()));
    }

    // PATCH — partial update (status, thumbnail, etc.)
    @PatchMapping("/{id}")
    public ResponseEntity<PostResponse> patchPost(
            @PathVariable Long id,
            @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.updatePost(id, request, userDetails.getUsername()));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // PATCH publish/unpublish
    @PatchMapping("/{id}/publish")
    public ResponseEntity<PostResponse> publishPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.publishPost(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<PostResponse> unpublishPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.unpublishPost(id, userDetails.getUsername()));
    }

    // GET my posts
    @GetMapping("/my-posts")
    public ResponseEntity<Page<PostResponse>> getMyPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getMyPosts(userDetails.getUsername(), pageable));
    }

    // GET search
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(keyword, pageable));
    }

    // GET most liked
    @GetMapping("/most-liked")
    public ResponseEntity<Page<PostResponse>> getMostLikedPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getMostLikedPosts(pageable));
    }

    // GET by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostResponse>> getPostsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, pageable));
    }

    // GET by tag
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<PostResponse>> getPostsByTag(
            @PathVariable Long tagId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByTag(tagId, pageable));
    }
}
