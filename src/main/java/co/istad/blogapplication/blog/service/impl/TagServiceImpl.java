package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.TagRequest;
import co.istad.blogapplication.blog.dto.response.TagResponse;
import co.istad.blogapplication.blog.entity.Tag;
import co.istad.blogapplication.blog.repository.TagRepository;
import co.istad.blogapplication.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TagResponse createTag(TagRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tag already exists");
        }
        Tag tag = Tag.builder()
                .name(request.getName())
                .slug(request.getName().toLowerCase().replace(" ", "-")) // simple slug
                .build();
        return modelMapper.map(tagRepository.save(tag), TagResponse.class);
    }

    @Override
    @Transactional
    public TagResponse updateTag(Long id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setName(request.getName());
        tag.setSlug(request.getName().toLowerCase().replace(" ", "-"));
        return modelMapper.map(tagRepository.save(tag), TagResponse.class);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        tagRepository.delete(tag);
    }

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(t -> modelMapper.map(t, TagResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public TagResponse getTagById(Long id) {
        return tagRepository.findById(id)
                .map(t -> modelMapper.map(t, TagResponse.class))
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }
}