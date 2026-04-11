package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.config.AppConfig;
import co.istad.blogapplication.blog.dto.request.PostRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.entity.Post;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.repository.BookmarkRepository;
import co.istad.blogapplication.blog.repository.CategoryRepository;
import co.istad.blogapplication.blog.repository.CommentRepository;
import co.istad.blogapplication.blog.repository.PostLikeRepository;
import co.istad.blogapplication.blog.repository.PostRepository;
import co.istad.blogapplication.blog.repository.TagRepository;
import co.istad.blogapplication.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private CommentRepository commentRepository;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(
                postRepository,
                userRepository,
                categoryRepository,
                tagRepository,
                postLikeRepository,
                bookmarkRepository,
                commentRepository,
                new AppConfig().modelMapper()
        );
    }

    @Test
    void updatePostReplacesEditableFields() {
        UUID authorId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        User author = User.builder()
                .id(authorId)
                .email("author@example.com")
                .username("author")
                .fullName("Author")
                .role(User.Role.AUTHOR)
                .isActive(true)
                .isDeleted(false)
                .build();

        Post post = Post.builder()
                .id(postId)
                .title("Original Title")
                .slug("original-title")
                .content("original content for reading time")
                .thumbnailUrl("old-thumb.png")
                .author(author)
                .tags(List.of())
                .status(Post.PostStatus.DRAFT)
                .build();

        PostRequest request = new PostRequest();
        request.setTitle("Updated Title");
        request.setContent("updated content for reading time");
        request.setThumbnail("new-thumb.png");
        request.setStatus(Post.PostStatus.PUBLISHED);

        when(userRepository.findByUsernameAndIsDeletedFalse("author")).thenReturn(Optional.of(author));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookmarkRepository.existsByUserAndPost(any(User.class), any(Post.class))).thenReturn(false);
        when(postLikeRepository.existsByUserAndPost(any(User.class), any(Post.class))).thenReturn(false);

        PostResponse response = postService.updatePost(postId, request, "author");

        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals("Updated Title", savedPost.getTitle());
        assertEquals("updated content for reading time", savedPost.getContent());
        assertEquals("new-thumb.png", savedPost.getThumbnailUrl());
        assertEquals("updated-title", savedPost.getSlug());
        assertEquals(Post.PostStatus.PUBLISHED, savedPost.getStatus());
        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        assertEquals("new-thumb.png", response.getThumbnailUrl());
    }

    @Test
    void getPostBySlugIncrementsViewCount() {
        User author = User.builder()
                .id(UUID.randomUUID())
                .email("author@example.com")
                .username("author")
                .fullName("Author")
                .role(User.Role.AUTHOR)
                .isActive(true)
                .isDeleted(false)
                .build();

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .title("View Test")
                .slug("view-test")
                .content("content")
                .author(author)
                .viewCount(3)
                .build();

        when(postRepository.findBySlug("view-test")).thenReturn(Optional.of(post));
        when(postRepository.incrementViewCountBySlug("view-test")).thenReturn(1);

        PostResponse response = postService.getPostBySlug("view-test");

        verify(postRepository).incrementViewCountBySlug("view-test");
        verify(postRepository, times(1)).findBySlug("view-test");
        assertEquals(4, post.getViewCount());
        assertEquals(4, response.getViewCount());
    }
}
