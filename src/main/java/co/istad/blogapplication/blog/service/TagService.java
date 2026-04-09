package co.istad.blogapplication.blog.service;


import co.istad.blogapplication.blog.dto.request.TagRequest;
import co.istad.blogapplication.blog.dto.response.TagResponse;

import java.util.List;
import java.util.UUID;

public interface TagService {
    TagResponse createTag(TagRequest request, String username);
    void deleteTag(UUID id, String username);
    List<TagResponse> getAllTags();
    TagResponse getTagById(UUID id);
}
