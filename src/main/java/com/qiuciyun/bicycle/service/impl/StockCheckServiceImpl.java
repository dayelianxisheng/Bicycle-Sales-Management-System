package com.qiuciyun.bicycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiuciyun.bicycle.entity.StockCheck;
import com.qiuciyun.bicycle.entity.StockCheckItem;
import com.qiuciyun.bicycle.entity.Product;
import com.qiuciyun.bicycle.mapper.StockCheckMapper;
import com.qiuciyun.bicycle.mapper.StockCheckItemMapper;
import com.qiuciyun.bicycle.mapper.ProductMapper;
import com.qiuciyun.bicycle.service.StockCheckService;
import com.qiuciyun.bicycle.dto.StockCheckPrepareDTO;
import com.qiuciyun.bicycle.dto.StockCheckPrepareDTO.ProductStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockCheckServiceImpl implements StockCheckService {

    @Autowired
    private StockCheckMapper stockCheckMapper;

    @Autowired
    private StockCheckItemMapper stockCheckItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Map<String, Object> getStockCheckList(int page, int size, String status, String startTime, String endTime) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 获取分页数据
        List<StockCheck> records = stockCheckMapper.findPage(offset, size, status, startTime, endTime);
        
        // 获取总记录数
        Long total = stockCheckMapper.count(status, startTime, endTime);
        
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
    public StockCheck getStockCheckDetail(Long id) {
        return stockCheckMapper.selectById(id);
    }

    @Override
    public List<StockCheckItem> getStockCheckItems(Long checkId) {
        return stockCheckItemMapper.findByCheckId(checkId);
    }

    @Override
    @Transactional
    public StockCheck createStockCheck(String operator) {
        // 创建盘点单
        StockCheck stockCheck = new StockCheck();
        stockCheck.setCheckNo(generateCheckNo());
        stockCheck.setCheckTime(LocalDateTime.now());
        stockCheck.setOperator(operator);
        stockCheck.setStatus("PENDING");
        stockCheck.setTotalProducts(0);
        stockCheck.setDiffCount(0);
        stockCheck.setCreateTime(LocalDateTime.now());
        stockCheck.setUpdateTime(LocalDateTime.now());
        
        stockCheckMapper.insert(stockCheck);
        
        // 获取所有商品并创建盘点明细
        List<Product> products = productMapper.findAll();
        for (Product product : products) {
            StockCheckItem item = new StockCheckItem();
            item.setCheckId(stockCheck.getId());
            item.setProductId(product.getId());
            item.setSystemStock(product.getStock());
            item.setActualStock(0);
            item.setDiffQuantity(0);
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            
            stockCheckItemMapper.insert(item);
        }
        
        // 更新盘点单的商品总数
        stockCheck.setTotalProducts(products.size());
        stockCheckMapper.updateById(stockCheck);
        
        return stockCheck;
    }

    @Override
    @Transactional
    public void updateStockCheckItem(Long checkId, Long productId, Integer actualStock, String remark) {
        // 获取盘点单
        StockCheck stockCheck = stockCheckMapper.selectById(checkId);
        if (!"PENDING".equals(stockCheck.getStatus()) && !"IN_PROGRESS".equals(stockCheck.getStatus())) {
            throw new RuntimeException("当前盘点单状态不允许修改");
        }
        
        // 如果状态是待盘点，更新为盘点中
        if ("PENDING".equals(stockCheck.getStatus())) {
            stockCheck.setStatus("IN_PROGRESS");
            stockCheckMapper.updateById(stockCheck);
        }
        
        // 更新盘点明细
        StockCheckItem item = stockCheckItemMapper.selectOne(
            new QueryWrapper<StockCheckItem>()
                .eq("check_id", checkId)
                .eq("product_id", productId)
        );
        
        if (item != null) {
            item.setActualStock(actualStock);
            item.setDiffQuantity(actualStock - item.getSystemStock());
            item.setRemark(remark);
            item.setUpdateTime(LocalDateTime.now());
            stockCheckItemMapper.updateById(item);
            
            // 更新盘点单的差异数量
            Integer diffCount = stockCheckItemMapper.countDiffItems(checkId);
            stockCheck.setDiffCount(diffCount);
            stockCheckMapper.updateById(stockCheck);
        }
    }

    @Override
    @Transactional
    public void completeStockCheck(Long checkId) {
        StockCheck stockCheck = stockCheckMapper.selectById(checkId);
        if (!"IN_PROGRESS".equals(stockCheck.getStatus())) {
            throw new RuntimeException("只有盘点中的盘点单才能完成盘点");
        }
        
        stockCheck.setStatus("COMPLETED");
        stockCheck.setUpdateTime(LocalDateTime.now());
        stockCheckMapper.updateById(stockCheck);
    }

    @Override
    public StockCheckPrepareDTO prepareStockCheck() {
        // 获取所有商品
        List<Product> products = productMapper.findAll();
        
        // 转换为DTO
        List<ProductStockDTO> productStockDTOs = products.stream().map(product -> {
            ProductStockDTO dto = new ProductStockDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setBrand(product.getBrand());
            dto.setModel(product.getModel());
            dto.setStock(product.getStock());
            return dto;
        }).collect(Collectors.toList());
        
        // 构建返回结果
        StockCheckPrepareDTO result = new StockCheckPrepareDTO();
        result.setTotalProducts(products.size());
        result.setProducts(productStockDTOs);
        
        return result;
    }

    private String generateCheckNo() {
        return "SC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
} 