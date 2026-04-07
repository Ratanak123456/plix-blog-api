package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.PostRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.entity.*;
import co.istad.blogapplication.blog.entity.Post.PostStatus;
import co.istad.blogapplication.blog.repository.*;
import co.istad.blogapplication.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, String email) {
        User author = getUser(email);

        String slug = generateUniqueSlug(request.getTitle());

        Post post = Post.builder()
                .title(request.getTitle())
                .slug(slug)
                .content(request.getContent())
                .thumbnail(request.getThumbnail())
                .status(request.getStatus() != null ? request.getStatus() : PostStatus.DRAFT)
                .scheduledAt(request.getScheduledAt())
                .readingTime(calculateReadingTime(request.getContent())) // simple stub
                .author(author)
                .build();

        if (request.getStatus() == PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategory(category);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            post.setTags(tags);
        }

        Post saved = postRepository.save(post);
        return mapToResponse(saved, email);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostRequest request, String email) {
        Post post = getPostAndVerifyOwner(id, email);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setThumbnail(request.getThumbnail());
        post.setReadingTime(calculateReadingTime(request.getContent()));

        if (request.getStatus() != null) {
            if (request.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
            post.setStatus(request.getStatus());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategory(category);
        }

        if (request.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            post.setTags(tags);
        }

        Post updated = postRepository.save(post);
        return mapToResponse(updated, email);
    }

    @Override
    @Transactional
    public void deletePost(Long id, String email) {
        Post post = getPostAndVerifyOwner(id, email);
        postRepository.delete(post);
    }

    @Override
    public PostResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponse(post, null);
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponse(post, null);
    }

    @Override
    public Page<PostResponse> getAllPublishedPosts(Pageable pageable) {
        return postRepository.findByStatus(PostStatus.PUBLISHED, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getMyPosts(String email, Pageable pageable) {
        User user = getUser(email);
        return postRepository.findByAuthor(user, pageable)
                .map(post -> mapToResponse(post, email));
    }

    @Override
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getPostsByCategory(Long categoryId, Pageable pageable) {
        return postRepository.findByCategoryId(categoryId, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getPostsByTag(Long tagId, Pageable pageable) {
        return postRepository.findByTagId(tagId, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getMostLikedPosts(Pageable pageable) {
        return postRepository.findMostLiked(pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    @Transactional
    public PostResponse publishPost(Long id, String email) {
        Post post = getPostAndVerifyOwner(id, email);
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
        return mapToResponse(postRepository.save(post), email);
    }

    @Override
    @Transactional
    public PostResponse unpublishPost(Long id, String email) {
        Post post = getPostAndVerifyOwner(id, email);
        post.setStatus(PostStatus.DRAFT);
        return mapToResponse(postRepository.save(post), email);
    }

    // ─── Helpers ───────────────────────────────────────────────

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Post getPostAndVerifyOwner(Long id, String email) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = getUser(email);
        if (!post.getAuthor().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("You are not authorized to modify this post");
        }
        return post;
    }

    private String generateUniqueSlug(String title) {
        // Simple slug stub without SlugUtil
        String base = title.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9\\-]", "");
        String slug = base;
        int count = 1;
        while (postRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + count++;
        }
        return slug;
    }

    private int calculateReadingTime(String content) {
        // Simple stub: 1 minute per 200 words
        int words = content == null ? 0 : content.trim().split("\\s+").length;
        return Math.max(1, words / 200);
    }

    private PostResponse mapToResponse(Post post, String currentUserEmail) {
        PostResponse response = modelMapper.map(post, PostResponse.class);
        response.setLikeCount(postLikeRepository.countByPost(post));
        response.setCommentCount(commentRepository.countByPost(post));

        if (currentUserEmail != null) {
            userRepository.findByEmail(currentUserEmail).ifPresent(user -> {
                response.setLikedByCurrentUser(postLikeRepository.existsByUserAndPost(user, post));
                response.setBookmarkedByCurrentUser(bookmarkRepository.existsByUserAndPost(user, post));
            });
        }
        return response;
    }
}