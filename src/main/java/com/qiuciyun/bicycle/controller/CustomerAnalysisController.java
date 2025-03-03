package com.qiuciyun.bicycle.controller;

import com.qiuciyun.bicycle.common.Result;
import com.qiuciyun.bicycle.dto.CustomerAnalysisDTO;
import com.qiuciyun.bicycle.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/analysis")
public class CustomerAnalysisController {
    
    private static final Logger log = LoggerFactory.getLogger(CustomerAnalysisController.class);
    
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Result<CustomerAnalysisDTO> getAnalysis(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            CustomerAnalysisDTO analysis = customerService.getCustomerAnalysis(startDate, endDate);
            return Result.success(analysis);
        } catch (Exception e) {
            log.error("获取客户分析数据失败", e);
            return Result.error("获取客户分析数据失败：" + e.getMessage());
        }
    }
} 