package co.istad.blogapplication.blog.service;

public interface LikeBookmarkService {
    boolean togglePostLike(Long postId, String email);
    boolean toggleBookmark(Long postId, String email);
    boolean isPostLiked(Long postId, String email);
    boolean isBookmarked(Long postId, String email);
}