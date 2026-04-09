package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.request.PostRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    PostResponse createPost(PostRequest request, String username);
    PostResponse updatePost(UUID id, PostRequest request, String username);
    void deletePost(UUID id, String username);
    PostResponse getPostBySlug(String slug);
    PostResponse getPostById(UUID id);
    Page<PostResponse> getAllPublishedPosts(Pageable pageable);
    Page<PostResponse> getMyPosts(String username, Pageable pageable);
    Page<PostResponse> getPostsByCategory(UUID categoryId, Pageable pageable);
    Page<PostResponse> getPostsByTag(UUID tagId, Pageable pageable);
    Page<PostResponse> getMostLikedPosts(Pageable pageable);
}
