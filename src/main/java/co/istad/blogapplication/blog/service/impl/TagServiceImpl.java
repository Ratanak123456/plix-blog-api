package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.TagRequest;
import co.istad.blogapplication.blog.dto.response.TagResponse;
import co.istad.blogapplication.blog.entity.Tag;
import co.istad.blogapplication.blog.entity.User;
import co.istad.blogapplication.blog.exception.ConflictException;
import co.istad.blogapplication.blog.exception.ForbiddenException;
import co.istad.blogapplication.blog.exception.NotFoundException;
import co.istad.blogapplication.blog.repository.TagRepository;
import co.istad.blogapplication.blog.repository.UserRepository;
import co.istad.blogapplication.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TagResponse createTag(TagRequest request, String username) {
        if (tagRepository.existsByName(request.getName())) {
            throw new ConflictException("Tag already exists");
        }
        User user = getUserByUsername(username);
        Tag tag = Tag.builder()
                .name(request.getName())
                .slug(request.getName().toLowerCase().replace(" ", "-"))
                .createdBy(user)
                .build();
        return mapToResponse(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void deleteTag(UUID id, String username) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found"));
        User user = getUserByUsername(username);
        if (!tag.getCreatedBy().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("You are not authorized to delete this tag");
        }
        tagRepository.delete(tag);
    }

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TagResponse getTagById(UUID id) {
        return tagRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Tag not found"));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private TagResponse mapToResponse(Tag tag) {
        TagResponse response = modelMapper.map(tag, TagResponse.class);
        response.setPostCount(tag.getPosts().size());
        return response;
    }
}
