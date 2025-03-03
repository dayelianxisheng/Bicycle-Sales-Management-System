package com.qiuciyun.spark.controller;

import com.qiuciyun.spark.service.CustomerSparkService;
import com.qiuciyun.spark.service.OrderSparkService;
import com.qiuciyun.spark.service.StockSparkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/spark")
public class SparkController {

    @Autowired
    private StockSparkService stockSparkService;
    
    @Autowired
    private OrderSparkService orderSparkService;
    
    @Autowired
    private CustomerSparkService customerSparkService;

    @GetMapping("/stock/analysis")
    public ResponseEntity<Map<String, Object>> getStockAnalysis() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stockSparkService.getCurrentAnalysis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/analysis")
    public ResponseEntity<Map<String, Object>> getOrderAnalysis() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", orderSparkService.getCurrentAnalysis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/analysis")
    public ResponseEntity<Map<String, Object>> getCustomerAnalysis() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", customerSparkService.getCurrentAnalysis());
        return ResponseEntity.ok(response);
    }
} 