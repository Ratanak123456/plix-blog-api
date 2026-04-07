package co.istad.blogapplication.blog.repository;

import co.istad.blogapplication.blog.entity.Post;
import co.istad.blogapplication.blog.entity.PostLike;
import co.istad.blogapplication.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    long countByPost(Post post);
    long countByUser(User user);
}
