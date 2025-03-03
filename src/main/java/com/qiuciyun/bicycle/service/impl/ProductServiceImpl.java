package com.qiuciyun.bicycle.service.impl;

import com.qiuciyun.bicycle.entity.Product;
import com.qiuciyun.bicycle.mapper.ProductMapper;
import com.qiuciyun.bicycle.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Map<String, Object> getAllProducts(int page, int size, String keyword, Long categoryId, Integer status) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 获取总记录数
        long total = productMapper.count(keyword, categoryId, status);
        
        // 获取分页数据
        List<Product> records = productMapper.findPage(offset, size, keyword, categoryId, status);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("size", size);
        result.put("current", page);
        result.put("pages", (total + size - 1) / size);
        
        return result;
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }

    @Override
    public Product createProduct(Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product updateProduct(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        productMapper.update(product);
        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.delete(id, LocalDateTime.now());
    }
} 