package co.istad.blogapplication.blog.repository;

import co.istad.blogapplication.blog.entity.Post;
import co.istad.blogapplication.blog.entity.Post.PostStatus;
import co.istad.blogapplication.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> findBySlug(String slug);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.slug = :slug")
    int incrementViewCountBySlug(@Param("slug") String slug);

    Page<Post> findByStatusAndDeletedAtIsNull(PostStatus status, Pageable pageable);

    Page<Post> findByAuthorAndStatusAndDeletedAtIsNull(User author, PostStatus status, Pageable pageable);

    Page<Post> findByAuthorAndDeletedAtIsNull(User author, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.category.id = :categoryId")
    Page<Post> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.status = 'PUBLISHED' AND t.id = :tagId")
    Page<Post> findByTagId(@Param("tagId") UUID tagId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.likes l WHERE p.status = 'PUBLISHED' GROUP BY p ORDER BY COUNT(l) DESC")
    Page<Post> findMostLiked(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' ORDER BY p.viewCount DESC")
    Page<Post> findMostViewed(Pageable pageable);

    long countByCategoryIdAndStatusAndDeletedAtIsNull(UUID categoryId, PostStatus status);

    long countByAuthor(User author);
}
