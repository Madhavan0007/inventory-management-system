package com.virtusa.product_service.service;

import com.virtusa.product_service.dto.CategoryRequest;
import com.virtusa.product_service.entity.Category;
import com.virtusa.product_service.exception.CategoryDeletionException;
import com.virtusa.product_service.exception.CategoryNotFoundException;
import com.virtusa.product_service.repository.CategoryRepository;
import com.virtusa.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Category createCategory(CategoryRequest request) {

        Category category = Category.builder()
                .name(request.getName())
                .build();

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + id
                        ));
    }

    public Category updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + id
                        ));

        category.setName(request.getName());

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + id
                        ));

        if (productRepository.existsByCategory(category)) {
            throw new CategoryDeletionException(
                    "Cannot delete category because products are linked to it."
            );
        }

        categoryRepository.delete(category);
    }
}