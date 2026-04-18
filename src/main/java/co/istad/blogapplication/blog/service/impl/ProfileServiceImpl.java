package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.PasswordRequest;
import co.istad.blogapplication.blog.dto.request.ProfileRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.exception.BadRequestException;
import co.istad.blogapplication.blog.exception.ConflictException;
import co.istad.blogapplication.blog.exception.NotFoundException;
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
    public UserResponse getMyProfile(String username) {
        User user = findUserByUsername(username);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String username, ProfileRequest request) {
        User user = findUserByUsername(username);
        if (request.getUsername() != null
                && !request.getUsername().equalsIgnoreCase(user.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());
        if (request.getCoverImage() != null) user.setCoverImage(request.getCoverImage());
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    @Transactional
    public void changePassword(String username, PasswordRequest request) {
        User user = findUserByUsername(username);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Page<PostResponse> getMyBookmarks(String username, Pageable pageable) {
        User user = findUserByUsername(username);
        return bookmarkRepository.findByUser(user, pageable)
                .map(bookmark -> postService.getPostById(bookmark.getPost().getId()));
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public Page<PostResponse> getUserPosts(String username, Post.PostStatus status, Pageable pageable) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return postService.getMyPosts(username, status, pageable);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
