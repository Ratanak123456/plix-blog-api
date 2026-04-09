package co.istad.blogapplication.blog.service;


import co.istad.blogapplication.blog.dto.request.CommentRequest;
import co.istad.blogapplication.blog.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    CommentResponse addComment(UUID postId, CommentRequest request, String username);
    CommentResponse updateComment(UUID commentId, CommentRequest request, String username);
    void deleteComment(UUID commentId, String username);
    Page<CommentResponse> getCommentsByPost(UUID postId, Pageable pageable);
    CommentResponse likeComment(UUID commentId, String username);
}
