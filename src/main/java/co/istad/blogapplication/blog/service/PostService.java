package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.request.PostRequest;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostResponse createPost(PostRequest request, String email);
    PostResponse updatePost(Long id, PostRequest request, String email);
    void deletePost(Long id, String email);
    PostResponse getPostBySlug(String slug);
    PostResponse getPostById(Long id);
    Page<PostResponse> getAllPublishedPosts(Pageable pageable);
    Page<PostResponse> getMyPosts(String email, Pageable pageable);
    Page<PostResponse> searchPosts(String keyword, Pageable pageable);
    Page<PostResponse> getPostsByCategory(Long categoryId, Pageable pageable);
    Page<PostResponse> getPostsByTag(Long tagId, Pageable pageable);
    Page<PostResponse> getMostLikedPosts(Pageable pageable);
    PostResponse publishPost(Long id, String email);
    PostResponse unpublishPost(Long id, String email);
}
