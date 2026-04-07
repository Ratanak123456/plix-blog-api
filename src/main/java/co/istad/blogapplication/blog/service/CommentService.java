package co.istad.blogapplication.blog.service;


import co.istad.blogapplication.blog.dto.request.CommentRequest;
import co.istad.blogapplication.blog.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse addComment(Long postId, CommentRequest request, String email);
    void deleteComment(Long commentId, String email);
    Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable);
    CommentResponse likeComment(Long commentId, String email);
}
