package com.inventory.client;

import com.inventory.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductClient(RestTemplate restTemplate,
                         @Value("${product-service.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    public void ensureProductExists(Long productId) {
        getProduct(productId);
    }

    public ProductDetails getProduct(Long productId) {
        try {
            return restTemplate.getForObject(productServiceUrl + "/products/{id}", ProductDetails.class, productId);
        } catch (HttpStatusCodeException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status.is4xxClientError()) {
                throw new ProductNotFoundException("Product not found with id: " + productId);
            }
            throw ex;
        } catch (RestClientException ex) {
            throw new IllegalStateException("Product service is unavailable", ex);
        }
    }
}
