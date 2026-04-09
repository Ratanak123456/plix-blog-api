package co.istad.blogapplication.blog.service.impl;

import co.istad.blogapplication.blog.dto.request.CategoryRequest;
import co.istad.blogapplication.blog.dto.response.CategoryResponse;
import co.istad.blogapplication.blog.entity.Category;
import co.istad.blogapplication.blog.exception.BadRequestException;
import co.istad.blogapplication.blog.exception.ConflictException;
import co.istad.blogapplication.blog.exception.NotFoundException;
import co.istad.blogapplication.blog.repository.CategoryRepository;
import co.istad.blogapplication.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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
            throw new ConflictException("Category already exists");
        }

        String slug = toSlug(request.getName());
        if (slug.isBlank()) {
            throw new BadRequestException("Category name must contain letters or numbers");
        }
        if (categoryRepository.existsBySlug(slug)) {
            throw new ConflictException("Category slug already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .build();

        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        String slug = toSlug(request.getName());
        if (slug.isBlank()) {
            throw new BadRequestException("Category name must contain letters or numbers");
        }
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Category already exists");
        }
        categoryRepository.findBySlug(slug)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Category slug already exists");
                });

        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());

        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> modelMapper.map(c, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(c -> modelMapper.map(c, CategoryResponse.class))
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    private String toSlug(String input) {
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\w\\-]", "").toLowerCase(Locale.ROOT);
    }
}
