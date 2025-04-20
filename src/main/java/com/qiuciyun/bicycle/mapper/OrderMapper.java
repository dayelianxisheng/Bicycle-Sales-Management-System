package com.qiuciyun.bicycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiuciyun.bicycle.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    IPage<Order> selectOrderPage(Page<Order> page, 
                               @Param("orderNo") String orderNo, 
                               @Param("customerName") String customerName, 
                               @Param("status") Integer status, 
                               @Param("startDate") String startDate, 
                               @Param("endDate") String endDate);
    
    long countOrders(@Param("orderNo") String orderNo, 
                    @Param("customerName") String customerName, 
                    @Param("status") Integer status, 
                    @Param("startDate") String startDate, 
                    @Param("endDate") String endDate);
} 