package com.qiuciyun.bicycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiuciyun.bicycle.entity.StockCheckItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StockCheckItemMapper extends BaseMapper<StockCheckItem> {
    @Select("SELECT * FROM stock_check_item WHERE check_id = #{checkId}")
    List<StockCheckItem> findByCheckId(@Param("checkId") Long checkId);

    @Select("SELECT COUNT(*) FROM stock_check_item WHERE check_id = #{checkId} AND diff_quantity != 0")
    Integer countDiffItems(@Param("checkId") Long checkId);
} 