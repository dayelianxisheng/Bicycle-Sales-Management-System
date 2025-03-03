package com.qiuciyun.bicycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiuciyun.bicycle.entity.Customer;
import com.qiuciyun.bicycle.entity.Order;
import com.qiuciyun.bicycle.mapper.CustomerMapper;
import com.qiuciyun.bicycle.mapper.OrderMapper;
import com.qiuciyun.bicycle.service.CustomerService;
import com.qiuciyun.bicycle.dto.GenerateCustomerDTO;
import com.qiuciyun.bicycle.dto.CustomerAnalysisDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Page<Customer> getCustomerList(Integer page, Integer size, String name, String phone) {
        Page<Customer> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(Customer::getName, name);
        }
        if (StringUtils.isNotBlank(phone)) {
            wrapper.like(Customer::getPhone, phone);
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(Customer::getCreatedAt);
        
        return page(pageInfo, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateCustomers(GenerateCustomerDTO config) {
        List<Customer> customers = new ArrayList<>();
        Random random = new Random();

        // 姓氏列表
        String[] surnames = {"张", "李", "王", "赵", "刘", "陈", "杨", "黄", "周", "吴", 
                           "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"};
        
        // 名字列表
        String[] givenNames = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
                             "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰", "霞"};
        
        // 生成客户数据
        for (int i = 0; i < config.getCount(); i++) {
            Customer customer = new Customer();
            
            // 生成姓名
            String surname = surnames[random.nextInt(surnames.length)];
            String givenName = givenNames[random.nextInt(givenNames.length)];
            customer.setName(surname + givenName);
            
            // 生成手机号
            String phone = "1" + (random.nextInt(7) + 3) + RandomStringUtils.randomNumeric(9);
            customer.setPhone(phone);
            
            // 生成邮箱
            String email = RandomStringUtils.randomAlphanumeric(8).toLowerCase() + "@example.com";
            customer.setEmail(email);
            
            // 随机选择地区和生成地址
            String region = config.getRegions().get(random.nextInt(config.getRegions().size()));
            String[] streets = {"人民路", "解放路", "建设路", "和平路", "中山路", "文化路"};
            String street = streets[random.nextInt(streets.length)];
            String address = region + "市" + street + random.nextInt(200) + "号";
            customer.setAddress(address);
            
            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            customer.setCreatedAt(now);
            customer.setUpdatedAt(now);
            
            customers.add(customer);
        }
        
        // 批量保存客户数据
        saveBatch(customers);
        
        return customers.size();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAnalysisDTO getCustomerAnalysis(String startDate, String endDate) {
        CustomerAnalysisDTO analysisDTO = new CustomerAnalysisDTO();
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 解析日期范围
            LocalDateTime start = startDate != null ? 
                LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
                LocalDateTime.now().minusMonths(1);
            LocalDateTime end = endDate != null ? 
                LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
                LocalDateTime.now();

            // 1. 获取总客户数
            Long totalCustomers = count();
            stats.put("totalCustomers", totalCustomers);

            // 2. 获取新增客户数
            LambdaQueryWrapper<Customer> newCustomerWrapper = new LambdaQueryWrapper<>();
            newCustomerWrapper.between(Customer::getCreatedAt, start, end);
            Long newCustomers = count(newCustomerWrapper);
            stats.put("newCustomers", newCustomers);

            // 3. 计算客单价和活跃用户
            LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.between(Order::getCreatedAt, start, end)
                       .eq(Order::getStatus, 3); // 只统计已完成的订单
            List<Order> orders = orderMapper.selectList(orderWrapper);
            
            // 初始化统计数据
            stats.put("avgCustomerValue", 0L);
            stats.put("activeRate", 0.0);
            stats.put("orderDistribution", new long[]{totalCustomers, 0, 0, 0}); // 默认所有客户都是0-1单
            Map<String, Long> dailyActiveUsers = new HashMap<>();
            
            if (!orders.isEmpty()) {
                // 计算客单价
                BigDecimal totalAmount = orders.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal avgCustomerValue = totalAmount
                        .divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100));
                    stats.put("avgCustomerValue", avgCustomerValue.longValue());
                }

                // 计算活跃率
                Set<Long> activeCustomers = orders.stream()
                    .map(Order::getCustomerId)
                    .collect(Collectors.toSet());
                double activeRate = totalCustomers > 0 ? 
                    (activeCustomers.size() * 100.0 / totalCustomers) : 0;
                stats.put("activeRate", activeRate);

                // 统计订单分布
                Map<Long, Long> customerOrderCounts = orders.stream()
                    .collect(Collectors.groupingBy(Order::getCustomerId, Collectors.counting()));
                
                long[] orderDistribution = new long[4];
                orderDistribution[0] = totalCustomers - customerOrderCounts.size(); // 无订单的客户数
                customerOrderCounts.values().forEach(count -> {
                    if (count == 1) orderDistribution[0]++;
                    else if (count <= 5) orderDistribution[1]++;
                    else if (count <= 10) orderDistribution[2]++;
                    else orderDistribution[3]++;
                });
                stats.put("orderDistribution", orderDistribution);

                // 统计每日活跃用户
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dailyActiveUsers = orders.stream()
                    .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().format(dateFormatter),
                        Collectors.mapping(Order::getCustomerId, 
                            Collectors.collectingAndThen(
                                Collectors.toSet(),
                                set -> Long.valueOf(set.size())
                            ))
                    ));
            }
            stats.put("dailyActiveUsers", dailyActiveUsers);

            // 5. 统计每日注册人数
            Map<String, Long> dailyRegistrations = new HashMap<>();
            List<Customer> newCustomerList = list(newCustomerWrapper);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            newCustomerList.forEach(customer -> {
                String dateStr = customer.getCreatedAt().format(dateFormatter);
                dailyRegistrations.merge(dateStr, 1L, Long::sum);
            });
            stats.put("dailyRegistrations", dailyRegistrations);

            // 7. 统计地域分布
            Map<String, Long> regionCounts = new HashMap<>();
            list().forEach(customer -> {
                String region = extractRegion(customer.getAddress());
                regionCounts.merge(region, 1L, Long::sum);
            });
            
            List<Map<String, Object>> regionDistribution = regionCounts.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> region = new HashMap<>();
                    region.put("name", entry.getKey());
                    region.put("value", entry.getValue());
                    return region;
                })
                .collect(Collectors.toList());
            stats.put("regionDistribution", regionDistribution);

        } catch (Exception e) {
            log.error("计算客户分析数据失败", e);
            throw new RuntimeException("计算客户分析数据失败", e);
        }

        analysisDTO.setStats(stats);
        return analysisDTO;
    }

    // 从地址中提取地区信息
    private String extractRegion(String address) {
        if (StringUtils.isBlank(address)) {
            return "未知";
        }
        // 假设地址格式为"XX市XX路XX号"
        int cityIndex = address.indexOf("市");
        if (cityIndex > 0) {
            return address.substring(0, cityIndex);
        }
        return "其他";
    }
} 