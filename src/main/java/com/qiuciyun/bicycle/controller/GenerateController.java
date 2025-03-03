package com.qiuciyun.bicycle.controller;

import com.qiuciyun.bicycle.common.Result;
import com.qiuciyun.bicycle.entity.Order;
import com.qiuciyun.bicycle.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/generate")
public class GenerateController {

    private static final Logger logger = LoggerFactory.getLogger(GenerateController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    public Result<List<Order>> generateOrders(@RequestBody Map<String, Object> params) {
        try {
            Integer count = (Integer) params.get("count");
            Integer status = (Integer) params.get("status");
            Integer paymentMethod = (Integer) params.get("paymentMethod");
            String startDate = (String) params.get("startDate");
            String endDate = (String) params.get("endDate");

            if (count == null || count < 1 || count > 100) {
                return Result.error("生成数量必须在1-100之间");
            }

            List<Order> orders = orderService.generateOrders(count, status, paymentMethod, startDate, endDate);
            return Result.success(orders);
        } catch (Exception e) {
            logger.error("生成订单数据失败", e);
            return Result.error("生成订单数据失败：" + e.getMessage());
        }
    }
} 