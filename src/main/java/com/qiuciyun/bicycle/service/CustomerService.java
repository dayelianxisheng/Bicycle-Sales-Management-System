package com.qiuciyun.bicycle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiuciyun.bicycle.entity.Customer;
import com.qiuciyun.bicycle.dto.GenerateCustomerDTO;
import com.qiuciyun.bicycle.dto.CustomerAnalysisDTO;

public interface CustomerService extends IService<Customer> {
    Page<Customer> getCustomerList(Integer page, Integer size, String name, String phone);
    
    int generateCustomers(GenerateCustomerDTO generateConfig);

    CustomerAnalysisDTO getCustomerAnalysis(String startDate, String endDate);
} 