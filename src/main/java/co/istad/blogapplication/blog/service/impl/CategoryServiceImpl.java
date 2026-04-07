package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.CategoryRequest;
import co.istad.blogapplication.blog.dto.response.CategoryResponse;
import co.istad.blogapplication.blog.entity.Category;
import co.istad.blogapplication.blog.repository.CategoryRepository;
import co.istad.blogapplication.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists"); // temporary replacement
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(toSlug(request.getName())) // temporary slug function
                .description(request.getDescription())
                .build();

        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found")); // temp replacement

        category.setName(request.getName());
        category.setSlug(toSlug(request.getName()));
        category.setDescription(request.getDescription());

        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found")); // temp replacement
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> modelMapper.map(c, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(c -> modelMapper.map(c, CategoryResponse.class))
                .orElseThrow(() -> new RuntimeException("Category not found")); // temp replacement
    }

    // Temporary simple slug generator
    private String toSlug(String input) {
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\w\\-]", "").toLowerCase(Locale.ROOT);
    }
}