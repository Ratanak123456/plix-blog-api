package co.istad.blogapplication.blog.repository;

import co.istad.blogapplication.blog.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revokedAt = CURRENT_TIMESTAMP WHERE r.user.id = :userId AND r.revokedAt IS NULL")
    int revokeAllUserTokens(UUID userId);

    @Query("SELECT r FROM RefreshToken r WHERE r.tokenHash = :token AND r.expiresAt > CURRENT_TIMESTAMP AND r.revokedAt IS NULL")
    Optional<RefreshToken> findValidToken(String tokenHash);
}