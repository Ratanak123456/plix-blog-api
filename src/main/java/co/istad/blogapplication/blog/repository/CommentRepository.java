package co.istad.blogapplication.blog.repository;

import co.istad.blogapplication.blog.entity.Comment;
import co.istad.blogapplication.blog.entity.Post;
import co.istad.blogapplication.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByPostAndParentIsNull(Post post, Pageable pageable);
    List<Comment> findByParentId(UUID parentId);
    long countByPost(Post post);
    long countByPostAndDeletedAtIsNull(Post post);
    long countByUser(User user);
}
