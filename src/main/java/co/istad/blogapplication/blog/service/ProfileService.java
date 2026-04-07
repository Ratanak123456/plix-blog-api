package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.request.PasswordRequest;
import co.istad.blogapplication.blog.dto.request.ProfileRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProfileService {
    UserResponse getMyProfile(String email);
    UserResponse updateProfile(String email, ProfileRequest request);
    void changePassword(String email, PasswordRequest request);
    Page<PostResponse> getMyBookmarks(String email, Pageable pageable);
    UserResponse getUserByUsername(String username);
    Page<PostResponse> getUserPosts(String username, Pageable pageable);
}

