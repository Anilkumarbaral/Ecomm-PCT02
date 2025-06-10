package com.example.product.service.impl;

import com.example.product.dto.request.CategoryRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.model.Category;
import com.example.product.repository.CategoryRepository;
import com.example.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(request.getSlug())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        if (request.getParentId() != null) {
            category.setParent(Category.builder().id(request.getParentId()).build());
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSlug(request.getSlug());
        category.setIsActive(request.getIsActive());

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse res = new CategoryResponse();
        res.setId(category.getId());
        res.setName(category.getName());
        res.setDescription(category.getDescription());
        res.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        res.setSlug(category.getSlug());
        res.setIsActive(category.getIsActive());
        res.setCreatedAt(category.getCreatedAt().toString());
        res.setUpdatedAt(category.getUpdatedAt().toString());
        return res;
    }
}

