package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.PasswordRequest;
import co.istad.blogapplication.blog.dto.request.ProfileRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.repository.*;
import co.istad.blogapplication.blog.service.PostService;
import co.istad.blogapplication.blog.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostService postService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserResponse getMyProfile(String email) {
        User user = getUser(email);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, ProfileRequest request) {
        User user = getUser(email);
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    @Transactional
    public void changePassword(String email, PasswordRequest request) {
        User user = getUser(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Page<PostResponse> getMyBookmarks(String email, Pageable pageable) {
        User user = getUser(email);
        return bookmarkRepository.findByUser(user, pageable)
                .map(bookmark -> postService.getPostById(bookmark.getPost().getId()));
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public Page<PostResponse> getUserPosts(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postService.getMyPosts(user.getEmail(), pageable);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}