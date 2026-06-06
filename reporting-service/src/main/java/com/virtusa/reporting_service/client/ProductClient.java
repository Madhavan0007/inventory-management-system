package com.virtusa.reporting_service.client;


import com.virtusa.reporting_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/*
 * Calls Product Service
 *
 * Later:
 * localhost URL can become
 * Docker hostname
 * or service discovery.
 */

@FeignClient(
        name = "product-service",
        url = "${services.product.url}"
)
public interface ProductClient {

    @GetMapping("/products")
    List<ProductDto> getProducts();
}