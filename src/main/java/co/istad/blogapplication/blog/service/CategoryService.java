package co.istad.blogapplication.blog.service;

import co.istad.blogapplication.blog.dto.request.CategoryRequest;
import co.istad.blogapplication.blog.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
}