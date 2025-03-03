package com.qiuciyun.bicycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiuciyun.bicycle.common.Result;
import com.qiuciyun.bicycle.entity.Customer;
import com.qiuciyun.bicycle.service.CustomerService;
import com.qiuciyun.bicycle.dto.GenerateCustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Result<Page<Customer>> getCustomerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        Page<Customer> customerPage = customerService.getCustomerList(page, size, name, phone);
        return Result.success(customerPage);
    }

    @GetMapping("/{id}")
    public Result<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        return Result.success(customer);
    }

    @PostMapping
    public Result<String> addCustomer(@RequestBody Customer customer) {
        customerService.save(customer);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<String> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        customerService.updateById(customer);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteCustomer(@PathVariable Long id) {
        customerService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/generate")
    public Result<Integer> generateCustomers(@RequestBody GenerateCustomerDTO generateConfig) {
        int count = customerService.generateCustomers(generateConfig);
        return Result.success(count);
    }
} 