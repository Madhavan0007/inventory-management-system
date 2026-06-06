package com.virtusa.product_service.service;

import com.virtusa.product_service.dto.ProductRequest;
import com.virtusa.product_service.dto.ProductResponse;
import com.virtusa.product_service.entity.Category;
import com.virtusa.product_service.entity.Product;
import com.virtusa.product_service.exception.CategoryNotFoundException;
import com.virtusa.product_service.exception.ProductNotFoundException;
import com.virtusa.product_service.repository.CategoryRepository;
import com.virtusa.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .price(request.getPrice())
                .description(request.getDescription())
                .category(category)
                .build();

        return productToDTO(productRepository.save(product));
    }

    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::productToDTO)
                .toList();
    }

    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        ));

        return productToDTO(product);
    }

    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        ));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found"
                        ));

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return productToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        ));

        productRepository.delete(product);
    }

    private ProductResponse productToDTO(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .categoryName(product.getCategory().getName())
                .build();
    }
}