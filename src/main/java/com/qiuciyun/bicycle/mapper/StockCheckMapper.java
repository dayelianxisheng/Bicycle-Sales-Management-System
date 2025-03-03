package com.qiuciyun.bicycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiuciyun.bicycle.entity.StockCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StockCheckMapper extends BaseMapper<StockCheck> {
    @Select({
        "<script>",
        "SELECT * FROM stock_check",
        "<where>",
        "  <if test='status != null'>",
        "    AND status = #{status}",
        "  </if>",
        "  <if test='startTime != null'>",
        "    AND check_time >= #{startTime}",
        "  </if>",
        "  <if test='endTime != null'>",
        "    AND check_time &lt;= #{endTime}",
        "  </if>",
        "</where>",
        "ORDER BY create_time DESC",
        "LIMIT #{offset}, #{size}",
        "</script>"
    })
    List<StockCheck> findPage(@Param("offset") int offset, 
                             @Param("size") int size,
                             @Param("status") String status,
                             @Param("startTime") String startTime,
                             @Param("endTime") String endTime);

    @Select({
        "<script>",
        "SELECT COUNT(*) FROM stock_check",
        "<where>",
        "  <if test='status != null'>",
        "    AND status = #{status}",
        "  </if>",
        "  <if test='startTime != null'>",
        "    AND check_time >= #{startTime}",
        "  </if>",
        "  <if test='endTime != null'>",
        "    AND check_time &lt;= #{endTime}",
        "  </if>",
        "</where>",
        "</script>"
    })
    Long count(@Param("status") String status,
               @Param("startTime") String startTime,
               @Param("endTime") String endTime);
} 