package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.entity.*;
import co.istad.blogapplication.blog.repository.*;
import co.istad.blogapplication.blog.service.LikeBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeBookmarkServiceImpl implements LikeBookmarkService {

    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public boolean togglePostLike(Long postId, String email) {
        User user = getUser(email);
        Post post = getPost(postId);

        return postLikeRepository.findByUserAndPost(user, post)
                .map(like -> { postLikeRepository.delete(like); return false; })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.builder().user(user).post(post).build());
                    return true;
                });
    }

    @Override
    @Transactional
    public boolean toggleBookmark(Long postId, String email) {
        User user = getUser(email);
        Post post = getPost(postId);

        return bookmarkRepository.findByUserAndPost(user, post)
                .map(bm -> { bookmarkRepository.delete(bm); return false; })
                .orElseGet(() -> {
                    bookmarkRepository.save(Bookmark.builder().user(user).post(post).build());
                    return true;
                });
    }

    @Override
    public boolean isPostLiked(Long postId, String email) {
        User user = getUser(email);
        Post post = getPost(postId);
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    @Override
    public boolean isBookmarked(Long postId, String email) {
        User user = getUser(email);
        Post post = getPost(postId);
        return bookmarkRepository.existsByUserAndPost(user, post);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")); // replaced AppException
    }

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found")); // replaced AppException
    }
}