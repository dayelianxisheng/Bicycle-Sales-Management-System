package com.qiuciyun.bicycle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiuciyun.bicycle.entity.Order;
import com.qiuciyun.bicycle.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    // 分页查询订单列表
    IPage<Order> getOrderPage(Integer page, Integer size, String orderNo, 
                            String customerName, Integer status, 
                            String startDate, String endDate);
    
    // 获取订单详情
    Order getOrderDetail(Long orderId);
    
    // 获取订单项列表
    List<OrderItem> getOrderItems(Long orderId);
    
    // 更新订单状态
    boolean updateOrderStatus(Long orderId, Integer status);
    
    // 取消订单
    boolean cancelOrder(Long orderId);

    // 生成订单数据
    List<Order> generateOrders(Integer count, Integer status, Integer paymentMethod, 
                             String startDate, String endDate);

    Map<String, Object> getOrderAnalysis(String startDate, String endDate);
} 