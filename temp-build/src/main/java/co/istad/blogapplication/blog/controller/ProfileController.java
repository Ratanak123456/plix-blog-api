package co.istad.blogapplication.blog.controller;

import co.istad.blogapplication.blog.dto.request.PasswordRequest;
import co.istad.blogapplication.blog.dto.request.ProfileRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.dto.response.ApiResponse;
import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(profileService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody ProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(profileService.updateProfile(userDetails.getUsername(), request));
    }

    @PatchMapping("/profile/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody PasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        profileService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(new ApiResponse("Password changed successfully"));
    }

    @GetMapping("/profile/bookmarks")
    public ResponseEntity<Page<PostResponse>> getMyBookmarks(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(profileService.getMyBookmarks(userDetails.getUsername(), pageable));
    }

    @GetMapping("/profiles/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getUserByUsername(username));
    }

    @GetMapping("/profiles/{username}/posts")
    public ResponseEntity<Page<PostResponse>> getUserPosts(
            @PathVariable String username,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(profileService.getUserPosts(username, pageable));
    }
}
