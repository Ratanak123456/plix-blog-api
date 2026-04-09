package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.*;
import co.istad.blogapplication.blog.exception.NotFoundException;
import co.istad.blogapplication.blog.repository.*;
import co.istad.blogapplication.blog.service.LikeBookmarkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeBookmarkServiceImpl implements LikeBookmarkService {

    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public boolean togglePostLike(UUID postId, String username) {
        User user = getUserByUsername(username);
        Post post = getPost(postId);

        return postLikeRepository.findByUserAndPost(user, post)
                .map(like -> {
                    postLikeRepository.delete(like);
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                    postRepository.save(post);
                    return false;
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.builder().user(user).post(post).build());
                    post.setLikeCount(post.getLikeCount() + 1);
                    postRepository.save(post);
                    return true;
                });
    }

    @Override
    @Transactional
    public boolean toggleBookmark(UUID postId, String username) {
        User user = getUserByUsername(username);
        Post post = getPost(postId);

        return bookmarkRepository.findByUserAndPost(user, post)
                .map(bm -> {
                    bookmarkRepository.delete(bm);
                    post.setBookmarkCount(Math.max(0, post.getBookmarkCount() - 1));
                    postRepository.save(post);
                    return false;
                })
                .orElseGet(() -> {
                    bookmarkRepository.save(Bookmark.builder().user(user).post(post).build());
                    post.setBookmarkCount(post.getBookmarkCount() + 1);
                    postRepository.save(post);
                    return true;
                });
    }

    @Override
    public boolean isPostLiked(UUID postId, String username) {
        User user = getUserByUsername(username);
        Post post = getPost(postId);
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    @Override
    public boolean isBookmarked(UUID postId, String username) {
        User user = getUserByUsername(username);
        Post post = getPost(postId);
        return bookmarkRepository.existsByUserAndPost(user, post);
    }

    @Override
    public Page<UserResponse> getPostLikes(UUID postId, Pageable pageable) {
        Post post = getPost(postId);
        return postLikeRepository.findByPostAndUserIsDeletedFalse(post, pageable)
                .map(postLike -> modelMapper.map(postLike.getUser(), UserResponse.class));
    }

    @Override
    public Post getPostSummary(UUID postId) {
        return getPost(postId);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Post getPost(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
    }
}
