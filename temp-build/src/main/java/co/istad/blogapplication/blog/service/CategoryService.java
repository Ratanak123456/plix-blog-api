package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.request.CategoryRequest;
import co.istad.blogapplication.blog.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(UUID id, CategoryRequest request);
    void deleteCategory(UUID id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(UUID id);
}