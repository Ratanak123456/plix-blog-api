package co.istad.blogapplication.blog.repository;

import co.istad.blogapplication.blog.entity.Post;
import co.istad.blogapplication.blog.entity.PostLike;
import co.istad.blogapplication.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    Page<PostLike> findByPostAndUserIsDeletedFalse(Post post, Pageable pageable);
    long countByPost(Post post);
    long countByUser(User user);
}
