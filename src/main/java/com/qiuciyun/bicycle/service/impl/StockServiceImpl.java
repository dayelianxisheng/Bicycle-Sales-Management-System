package com.qiuciyun.bicycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiuciyun.bicycle.dto.StockStatsDTO;
import com.qiuciyun.bicycle.dto.StockWarningDTO;
import com.qiuciyun.bicycle.entity.Product;
import com.qiuciyun.bicycle.entity.StockRecord;
import com.qiuciyun.bicycle.mapper.ProductMapper;
import com.qiuciyun.bicycle.mapper.StockRecordMapper;
import com.qiuciyun.bicycle.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    public StockStatsDTO getStockStats() {
        StockStatsDTO stats = new StockStatsDTO();
        
        // 获取商品总数
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        Integer totalProducts = productMapper.selectCount(queryWrapper);
        stats.setTotalProducts(totalProducts);
        
        // 获取预警商品数量
        stats.setWarningCount(productMapper.countWarningProducts());
        
        // 获取本月入库数量
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        stats.setMonthlyIn(stockRecordMapper.countMonthlyIn(startOfMonth));
        
        // 计算库存总金额
        BigDecimal totalAmount = productMapper.calculateTotalStockAmount();
        stats.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        return stats;
    }

    @Override
    public List<StockWarningDTO> getWarningProducts() {
        List<Product> warningProducts = productMapper.selectWarningProducts();
        
        return warningProducts.stream().map(product -> {
            StockWarningDTO dto = new StockWarningDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setBrand(product.getBrand());
            dto.setModel(product.getModel());
            dto.setStock(product.getStock());
            dto.setWarningStock(product.getWarningStock());
            dto.setPrice(product.getPrice());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getWarningProductsPage(int page, int size, String keyword) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 获取分页数据
        List<Product> products = productMapper.findWarningProductsPage(keyword, offset, size);
        
        // 获取总记录数
        Long total = productMapper.countWarningProductsWithKeyword(keyword);
        
        // 转换为DTO
        List<StockWarningDTO> records = products.stream().map(product -> {
            StockWarningDTO dto = new StockWarningDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setBrand(product.getBrand());
            dto.setModel(product.getModel());
            dto.setStock(product.getStock());
            dto.setWarningStock(product.getWarningStock());
            dto.setPrice(product.getPrice());
            return dto;
        }).collect(Collectors.toList());
        
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
    @Transactional
    public void stockIn(Long productId, Integer amount, String remark) {
        // 更新商品库存
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        product.setStock(product.getStock() + amount);
        productMapper.updateById(product);
        
        // 记录入库记录
        StockRecord record = new StockRecord();
        record.setProductId(productId);
        record.setAmount(amount);
        record.setType("IN");
        record.setOperateTime(LocalDateTime.now());
        record.setRemark(remark);
        stockRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void stockOut(Long productId, Integer amount, String reason, String remark) {
        // 更新商品库存
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查库存是否足够
        if (product.getStock() < amount) {
            throw new RuntimeException("库存不足");
        }
        
        product.setStock(product.getStock() - amount);
        productMapper.updateById(product);
        
        // 记录出库记录
        StockRecord record = new StockRecord();
        record.setProductId(productId);
        record.setAmount(amount);
        record.setType("OUT");
        record.setOperateTime(LocalDateTime.now());
        record.setRemark(reason + (remark != null && !remark.isEmpty() ? "，" + remark : ""));
        stockRecordMapper.insert(record);
    }

    @Override
    public Map<String, Object> getStockRecords(int page, int size, String keyword, String type, String startDate, String endDate) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 构建查询条件
        QueryWrapper<StockRecord> queryWrapper = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }
        
        // 添加时间范围条件
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge("operate_time", LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le("operate_time", LocalDateTime.parse(endDate + "T23:59:59"));
        }
        
        // 如果有关键字，需要联表查询商品信息
        if (keyword != null && !keyword.isEmpty()) {
            String searchKeyword = keyword; // 创建一个final的副本
            queryWrapper.exists("SELECT 1 FROM product p WHERE p.id = stock_record.product_id AND " +
                    "(p.name LIKE '%" + searchKeyword + "%' OR p.brand LIKE '%" + searchKeyword + "%' OR p.model LIKE '%" + searchKeyword + "%')");
        }
        
        // 添加排序
        queryWrapper.orderByDesc("operate_time");
        
        // 获取总记录数
        Long total = stockRecordMapper.selectCount(queryWrapper);
        
        // 获取分页数据
        queryWrapper.last("LIMIT " + offset + "," + size);
        List<StockRecord> records = stockRecordMapper.selectList(queryWrapper);
        
        // 获取关联的商品信息
        List<Long> productIds = records.stream()
                .map(StockRecord::getProductId)
                .collect(Collectors.toList());
        
        // 创建一个final的商品映射
        final Map<Long, Product> productMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<Product> products = productMapper.selectBatchIds(productIds);
            productMap.putAll(products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p)));
        }
        
        // 构建返回结果
        List<Map<String, Object>> resultRecords = records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", record.getId());
            map.put("amount", record.getAmount());
            map.put("type", record.getType());
            map.put("operateTime", record.getOperateTime());
            map.put("remark", record.getRemark());
            
            // 添加商品信息
            Product product = productMap.get(record.getProductId());
            if (product != null) {
                map.put("productName", product.getName());
                map.put("brand", product.getBrand());
                map.put("model", product.getModel());
            }
            
            return map;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", resultRecords);
        result.put("total", total);
        result.put("size", size);
        result.put("current", page);
        result.put("pages", (total + size - 1) / size);
        
        return result;
    }
} 