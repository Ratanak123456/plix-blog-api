package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.response.DashboardResponse;
import co.istad.blogapplication.blog.dto.response.PostResponse;
import co.istad.blogapplication.blog.entity.Post.PostStatus;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.repository.*;
import co.istad.blogapplication.blog.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ModelMapper modelMapper;

    @Override
    public DashboardResponse getMyDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")); // replaced AppException

        long totalPosts = postRepository.countByAuthor(user);
        long totalLikes = postLikeRepository.countByUser(user);
        long totalComments = commentRepository.countByUser(user);

        List<PostResponse> recentPosts = postRepository
                .findByAuthorAndStatus(user, PostStatus.PUBLISHED,
                        PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "publishedAt")))
                .stream()
                .map(p -> modelMapper.map(p, PostResponse.class))
                .collect(Collectors.toList());

        List<PostResponse> mostLikedPosts = postRepository
                .findMostLiked(PageRequest.of(0, 5))
                .stream()
                .map(p -> modelMapper.map(p, PostResponse.class))
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalPosts(totalPosts)
                .totalLikes(totalLikes)
                .totalComments(totalComments)
                .recentPosts(recentPosts)
                .mostLikedPosts(mostLikedPosts)
                .build();
    }
}