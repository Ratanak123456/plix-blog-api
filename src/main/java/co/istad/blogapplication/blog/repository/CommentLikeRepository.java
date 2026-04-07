package co.istad.blogapplication.blog.repository;

import co.istad.blogapplication.blog.entity.Comment;
import co.istad.blogapplication.blog.entity.CommentLike;
import co.istad.blogapplication.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
    boolean existsByUserAndComment(User user, Comment comment);
    long countByComment(Comment comment);
}
