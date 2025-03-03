package com.qiuciyun.bicycle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiuciyun.bicycle.common.Result;
import com.qiuciyun.bicycle.entity.Order;
import com.qiuciyun.bicycle.entity.OrderItem;
import com.qiuciyun.bicycle.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        IPage<Order> pageResult = orderService.getOrderPage(page, size, orderNo, 
                                                          customerName, status, 
                                                          startDate, endDate);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageResult.getRecords());
        data.put("total", pageResult.getTotal());
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        List<OrderItem> items = orderService.getOrderItems(id);
        
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        
        return Result.success(data);
    }

    @PutMapping("/{id}/payment")
    public Result<String> updatePaymentStatus(@PathVariable Long id) {
        boolean success = orderService.updateOrderStatus(id, 1);
        return success ? Result.success("支付状态更新成功") : Result.error("支付状态更新失败");
    }

    @PutMapping("/{id}/shipment")
    public Result<String> updateShipmentStatus(@PathVariable Long id) {
        boolean success = orderService.updateOrderStatus(id, 2);
        return success ? Result.success("发货状态更新成功") : Result.error("发货状态更新失败");
    }

    @PutMapping("/{id}/complete")
    public Result<String> completeOrder(@PathVariable Long id) {
        boolean success = orderService.updateOrderStatus(id, 3);
        return success ? Result.success("订单已完成") : Result.error("订单状态更新失败");
    }

    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id) {
        boolean success = orderService.cancelOrder(id);
        return success ? Result.success("订单已取消") : Result.error("订单取消失败");
    }

    @GetMapping("/analysis")
    public Result getOrderAnalysis(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // 如果没有指定时间范围，默认查询全部
        if (timeRange == null) {
            timeRange = "all";
        }
        
        // 根据时间范围类型设置查询范围
        LocalDateTime now = LocalDateTime.now();
        String start = null;
        String end = null;
        
        switch (timeRange) {
            case "month":
                // 本月
                start = now.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                end = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                logger.info("本月时间范围：{} 至 {}", start, end);
                break;
            case "year":
                // 本年
                start = now.withMonth(1).withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                end = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                logger.info("本年时间范围：{} 至 {}", start, end);
                break;
            case "custom":
                // 自定义时间范围
                if (startDate == null || endDate == null) {
                    return Result.error("自定义时间范围必须提供开始和结束日期");
                }
                start = startDate;
                end = endDate;
                logger.info("自定义时间范围：{} 至 {}", start, end);
                break;
            case "all":
            default:
                // 全部时间范围，不设置开始和结束日期
                logger.info("查询全部时间范围");
                break;
        }
        
        try {
            Map<String, Object> analysisData = orderService.getOrderAnalysis(start, end);
            return Result.success(analysisData);
        } catch (Exception e) {
            logger.error("获取订单分析数据失败", e);
            return Result.error("获取订单分析数据失败：" + e.getMessage());
        }
    }
} 