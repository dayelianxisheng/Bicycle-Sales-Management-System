package com.qiuciyun.bicycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiuciyun.bicycle.entity.StockRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface StockRecordMapper extends BaseMapper<StockRecord> {
    
    @Select("SELECT COUNT(*) FROM stock_record WHERE type = 'IN' AND operate_time >= #{startTime}")
    Integer countMonthlyIn(LocalDateTime startTime);
} 