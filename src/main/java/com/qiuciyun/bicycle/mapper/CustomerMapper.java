package com.qiuciyun.bicycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiuciyun.bicycle.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
} 