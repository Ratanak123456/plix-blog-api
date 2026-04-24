package co.istad.blogapplication.blog.controller;

import co.istad.blogapplication.blog.dto.request.TagRequest;
import co.istad.blogapplication.blog.dto.response.TagResponse;
import co.istad.blogapplication.blog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable UUID id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
            @Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.createTag(request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        tagService.deleteTag(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
