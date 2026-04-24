package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.CommentRequest;
import co.istad.blogapplication.blog.dto.response.CommentResponse;
import co.istad.blogapplication.blog.entity.*;
import co.istad.blogapplication.blog.exception.ForbiddenException;
import co.istad.blogapplication.blog.exception.NotFoundException;
import co.istad.blogapplication.blog.repository.*;
import co.istad.blogapplication.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CommentResponse addComment(UUID postId, CommentRequest request, String username) {
        User user = getUser(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .build();

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found"));
            comment.setParent(parent);
        }

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved, username);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID commentId, CommentRequest request, String username) {
        User user = getUser(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("You are not authorized to update this comment");
        }

        comment.setContent(request.getContent());
        return mapToResponse(commentRepository.save(comment), username);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId, String username) {
        User user = getUser(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("You are not authorized to delete this comment");
        }

        // Soft delete to trigger trg_comments_sync in DB
        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    public Page<CommentResponse> getCommentsByPost(UUID postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        return commentRepository.findByPostAndParentIsNull(post, pageable)
                .map(comment -> mapToResponse(comment, null));
    }

    @Override
    @Transactional
    public CommentResponse likeComment(UUID commentId, String username) {
        User user = getUser(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        commentLikeRepository.findByUserAndComment(user, comment).ifPresentOrElse(
                like -> commentLikeRepository.delete(like),
                () -> commentLikeRepository.save(
                        CommentLike.builder().user(user).comment(comment).build()
                )
        );

        return mapToResponse(comment, username);
    }

    private User getUser(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private CommentResponse mapToResponse(Comment comment, String currentUsername) {
        CommentResponse response = modelMapper.map(comment, CommentResponse.class);
        response.setLikeCount(commentLikeRepository.countByComment(comment));

        if (comment.getParent() != null) {
            response.setParentId(comment.getParent().getId());
        }

        List<CommentResponse> replies = commentRepository.findByParentId(comment.getId())
                .stream()
                .map(reply -> mapToResponse(reply, currentUsername))
                .collect(Collectors.toList());
        response.setReplies(replies);

        if (currentUsername != null) {
            userRepository.findByUsernameAndIsDeletedFalse(currentUsername).ifPresent(user ->
                    response.setLikedByCurrentUser(commentLikeRepository.existsByUserAndComment(user, comment))
            );
        }
        return response;
    }
}
