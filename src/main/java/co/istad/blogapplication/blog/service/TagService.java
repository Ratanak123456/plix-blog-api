package co.istad.blogapplication.blog.service;


import co.istad.blogapplication.blog.dto.request.TagRequest;
import co.istad.blogapplication.blog.dto.response.TagResponse;

import java.util.List;

public interface TagService {
    TagResponse createTag(TagRequest request);
    TagResponse updateTag(Long id, TagRequest request);
    void deleteTag(Long id);
    List<TagResponse> getAllTags();
    TagResponse getTagById(Long id);
}