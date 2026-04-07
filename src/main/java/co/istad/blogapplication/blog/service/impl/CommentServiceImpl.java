package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.CommentRequest;
import co.istad.blogapplication.blog.dto.response.CommentResponse;
import co.istad.blogapplication.blog.entity.*;
import co.istad.blogapplication.blog.repository.*;
import co.istad.blogapplication.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public CommentResponse addComment(Long postId, CommentRequest request, String email) {
        User user = getUser(email);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .build();

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParent(parent);
        }

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved, email);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String email) {
        User user = getUser(email);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return commentRepository.findByPostAndParentIsNull(post, pageable)
                .map(comment -> mapToResponse(comment, null));
    }

    @Override
    @Transactional
    public CommentResponse likeComment(Long commentId, String email) {
        User user = getUser(email);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentLikeRepository.findByUserAndComment(user, comment).ifPresentOrElse(
                like -> commentLikeRepository.delete(like),
                () -> commentLikeRepository.save(
                        CommentLike.builder().user(user).comment(comment).build()
                )
        );

        return mapToResponse(comment, email);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CommentResponse mapToResponse(Comment comment, String currentUserEmail) {
        CommentResponse response = modelMapper.map(comment, CommentResponse.class);
        response.setLikeCount(commentLikeRepository.countByComment(comment));

        if (comment.getParent() != null) {
            response.setParentId(comment.getParent().getId());
        }

        List<CommentResponse> replies = commentRepository.findByParentId(comment.getId())
                .stream()
                .map(reply -> mapToResponse(reply, currentUserEmail))
                .collect(Collectors.toList());
        response.setReplies(replies);

        if (currentUserEmail != null) {
            userRepository.findByEmail(currentUserEmail).ifPresent(user ->
                    response.setLikedByCurrentUser(commentLikeRepository.existsByUserAndComment(user, comment))
            );
        }
        return response;
    }
}