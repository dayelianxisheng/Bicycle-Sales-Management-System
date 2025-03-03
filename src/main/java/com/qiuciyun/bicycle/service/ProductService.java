package com.qiuciyun.bicycle.service;

import com.qiuciyun.bicycle.entity.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<String, Object> getAllProducts(int page, int size, String keyword, Long categoryId, Integer status);
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long id);
} 