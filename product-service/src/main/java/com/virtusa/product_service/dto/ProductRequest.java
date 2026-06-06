package com.virtusa.product_service.dto;

import lombok.Data;

@Data
public class ProductRequest {

    private String id;

    private String name;

    private String sku;

    private Double price;

    private Long categoryId;

    private String description;
}