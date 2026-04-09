package co.istad.blogapplication.blog.repository;

import co.istad.blogapplication.blog.entity.Bookmark;
import co.istad.blogapplication.blog.entity.Post;
import co.istad.blogapplication.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    Optional<Bookmark> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    Page<Bookmark> findByUser(User user, Pageable pageable);
}