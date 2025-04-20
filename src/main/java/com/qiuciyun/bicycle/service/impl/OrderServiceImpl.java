package com.qiuciyun.bicycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiuciyun.bicycle.entity.Order;
import com.qiuciyun.bicycle.entity.OrderItem;
import com.qiuciyun.bicycle.entity.Customer;
import com.qiuciyun.bicycle.entity.Product;
import com.qiuciyun.bicycle.mapper.OrderMapper;
import com.qiuciyun.bicycle.mapper.OrderItemMapper;
import com.qiuciyun.bicycle.mapper.CustomerMapper;
import com.qiuciyun.bicycle.mapper.ProductMapper;
import com.qiuciyun.bicycle.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public IPage<Order> getOrderPage(Integer page, Integer size, String orderNo, 
                                   String customerName, Integer status, 
                                   String startDate, String endDate) {
        try {
            // 创建分页对象
            Page<Order> pageInfo = new Page<>(page, size);
            
            // 获取分页数据
            IPage<Order> result = baseMapper.selectOrderPage(pageInfo, orderNo, customerName, status, startDate, endDate);
            
            // 如果结果为空，返回空页
            if (result == null) {
                pageInfo.setTotal(0);
                return pageInfo;
            }
            
            return result;
        } catch (Exception e) {
            logger.error("获取订单列表失败", e);
            Page<Order> emptyPage = new Page<>(page, size);
            emptyPage.setTotal(0);
            return emptyPage;
        }
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        return getById(orderId);
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        
        // 检查状态变更是否合法
        if (!isValidStatusChange(order.getStatus(), status)) {
            return false;
        }

        order.setStatus(status);
        if (status == 1) { // 已支付
            order.setPaymentTime(LocalDateTime.now());
        }
        
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null || order.getStatus() != 0) { // 只能取消待支付的订单
            return false;
        }

        order.setStatus(4); // 设置为已取消
        return updateById(order);
    }

    // 检查订单状态变更是否合法
    private boolean isValidStatusChange(Integer currentStatus, Integer newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        
        // 定义合法的状态变更
        switch (currentStatus) {
            case 0: // 待支付
                return newStatus == 1 || newStatus == 4; // 可变更为已支付或已取消
            case 1: // 已支付
                return newStatus == 2; // 可变更为已发货
            case 2: // 已发货
                return newStatus == 3; // 可变更为已完成
            default:
                return false; // 其他状态不可变更
        }
    }

    @Override
    @Transactional
    public List<Order> generateOrders(Integer count, Integer status, Integer paymentMethod, 
                                    String startDate, String endDate) {
        // 获取所有客户和产品
        List<Customer> customers = customerMapper.selectList(null);
        List<Product> products = productMapper.selectList(null);

        if (customers.isEmpty() || products.isEmpty()) {
            throw new RuntimeException("没有可用的客户或产品数据");
        }

        // 解析日期范围
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? 
            LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now();

        List<Order> generatedOrders = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            // 随机选择客户和创建时间
            Customer customer = customers.get(random.nextInt(customers.size()));
            LocalDateTime orderTime = randomDateTime(start, end);

            // 创建订单
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setCustomerId(customer.getId());
            order.setCustomerName(customer.getName());
            
            // 设置订单状态
            order.setStatus(status != null && status != -1 ? status : random.nextInt(5));
            
            // 设置支付方式
            order.setPaymentMethod(paymentMethod != null && paymentMethod != -1 ? 
                                 paymentMethod : random.nextInt(3) + 1);

            // 设置收货信息
            order.setShippingName(customer.getName());
            order.setShippingPhone(customer.getPhone());
            order.setShippingAddress(customer.getAddress());

            // 生成订单项
            int itemCount = random.nextInt(3) + 1; // 1-3个商品
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();

            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(random.nextInt(products.size()));
                int quantity = random.nextInt(3) + 1; // 1-3件
                
                // 检查并更新库存
                if (product.getStock() < quantity) {
                    throw new RuntimeException("商品 " + product.getName() + " 库存不足");
                }
                product.setStock(product.getStock() - quantity);
                productMapper.updateById(product);
                
                OrderItem item = new OrderItem();
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductPrice(product.getPrice());
                item.setQuantity(quantity);
                item.setSubtotal(product.getPrice().multiply(new BigDecimal(quantity)));
                item.setCreatedAt(orderTime);  // 设置创建时间
                
                totalAmount = totalAmount.add(item.getSubtotal());
                orderItems.add(item);
            }

            order.setTotalAmount(totalAmount);
            order.setCreatedAt(orderTime);
            order.setUpdatedAt(orderTime);
            
            // 如果订单状态为已支付或更高状态，设置支付时间
            if (order.getStatus() >= 1) {
                order.setPaymentTime(orderTime.plusMinutes(random.nextInt(60)));
            }

            // 保存订单和订单项
            save(order);
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getId());
                orderItemMapper.insert(item);
            }

            generatedOrders.add(order);
        }

        return generatedOrders;
    }

    // 生成订单号
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + 
               String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    // 生成随机时间
    private LocalDateTime randomDateTime(LocalDateTime start, LocalDateTime end) {
        long startEpochSecond = start.toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpochSecond = end.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpochSecond = ThreadLocalRandom.current()
            .nextLong(startEpochSecond, endEpochSecond + 1);
        return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, java.time.ZoneOffset.UTC);
    }

    @Override
    public Map<String, Object> getOrderAnalysis(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            logger.info("接收到查询参数：startDate={}, endDate={}", startDate, endDate);
            
            // 构建查询条件
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            
            // 如果提供了时间范围，则添加时间条件
            if (startDate != null && endDate != null) {
                // 解析时间
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
                LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);
                
                logger.info("转换后的时间范围：start={}, end={}", start, end);
                
                // 使用明确的时间比较
                wrapper.apply("date_format(created_at, '%Y-%m-%d') >= date_format({0}, '%Y-%m-%d')", start)
                       .apply("date_format(created_at, '%Y-%m-%d') <= date_format({0}, '%Y-%m-%d')", end);
                
                logger.info("构建的SQL条件：date_format(created_at, '%Y-%m-%d') >= '{}' AND date_format(created_at, '%Y-%m-%d') <= '{}'",
                    startDate, endDate);
            } else {
                logger.warn("未提供时间范围参数，将返回所有订单数据");
            }
            
            // 按创建时间升序排序
            wrapper.orderByAsc(Order::getCreatedAt);
            
            // 查询订单数据
            List<Order> orders = list(wrapper);
            logger.info("查询到订单数量：{}", orders.size());
            
            if (!orders.isEmpty()) {
                logger.info("第一条订单时间：{}", orders.get(0).getCreatedAt());
                logger.info("最后一条订单时间：{}", orders.get(orders.size() - 1).getCreatedAt());
                
                // 验证每条订单的时间是否在范围内
                orders.forEach(order -> {
                    if (order.getCreatedAt() != null) {
                        String orderDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        logger.debug("订单日期：{}", orderDate);
                    }
                });
            }
            
            // 计算统计数据
            Map<String, Object> stats = calculateStats(orders);
            result.put("stats", stats);
            
            return result;
        } catch (Exception e) {
            logger.error("订单分析数据处理失败", e);
            result.put("stats", initEmptyStats());
            return result;
        }
    }

    // 初始化空的统计数据
    private Map<String, Object> initEmptyStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", 0);
        stats.put("totalAmount", 0);
        stats.put("avgOrderAmount", 0);
        stats.put("statusCounts", new int[5]);
        stats.put("paymentCounts", new int[4]);
        stats.put("dailyOrderCounts", new TreeMap<>());
        stats.put("dailySales", new TreeMap<>());
        return stats;
    }

    // 计算统计数据的辅助方法
    private Map<String, Object> calculateStats(List<Order> orders) {
        Map<String, Object> stats = new HashMap<>();
        
        // 基础统计
        int totalOrders = orders.size();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int[] statusCounts = new int[5];  // 0-待支付，1-已支付，2-已发货，3-已完成，4-已取消
        int[] paymentCounts = new int[4];  // 0-未支付，1-微信，2-支付宝，3-银行卡
        
        // 按日期统计
        Map<String, Integer> dailyOrderCounts = new TreeMap<>();
        Map<String, BigDecimal> dailySales = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Order order : orders) {
            // 累加总金额
            if (order.getTotalAmount() != null) {
                totalAmount = totalAmount.add(order.getTotalAmount());
            }
            
            // 统计订单状态
            if (order.getStatus() != null && order.getStatus() >= 0 && order.getStatus() < 5) {
                statusCounts[order.getStatus()]++;
            }
            
            // 统计支付方式
            if (order.getPaymentMethod() != null && order.getPaymentMethod() > 0 && order.getPaymentMethod() <= 3) {
                paymentCounts[order.getPaymentMethod()]++;
            }
            
            // 按日期统计
            if (order.getCreatedAt() != null) {
                String dateStr = order.getCreatedAt().format(formatter);
                dailyOrderCounts.merge(dateStr, 1, Integer::sum);
                if (order.getTotalAmount() != null) {
                    dailySales.merge(dateStr, order.getTotalAmount(), BigDecimal::add);
                }
            }
        }

        // 组装统计数据
        stats.put("totalOrders", totalOrders);
        stats.put("totalAmount", totalAmount.multiply(new BigDecimal(100))); // 转换为分
        stats.put("avgOrderAmount", totalOrders > 0 ? 
            totalAmount.multiply(new BigDecimal(100)).divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO);
        stats.put("statusCounts", statusCounts);
        stats.put("paymentCounts", paymentCounts);
        stats.put("dailyOrderCounts", dailyOrderCounts);
        stats.put("dailySales", dailySales.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().multiply(new BigDecimal(100))
            )));

        return stats;
    }
} 