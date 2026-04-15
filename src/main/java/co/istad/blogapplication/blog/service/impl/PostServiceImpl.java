package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.PostRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.entity.*;
import co.istad.blogapplication.blog.entity.Post.PostStatus;
import co.istad.blogapplication.blog.exception.ForbiddenException;
import co.istad.blogapplication.blog.exception.NotFoundException;
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
    public PostResponse createPost(PostRequest request, String username) {
        User author = getUserByUsername(username);

        String slug = generateUniqueSlug(request.getTitle(), null);

        Post post = Post.builder()
                .title(request.getTitle())
                .slug(slug)
                .content(request.getContent())
                .thumbnailUrl(request.getThumbnail())
                .status(request.getStatus() != null ? request.getStatus() : PostStatus.DRAFT)
                .author(author)
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .bookmarkCount(0)
                .build();

        if (request.getStatus() == PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            post.setCategory(category);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            post.setTags(tags);
        }

        Post saved = postRepository.save(post);
        return mapToResponse(saved, username);
    }

    @Override
    @Transactional
    public PostResponse updatePost(UUID id, PostRequest request, String username) {
        Post post = getPostAndVerifyOwner(id, username);

        post.setTitle(request.getTitle());
        post.setSlug(generateUniqueSlug(request.getTitle(), post.getId()));
        post.setContent(request.getContent());
        post.setThumbnailUrl(request.getThumbnail());

        if (request.getStatus() != null) {
            if (request.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
            post.setStatus(request.getStatus());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }

        post.setTags(resolveTags(request.getTagIds()));

        Post updated = postRepository.save(post);
        return mapToResponse(updated, username);
    }

    @Override
    @Transactional
    public void deletePost(UUID id, String username) {
        Post post = getPostAndVerifyOwner(id, username);
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    @Transactional
    public PostResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        postRepository.incrementViewCountBySlug(slug);
        post.setViewCount(post.getViewCount() + 1);

        return mapToResponse(post, null);
    }

    @Override
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        return mapToResponse(post, null);
    }

    @Override
    public Page<PostResponse> getAllPublishedPosts(Pageable pageable) {
        return postRepository.findByStatus(PostStatus.PUBLISHED, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getMyPosts(String username, Pageable pageable) {
        User user = getUserByUsername(username);
        return postRepository.findByAuthor(user, pageable)
                .map(post -> mapToResponse(post, username));
    }

    @Override
    public Page<PostResponse> getPostsByCategory(UUID categoryId, Pageable pageable) {
        return postRepository.findByCategoryId(categoryId, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getPostsByTag(UUID tagId, Pageable pageable) {
        return postRepository.findByTagId(tagId, pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getMostLikedPosts(Pageable pageable) {
        return postRepository.findMostLiked(pageable)
                .map(post -> mapToResponse(post, null));
    }

    @Override
    public Page<PostResponse> getMostViewedPosts(Pageable pageable) {
        return postRepository.findMostViewed(pageable)
                .map(post -> mapToResponse(post, null));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Post getPostAndVerifyOwner(UUID id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        User user = getUserByUsername(username);
        if (!post.getAuthor().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("You are not authorized to modify this post");
        }
        return post;
    }

    private String generateUniqueSlug(String title, UUID currentPostId) {
        String base = title.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9\\-]", "");
        String slug = base;
        int count = 1;
        while (postRepository.findBySlug(slug)
                .filter(existingPost -> !existingPost.getId().equals(currentPostId))
                .isPresent()) {
            slug = base + "-" + count++;
        }
        return slug;
    }

    private List<Tag> resolveTags(List<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        return tagRepository.findAllById(tagIds);
    }

    private PostResponse mapToResponse(Post post, String currentUsername) {
        PostResponse response = modelMapper.map(post, PostResponse.class);

        // Read live counts so the API is correct even if persisted counters drift.
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(Math.toIntExact(commentRepository.countByPostAndDeletedAtIsNull(post)));
        response.setViewCount(post.getViewCount());
        response.setBookmarkCount(post.getBookmarkCount());

        if (currentUsername != null) {
            userRepository.findByUsernameAndIsDeletedFalse(currentUsername).ifPresent(user -> {
                response.setLikedByCurrentUser(postLikeRepository.existsByUserAndPost(user, post));
                response.setBookmarkedByCurrentUser(bookmarkRepository.existsByUserAndPost(user, post));
            });
        }
        return response;
    }
}
